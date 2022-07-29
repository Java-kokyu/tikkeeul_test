package com.hanghae0705.sbmoney.controller.item;

import com.hanghae0705.sbmoney.data.Message;
import com.hanghae0705.sbmoney.exception.ItemException;
import com.hanghae0705.sbmoney.model.domain.item.SavedItem;
import com.hanghae0705.sbmoney.model.domain.user.User;
import com.hanghae0705.sbmoney.repository.item.SavedItemRepository;
import com.hanghae0705.sbmoney.service.CommonService;
import com.hanghae0705.sbmoney.service.item.SavedItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class SavedItemController {
    private final SavedItemService savedItemService;
    private final SavedItemRepository savedItemRepository;
    private final CommonService commonService;

    @GetMapping("/api/savedItem/{goalItemId}")
    private ResponseEntity<Message> getSavedItem(@PathVariable Long goalItemId) throws ItemException {
        User user = commonService.getUser();
        Message message = savedItemService.getSavedItems(goalItemId, user);
        return ResponseEntity.ok(message);
    }

    @PostMapping("/api/savedItem")
    private ResponseEntity<Message> postSavedItem(@RequestBody SavedItem.Request savedItemRequest) throws ItemException {
        User user = commonService.getUser();
        Message message = savedItemService.postSavedItem(savedItemRequest, user);
        return ResponseEntity.ok(message);
    }

    @PutMapping("/api/savedItem/{savedItemId}")
    private ResponseEntity<Message> updateSavedItem(@PathVariable Long savedItemId, @RequestBody SavedItem.Update price) throws ItemException {
        User user = commonService.getUser();
        Message message = savedItemService.updateSavedItem(savedItemId, price, user);
        return ResponseEntity.ok(message);
    }

    @DeleteMapping("/api/savedItem/{savedItemId}")
    private ResponseEntity<Message> deleteSavedItem(@PathVariable Long savedItemId) throws ItemException {
        User user = commonService.getUser();
        Message message = savedItemService.deleteSavedItem(savedItemId, user);
        return ResponseEntity.ok(message);
    }
}