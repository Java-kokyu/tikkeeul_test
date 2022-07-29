package com.hanghae0705.sbmoney.validator;

import com.hanghae0705.sbmoney.exception.Constants;
import com.hanghae0705.sbmoney.exception.ItemException;
import com.hanghae0705.sbmoney.model.domain.item.GoalItem;
import com.hanghae0705.sbmoney.model.domain.item.Item;
import com.hanghae0705.sbmoney.model.domain.item.SavedItem;
import com.hanghae0705.sbmoney.model.domain.user.Favorite;
import com.hanghae0705.sbmoney.model.domain.user.User;
import com.hanghae0705.sbmoney.repository.item.GoalItemRepository;
import com.hanghae0705.sbmoney.repository.item.ItemRepository;
import com.hanghae0705.sbmoney.repository.item.SavedItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ItemValidator {
    private final GoalItemRepository goalItemRepository;
    private final SavedItemRepository savedItemRepository;
    private final ItemRepository itemRepository;

    public Favorite.SavedItemResponse isFavoriteItem(List<Favorite> favorites, Item item, int price){
        Favorite.SavedItemResponse savedItemResponse = new Favorite.SavedItemResponse();
        for(Favorite favorite : favorites){
            if(favorite.getItem().equals(item) && favorite.getPrice() == price){
                savedItemResponse.setFavorite(true);
                savedItemResponse.setId(favorite.getId());
                return savedItemResponse;
            }
        }
        savedItemResponse.setFavorite(false);
        savedItemResponse.setId(null);
        return savedItemResponse;
    }

    public GoalItem isValidGoalItem(Long goalItemId, User user) throws ItemException {
        GoalItem goalItem = goalItemRepository.findById(goalItemId).orElseThrow(
                () -> new ItemException(Constants.ExceptionClass.GOAL_ITEM, HttpStatus.BAD_REQUEST, "존재하지 않는 태산입니다.")
        );
        if(!goalItem.getUser().getId().equals(user.getId())){
            throw new ItemException(Constants.ExceptionClass.GOAL_ITEM, HttpStatus.BAD_REQUEST, "태산과 유저정보가 일치하지 않습니다");
        }
        return goalItem;
    }

    public SavedItem isValidSavedItem(Long savedItemId, User user) throws ItemException {
        SavedItem savedItem = savedItemRepository.findById(savedItemId).orElseThrow(
                () -> new ItemException(Constants.ExceptionClass.SAVED_ITEM, HttpStatus.BAD_REQUEST, "존재하지 않는 티끌입니다.")
        );
        if(!user.equals(savedItem.getUser())){
            throw new ItemException(Constants.ExceptionClass.SAVED_ITEM, HttpStatus.BAD_REQUEST, "유저 아이디가 일치하지 않습니다.");
        }
        return savedItem;
    }

    public void isExistItem(String itemName) throws ItemException {
        if(itemRepository.findByName(itemName).isPresent()){
            throw new ItemException(Constants.ExceptionClass.ITEM, HttpStatus.BAD_REQUEST, "이미 존재하는 아이템입니다.");
        }
    }

    public void isReachedGoalItem(double goalPercent) throws ItemException {
        if(goalPercent >= 100.0){
            throw new ItemException(Constants.ExceptionClass.SAVED_ITEM, HttpStatus.BAD_REQUEST, "달성율을 초과한 태산입니다.");
        }
    }

    public Item isValidCategoryAndItem(Long categoryId, Long itemId) throws ItemException {
        if(itemId == null){
            throw new ItemException(Constants.ExceptionClass.GOAL_ITEM, HttpStatus.BAD_REQUEST,"존재하지 않는 물건입니다.");
        }
        return itemRepository.findByCategoryIdAndId(categoryId, itemId).orElseThrow(
                () -> new ItemException(Constants.ExceptionClass.GOAL_ITEM, HttpStatus.BAD_REQUEST,"존재하지 않는 물건입니다.")
        );
    }

    public Item isValidItem(Long itemId) throws ItemException {
        if(itemId == null){
            throw new ItemException(Constants.ExceptionClass.GOAL_ITEM, HttpStatus.BAD_REQUEST,"존재하지 않는 물건입니다.");
        }
        return itemRepository.findById(itemId).orElseThrow(
                () -> new ItemException(Constants.ExceptionClass.GOAL_ITEM, HttpStatus.BAD_REQUEST,"존재하지 않는 물건입니다.")
        );
    }
}
