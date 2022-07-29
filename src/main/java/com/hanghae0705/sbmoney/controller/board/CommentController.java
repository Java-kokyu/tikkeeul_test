package com.hanghae0705.sbmoney.controller.board;

import com.hanghae0705.sbmoney.data.Message;
import com.hanghae0705.sbmoney.model.domain.board.Comment;
import com.hanghae0705.sbmoney.service.board.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/board")
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/{boardId}/comment")
    public ResponseEntity<Message> getCommentList(@PathVariable Long boardId) {
        Message message = commentService.getCommentList(boardId);
        return ResponseEntity.ok(message);
    }

    @PostMapping("/{boardId}/comment")
    public ResponseEntity<Message> postComment(@PathVariable Long boardId, @RequestBody Comment.Request request) {
        Message message = commentService.postComment(boardId, request);
        return ResponseEntity.ok(message);
    }

    @PutMapping("/{boardId}/comment/{commentId}")
    public ResponseEntity<Message> updateComment(@PathVariable Long boardId, @PathVariable Long commentId, @RequestBody Comment.Request request) {
        Message message = commentService.updateComment(request, commentId, boardId);
        return ResponseEntity.ok(message);
    }

    @DeleteMapping("/{boardId}/comment/{commentId}")
    public ResponseEntity<Message> deleteComment(@PathVariable Long boardId, @PathVariable Long commentId) {
        Message message = commentService.deleteComment(boardId, commentId);
        return ResponseEntity.ok(message);
    }
}
