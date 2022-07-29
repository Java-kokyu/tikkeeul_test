package com.hanghae0705.sbmoney.controller.board;

import com.hanghae0705.sbmoney.data.Message;
import com.hanghae0705.sbmoney.service.board.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/api/board/{boardId}")
    public ResponseEntity<Message> changeLike(@PathVariable Long boardId, @RequestHeader("Authorization") String authorization) {
        Message message = likeService.changeLike(boardId, authorization);
        return ResponseEntity.ok(message);
    }
}
