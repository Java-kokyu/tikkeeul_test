package com.hanghae0705.sbmoney.service.board;

import com.hanghae0705.sbmoney.data.Message;
import com.hanghae0705.sbmoney.exception.ApiException;
import com.hanghae0705.sbmoney.exception.ApiRequestException;
import com.hanghae0705.sbmoney.model.domain.board.Board;
import com.hanghae0705.sbmoney.model.domain.board.Comment;
import com.hanghae0705.sbmoney.model.domain.user.User;
import com.hanghae0705.sbmoney.repository.board.BoardRepository;
import com.hanghae0705.sbmoney.repository.board.CommentRepository;
import com.hanghae0705.sbmoney.repository.user.UserRepository;
import com.hanghae0705.sbmoney.security.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CommentService {
    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    private String errorMsg;

    public CommentService(CommentRepository commentRepository, BoardRepository boardRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.boardRepository = boardRepository;
        this.userRepository = userRepository;
    }

    // 댓글 조회
    public Message getCommentList(Long boardId) {
        try {
            getValueByIdFromRepo("board", boardId);
            List<Comment> commentList = commentRepository.findByBoard_Id(boardId);
            List<Comment.Response> responseList = new ArrayList<>();
            for (Comment comment : commentList) {
                Comment.Response response = new Comment.Response(comment);
                responseList.add(response);
            }
            return new Message(true, "댓글을 조회했습니다.", responseList);
        } catch (Exception e) {
            return new Message(false, errorMsg);
        }
    }

    @Transactional
    public Message postComment(Long boardId, Comment.Request request) {
        try {
            checkValueIsEmpty(request.getComment());
            checkCommentLength(request.getComment());
            Comment comment = new Comment(request, (Board) getValueByIdFromRepo("board", boardId), getUser());
            Board board = (Board) getValueByIdFromRepo("board", boardId);
            board.commentCount(board.getCommentCount() + 1);
            commentRepository.save(comment);
            return new Message(true, "댓글을 등록하였습니다.");
        } catch (Exception e) {
            return new Message(false, errorMsg);
        }
    }

    @Transactional
    public Message updateComment(Comment.Request request, Long commentId, Long boardId) {
        try {
            getValueByIdFromRepo("board", boardId);
            Comment comment = (Comment) getValueByIdFromRepo("comment", commentId);
            checkCommentUserAndCurrentUser(comment);
            checkValueIsEmpty(request.getComment());
            checkCommentLength(request.getComment());
            comment.updateComment(request);
            return new Message(true, "댓글을 수정하였습니다.");
        } catch (Exception e) {
            return new Message(false, errorMsg);
        }
    }

    @Transactional
    public Message deleteComment(Long boardId, Long commentId) {
        try {
            getValueByIdFromRepo("board", boardId);
            checkCommentUserAndCurrentUser((Comment) getValueByIdFromRepo("comment", commentId));
            commentRepository.deleteById(commentId);
            Board board = (Board) getValueByIdFromRepo("board", boardId);
            board.commentCount(board.getCommentCount() -1);
            return new Message(true, "댓글을 삭제하였습니다.");
        } catch (Exception e) {
            return new Message(false, errorMsg);
        }
    }

//    @Transactional
//    public Comment test(Long boardId, User user, Comment.Request request) {
//        Comment comment = new Comment(request, (Board) getValueByIdFromRepo("board", boardId), user);
//        commentRepository.save(comment);
//        return comment;
//    }


    private User getUser() {
        ApiRequestException e = new ApiRequestException(ApiException.NOT_MATCH_USER);
        errorMsg = e.getMessage();
        return userRepository.findByUsername(SecurityUtil.getCurrentUsername()).orElseThrow(() -> e);
    }

    private void getErrMsg(Exception e) {
        log.info(e.getMessage());
        errorMsg = e.getMessage();
    }

    private void checkCommentLength(String target) {
        if (target.length() > 200) {
            IllegalArgumentException e = new IllegalArgumentException("댓글은 200자 이내로 작성해야합니다.");
            getErrMsg(e);
            throw e;
        }
    }

    private void checkCommentUserAndCurrentUser(Comment comment) {
        Comment target = (Comment) getValueByIdFromRepo("comment", comment.getId());
        if (!target.getUser().equals(getUser())) {
            ApiRequestException e = new ApiRequestException(ApiException.NOT_MATCH_USER);
            getErrMsg(e);
            throw e;
        }
    }

    private void checkValueIsEmpty(String target) {
        if (target.trim().isEmpty()) {
            NullPointerException e = new NullPointerException("내용이 없습니다.");
            getErrMsg(e);
            throw e;
        }
    }

    // AOP를 적용하면 얘를 다른 곳에서 쓸 수 있게 한다 이건데...
    private Object getValueByIdFromRepo(String repo, Long id) {
        ApiRequestException e = new ApiRequestException(ApiException.NOT_EXIST_DATA);
        errorMsg = e.getMessage();
        switch (repo) {
            case "comment":
                return commentRepository.findById(id).orElseThrow(() -> e);
            case "board":
                return boardRepository.findById(id).orElseThrow(() -> e);
        }
        return false;
    }


}