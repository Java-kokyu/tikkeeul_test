package com.hanghae0705.sbmoney.controller.item;

import com.hanghae0705.sbmoney.data.Message;
import com.hanghae0705.sbmoney.exception.ItemException;
import com.hanghae0705.sbmoney.model.domain.item.GoalItem;
import com.hanghae0705.sbmoney.model.domain.user.User;
import com.hanghae0705.sbmoney.service.CommonService;
import com.hanghae0705.sbmoney.service.item.GoalItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class GoalItemController {
    private final GoalItemService goalItemService;
    private final CommonService commonService;

    @GetMapping("/api/goalItem")
    public ResponseEntity<Message> getGoalItem() throws ItemException {
        User user = commonService.getUser();
        Message message = goalItemService.getGoalItem(user);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/api/mypage/history")
    public ResponseEntity<Message> getHistory() {
        User user = commonService.getUser();
        Message message = goalItemService.getHistory(user);
        return ResponseEntity.ok(message);
    }

    @PostMapping("/api/goalItem")
    public ResponseEntity<Message> postGoalItem(@RequestPart(value = "image", required = false) MultipartFile multipartFile,
                                                @RequestPart(value = "goalItem") GoalItem.Request goalItemRequest) throws ItemException, IOException {
        User user = commonService.getUser();
        Message message = goalItemService.postGoalItem(goalItemRequest, multipartFile,  user);
        return ResponseEntity.ok(message);
    }

    @PutMapping("/api/goalItem/{goalItemId}")
    public ResponseEntity<Message> updateGoalItem(@PathVariable Long goalItemId,
                                                  @RequestPart(value = "image",required = false) MultipartFile multipartFile,
                                                  @RequestPart(value = "goalItem") GoalItem.Request goalItemRequest) throws ItemException, IOException {
        User user = commonService.getUser();
        Message message = goalItemService.updateGoalItem(goalItemId, goalItemRequest, multipartFile, user);
        return ResponseEntity.ok(message);
    }

    @DeleteMapping("/api/goalItem/{goalItemId}")
    public ResponseEntity<Message> deleteGoalItem(@PathVariable Long goalItemId) throws ItemException {
        User user = commonService.getUser();
        Message message = goalItemService.deleteGoalItem(goalItemId, user);
        return ResponseEntity.ok(message);
    }
}