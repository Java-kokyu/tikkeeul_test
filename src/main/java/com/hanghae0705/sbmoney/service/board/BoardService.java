package com.hanghae0705.sbmoney.service.board;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.hanghae0705.sbmoney.data.Message;
import com.hanghae0705.sbmoney.model.domain.board.Board;
import com.hanghae0705.sbmoney.model.domain.item.GoalItem;
import com.hanghae0705.sbmoney.model.domain.item.SavedItem;
import com.hanghae0705.sbmoney.model.domain.user.User;
import com.hanghae0705.sbmoney.repository.board.BoardRepository;
import com.hanghae0705.sbmoney.repository.item.GoalItemRepository;
import com.hanghae0705.sbmoney.repository.item.SavedItemRepository;
import com.hanghae0705.sbmoney.repository.user.UserRepository;
import com.hanghae0705.sbmoney.service.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.auth0.jwt.JWT.decode;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final LikeService likeService;
    private final GoalItemRepository goalItemRepository;
    private final UserRepository userRepository;
    private final SavedItemRepository savedItemRepository;
    private final S3Uploader s3Uploader;

    private static final int DEFAULT_PAGE_NUM = 0;


    private Optional<User> getUser(String authorization) {
        String token = authorization.substring(7);
        DecodedJWT decodeToken = decode(token);
        String username = decodeToken.getClaim("sub").toString();
        int length = username.length();
        username = username.substring(1, length - 1);
        Optional<User> user = userRepository.findByUsername(username);

        return user;

    }

    @Transactional
    public Message getBoard(Long lastBoardId, int size, String authorization) {
        PageRequest pageRequest = PageRequest.of(DEFAULT_PAGE_NUM, size);
        Page<Board> boardList = boardRepository.findByIdLessThanOrderByIdDesc(lastBoardId, pageRequest);
        List<Board.Response> responseList = new ArrayList<>();
        for (Board board : boardList) {
            if (authorization == null) {
                Long likeCount = likeService.likeCount(board.getId());
                board.likeBoard(false, likeCount);

            } else {
                boolean checkLike = likeService.checkLike(board.getId(), authorization);
                Long likeCount = likeService.likeCount(board.getId());
                board.likeBoard(checkLike, likeCount);
            }

            Board.Response response = new Board.Response(board);
            responseList.add(response);
        }
        return new Message(true, "게시판을 조회하였습니다.", responseList);
    }

    @Transactional
    public Message getDetailBoard(Long boardId, String authorization) {
        if(boardRepository.findById(boardId).isEmpty()){
            return new Message(false, "존재하지 않는 게시글입니다");
        }
        Board board = boardRepository.findAllById(boardId);

        boolean checkLike = false;
        Long likeCount = likeService.likeCount(board.getId());
        if (authorization == null) {
            board.likeBoard(false, likeCount);

        } else {
            checkLike = likeService.checkLike(board.getId(), authorization);
        }

        board.likeBoard(checkLike, likeCount);
        board.viewCount(board.getViewCount() + 1);
        Board.Response response = new Board.Response(board);
        return new Message(true, "게시판을 조회하였습니다.", response);
    }

    @Transactional
    public Message getSaveBoard(Long boardId) {
        if(boardRepository.findById(boardId).isEmpty()){
            return new Message(false, "존재하지 않는 게시글입니다");
        }

        Board board = boardRepository.findAllById(boardId);

        if(goalItemRepository.findById(board.getGoalItemId()).isEmpty()){
            return new Message(false, "존재하지 않는 태산입니다");
        }

        GoalItem goalItem = goalItemRepository.findById(board.getGoalItemId()).orElseThrow(
                () -> new NullPointerException("존재하지 않는 태산입니다"));
        List<SavedItem> savedItemList = savedItemRepository.findAllByGoalItemAndAndCreatedAtIsBefore(goalItem, board.getCreatedDate());
        int length = savedItemList.size();
        int total = 0;

        List<Board.SaveItem> saveItemList = new ArrayList<>();

        for (int i = 0; i < length; i++) {
            total = total + savedItemList.get(i).getPrice();
            Board.SaveItem dto = new Board.SaveItem(savedItemList.get(i));
            saveItemList.add(dto);
        }
        Board.SaveItemResponse response = new Board.SaveItemResponse(board, saveItemList, total);
        return new Message(true, "티끌을 조회하였습니다.", response);
    }


    @Transactional
    public Message postBoard(Board.Request request, String authorization, MultipartFile multipartFile) throws IOException {

        if(request.getContents().length() > 500){
            return new Message(false, "500자 이하로 작성해주세요");
        }

        Optional<User> user = getUser(authorization);

        GoalItem goalItem = goalItemRepository.findByUserAndAndCheckReachedIsFalse(user.get());
        if(goalItem.equals(null)){
            return new Message(false, "태산을 등록 해주세요");
        }

        Board board = new Board(request, goalItem, user);
        if (multipartFile != null) {
            String url = s3Uploader.upload(multipartFile, "static");
            board.changeImage(url);
        }
        boardRepository.save(board);
        return new Message(true, "게시글을 등록하였습니다");
    }

    @Transactional
    public Message putBoard(Board.Update request, Long boardId, String authorization, MultipartFile multipartFile) throws IOException {

        if(request.getContents().length() > 500){
            return new Message(false, "500자 이하로 작성해주세요");
        }

        Optional<User> user = getUser(authorization);

        if(boardRepository.findById(boardId).isEmpty()){
            return new Message(false, "존재하지 않는 게시글입니다");
        }

        Board board = boardRepository.findAllById(boardId);

        if (user.get().getId().equals(board.getUser().getId())) {
            board.updateBoard(request);
            if (multipartFile != null) {
                String url = s3Uploader.upload(multipartFile, "static");
                board.changeImage(url);
            }
            return new Message(true, "게시글을 수정하였습니다");
        } else {
            return new Message(false, "유저 정보를 확인해주세요");
        }

    }

    @Transactional
    public Message deleteBoard(Long boardId, String authorization) {
        Optional<User> user = getUser(authorization);

        if(boardRepository.findById(boardId).isEmpty()){
            return new Message(false, "존재하지 않는 게시글입니다");
        }

        Board board = boardRepository.findAllById(boardId);

        if (user.get().getId().equals(board.getUser().getId())) {
            boardRepository.delete(board);
            return new Message(true, "게시글을 삭제하였습니다");
        } else {
            return new Message(false, "유저 정보를 확인해주세요");
        }
    }


}
