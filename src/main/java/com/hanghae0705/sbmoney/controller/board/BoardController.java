package com.hanghae0705.sbmoney.controller.board;


import com.hanghae0705.sbmoney.data.Message;
import com.hanghae0705.sbmoney.model.domain.board.Board;
import com.hanghae0705.sbmoney.service.board.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@RestController
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;

    @GetMapping("/api/board")
    public ResponseEntity<Message> getBoard(@RequestParam Long lastBoardId, @RequestParam int size,
                                            @RequestHeader(required = false, name = "Authorization") String authorization) {
        Message message = boardService.getBoard(lastBoardId, size, authorization);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/api/board/detail/{boardId}")
    public ResponseEntity<Message> getDetailBoard(@PathVariable Long boardId, @RequestHeader(required = false, name = "Authorization") String authorization) {
        Message message = boardService.getDetailBoard(boardId, authorization);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/api/board/save/{boardId}")
    public ResponseEntity<Message> getSaveBoard(@PathVariable Long boardId) {
        Message message = boardService.getSaveBoard(boardId);
        return ResponseEntity.ok(message);
    }


    @PostMapping("/api/post/board")
    public ResponseEntity<Message> postBoard(@RequestPart(value = "request") Board.Request request,
                                             @RequestHeader("Authorization") String authorization,
                                             @RequestPart(required = false, value = "file") MultipartFile multipartFile) throws IOException {
        Message message = boardService.postBoard(request, authorization, multipartFile);
        return ResponseEntity.ok(message);
    }

    @PutMapping("/api/board/{boardId}")
    public ResponseEntity<Message> putBoard(@RequestPart(value = "request") Board.Update request, @PathVariable Long boardId, @RequestHeader("Authorization") String authorization, @RequestPart(required = false, value = "file") MultipartFile multipartFile) throws IOException {
        Message message = boardService.putBoard(request, boardId, authorization, multipartFile);
        return ResponseEntity.ok(message);
    }

    @DeleteMapping("/api/board/{boardId}")
    public ResponseEntity<Message> deleteBoard(@PathVariable Long boardId, @RequestHeader("Authorization") String authorization) {
        Message message = boardService.deleteBoard(boardId, authorization);
        return ResponseEntity.ok(message);
    }


}
