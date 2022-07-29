package com.hanghae0705.sbmoney.service.board;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.hanghae0705.sbmoney.data.Message;
import com.hanghae0705.sbmoney.model.domain.board.Board;
import com.hanghae0705.sbmoney.model.domain.board.BoardLike;
import com.hanghae0705.sbmoney.model.domain.user.User;
import com.hanghae0705.sbmoney.repository.board.BoardRepository;
import com.hanghae0705.sbmoney.repository.board.LikeRepository;
import com.hanghae0705.sbmoney.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

import static com.auth0.jwt.JWT.decode;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final BoardRepository boardRepository;

    private final UserRepository userRepository;

    private User getUser(String authorization) {
        String token = authorization.substring(7);
        DecodedJWT decodeToken = decode(token);
        String username = decodeToken.getClaim("sub").toString();
        int length = username.length();
        username = username.substring(1, length - 1);
        Optional<User> optionalUser = userRepository.findByUsername(username);
        User user = optionalUser.get();
        return user;

    }

    @Transactional
    public Message changeLike(Long boardId, String authorization) {

        if(boardRepository.findById(boardId).isEmpty()){
            return new Message(false, "존재하지 않는 게시글입니다");
        }

        Board board = boardRepository.findById(boardId).orElseThrow(
                () -> new NullPointerException("존재하지 않는 게시글입니다"));

        User user = getUser(authorization);
        if (likeRepository.findByBoardAndUser(board, user) == null) {
            BoardLike like = new BoardLike(board, user);
            likeRepository.save(like);
        } else {
            BoardLike like = likeRepository.findByBoardAndUser(board, user);
            if (like.isLike() == true) {
                like.changeLike(false);
            } else {
                like.changeLike(true);
            }
        }
        Long likeCount = likeCount(boardId);
        boolean checkLike = checkLike(boardId, authorization);
        board.likeBoard(checkLike, likeCount);
        BoardLike.Response response = new BoardLike.Response(likeCount, checkLike);
        return new Message(true, "좋아요를 변경하였습니다.", response);

    }

    @Transactional
    public boolean checkLike(Long boardId, String authorization) {

        Board board = boardRepository.findById(boardId).orElseThrow(
                () -> new NullPointerException("존재하지 않는 게시글입니다"));
        User user = getUser(authorization);
        if (likeRepository.findByBoardAndUser(board, user) == null) {
            return false;
        } else {
            BoardLike like = likeRepository.findByBoardAndUser(board, user);
            return like.isLike();
        }
    }

    @Transactional
    public Long likeCount(Long boardId) {


        Board board = boardRepository.findById(boardId).orElseThrow(
                () -> new NullPointerException("존재하지 않는 게시글입니다"));

        return (long) likeRepository.findByBoardAndLikeIsTrue(board).size();
    }


}
