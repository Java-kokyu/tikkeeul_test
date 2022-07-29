package com.hanghae0705.sbmoney.service.item;

import com.hanghae0705.sbmoney.data.Message;
import com.hanghae0705.sbmoney.exception.Constants;
import com.hanghae0705.sbmoney.exception.ItemException;
import com.hanghae0705.sbmoney.model.domain.item.Category;
import com.hanghae0705.sbmoney.model.domain.item.GoalItem;
import com.hanghae0705.sbmoney.model.domain.item.Item;
import com.hanghae0705.sbmoney.model.domain.item.SavedItem;
import com.hanghae0705.sbmoney.model.domain.user.User;
import com.hanghae0705.sbmoney.repository.item.CategoryRepository;
import com.hanghae0705.sbmoney.repository.item.ItemRepository;
import com.hanghae0705.sbmoney.validator.ItemValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemValidator itemValidator;
    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;
    private final SavedItemService savedItemService;
    private final GoalItemService goalItemService;
    public Message postNewSavedItem(Item.savedItemRequest itemRequest, User user) throws ItemException {
        itemValidator.isExistItem(itemRequest.getItemName());
        Category category = categoryRepository.findById(itemRequest.getCategoryId()).orElseThrow(
                () -> new ItemException(Constants.ExceptionClass.CATEGORY, HttpStatus.BAD_REQUEST, "존재하지 않는 카테고리입니다.")
        );
        Item item = itemRepository.save(new Item(itemRequest, category));
        
        //아이템 등록 후 티끌 등록
        SavedItem.Request savedItemRequest = new SavedItem.Request(item.getId(), itemRequest.getGoalItemId(), item.getDefaultPrice());
        return savedItemService.postSavedItem(savedItemRequest, user);
    }

    public Message postNewGoalItem(Item.goalItemRequest itemRequest, MultipartFile multipartFile, User user) throws ItemException, IOException {
        itemValidator.isExistItem(itemRequest.getItemName());
        Category category = categoryRepository.findById(itemRequest.getCategoryId()).orElseThrow(
                () -> new ItemException(Constants.ExceptionClass.CATEGORY, HttpStatus.BAD_REQUEST, "존재하지 않는 카테고리입니다.")
        );
        Item item = itemRepository.save(new Item(itemRequest, category));

        //아이템 등록 후 태산 등록
        GoalItem.Request goalItemRequest = new GoalItem.Request(category.getId(), item.getId(), itemRequest.getGoalItemCount(), item.getDefaultPrice());
        return goalItemService.postGoalItem(goalItemRequest, multipartFile, user);
    }

    public Message updateNewGoalItem(Long goalItemId, Item.goalItemRequest itemRequest, MultipartFile multipartFile, User user) throws ItemException, IOException {
        itemValidator.isExistItem(itemRequest.getItemName());
        Category category = categoryRepository.findById(itemRequest.getCategoryId()).orElseThrow(
                () -> new ItemException(Constants.ExceptionClass.CATEGORY, HttpStatus.BAD_REQUEST, "존재하지 않는 카테고리입니다.")
        );
        Item item = itemRepository.save(new Item(itemRequest, category));

        //아이템 등록 후 태산 등록
        GoalItem.Request goalItemRequest = new GoalItem.Request(category.getId(), item.getId(), itemRequest.getGoalItemCount(), item.getDefaultPrice());
        return goalItemService.updateGoalItem(goalItemId, goalItemRequest, multipartFile, user);
    }

    public Message getItems() {
        List<Item> items = itemRepository.findAll();
        List<Item.Response> itemResponses = new ArrayList<>();
        for(Item item : items){
            if(item.getId() != -1L || item.getCategory().getId() != -1L){
                Item.Response itemResponse = new Item.Response(item);
                itemResponses.add(itemResponse);
            }
        }
        return new Message(true, "아이템 전체 조회를 성공했습니다.", itemResponses);
    }
}
