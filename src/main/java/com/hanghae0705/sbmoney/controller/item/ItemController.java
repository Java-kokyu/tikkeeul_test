package com.hanghae0705.sbmoney.controller.item;

import com.hanghae0705.sbmoney.data.Message;
import com.hanghae0705.sbmoney.exception.ItemException;
import com.hanghae0705.sbmoney.model.domain.item.Item;
import com.hanghae0705.sbmoney.model.domain.user.User;
import com.hanghae0705.sbmoney.service.CommonService;
import com.hanghae0705.sbmoney.service.item.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final CommonService commonService;

    @PostMapping("/api/items/savedItem")
    public ResponseEntity<Message> postNewSavedItem(@RequestBody Item.savedItemRequest itemRequest) throws ItemException {
        //바로 티끌에 추가
        User user = commonService.getUser();
        Message message = itemService.postNewSavedItem(itemRequest, user);
        return ResponseEntity.ok(message);
    }

    @PostMapping("/api/items/goalItem")
    public ResponseEntity<Message> postNewGoalItem(@RequestPart(value = "image",required = false) MultipartFile multipartFile,
                                                   @RequestPart(value = "goalItem") Item.goalItemRequest itemRequest) throws ItemException, IOException {
        //바로 티끌에 추가
        User user = commonService.getUser();
        Message message = itemService.postNewGoalItem(itemRequest, multipartFile, user);
        return ResponseEntity.ok(message);
    }

    @PutMapping("/api/items/goalItem/{goalItemId}")
    public ResponseEntity<Message> postNewGoalItem(@PathVariable Long goalItemId,
                                                   @RequestPart(value = "image",required = false) MultipartFile multipartFile,
                                                   @RequestPart(value = "goalItem") Item.goalItemRequest itemRequest) throws ItemException, IOException {
        //바로 티끌에 추가
        User user = commonService.getUser();
        Message message = itemService.updateNewGoalItem(goalItemId, itemRequest, multipartFile, user);
        return ResponseEntity.ok(message);
    }



    @GetMapping("/api/item")
    public ResponseEntity<Message> getItems(){
        Message message = itemService.getItems();
        return ResponseEntity.ok(message);
    }
}
