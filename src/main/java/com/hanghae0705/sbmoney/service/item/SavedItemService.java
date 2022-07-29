package com.hanghae0705.sbmoney.service.item;

import com.hanghae0705.sbmoney.data.Message;
import com.hanghae0705.sbmoney.exception.ItemException;
import com.hanghae0705.sbmoney.model.domain.item.GoalItem;
import com.hanghae0705.sbmoney.model.domain.item.Item;
import com.hanghae0705.sbmoney.model.domain.item.SavedItem;
import com.hanghae0705.sbmoney.model.domain.user.Favorite;
import com.hanghae0705.sbmoney.model.domain.user.User;
import com.hanghae0705.sbmoney.repository.item.FavoriteRepository;
import com.hanghae0705.sbmoney.repository.item.GoalItemRepository;
import com.hanghae0705.sbmoney.repository.item.SavedItemRepository;
import com.hanghae0705.sbmoney.util.MathFloor;
import com.hanghae0705.sbmoney.validator.ItemValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SavedItemService {
    private final SavedItemRepository savedItemRepository;
    private final GoalItemRepository goalItemRepository;
    private final FavoriteRepository favoriteRepository;
    private final ItemValidator itemValidator;

    @Transactional
    public Message postSavedItem(SavedItem.Request savedItemRequest, User user) throws ItemException {
        Item item = itemValidator.isValidItem(savedItemRequest.getItemId());

        int price = (savedItemRequest.getPrice() == 0) ? item.getDefaultPrice() : savedItemRequest.getPrice();

        //GoalItem이 등록되었을 때 (itemId가 -1일 경우 태산 없음)
        GoalItem goalItem = itemValidator.isValidGoalItem(savedItemRequest.getGoalItemId(), user);
        itemValidator.isReachedGoalItem(goalItem.getGoalPercent()); //달성율이 100% 이상이면 등록 불가

        int savedItemTotal = 0;
        for (SavedItem savedItem : goalItem.getSavedItems()) {
            savedItemTotal += savedItem.getPrice();
        }
        int updatePrice = savedItemTotal + price;

        if (goalItem.getItem().getId() != -1L && updatePrice >= goalItem.getTotal()) { // GoalItem이 목표 금액을 달성했을 때
            LocalDateTime reachedAt = LocalDateTime.now();
            goalItem.setCheckReached(true, 100.0, reachedAt);
            savedItemRepository.save(new SavedItem(item, price, user, goalItem));

            Item noItem = itemValidator.isValidItem(-1L); // 목표 없음 카테고리
            GoalItem noGoalItem = new GoalItem(user, 0, 0, noItem);
            goalItemRepository.save(noGoalItem);

        } else if (goalItem.getItem().getId() == -1L) {
            savedItemRepository.save(new SavedItem(item, price, user, goalItem));
        } else{ // GoalItem이 목표 금액을 달성하지 못했을 때
            double decimal = ((double) updatePrice / goalItem.getTotal());
            double updateGoalPercent = MathFloor.PercentTenths(decimal);
            savedItemRepository.save(new SavedItem(item, price, user, goalItem));
            goalItem.setGoalPercent(updateGoalPercent);
        }
        return new Message(true, "티끌 등록에 성공했습니다.");
    }

    public Message getSavedItems(Long goalItemId, User user) throws ItemException {
        GoalItem goalItem = itemValidator.isValidGoalItem(goalItemId, user);

        List<SavedItem> savedItemList = goalItem.getSavedItems();
        List<Favorite> favorites = favoriteRepository.findByUserId(user.getId());
        List<SavedItem.Response> savedItemResponseList = new ArrayList<>();
        for (SavedItem savedItem : savedItemList) {
            Favorite.SavedItemResponse favorite = itemValidator.isFavoriteItem(favorites, savedItem.getItem(), savedItem.getPrice());
            SavedItem.Response savedItemResponse = new SavedItem.Response(savedItem, favorite);
            savedItemResponseList.add(savedItemResponse);
        }
        Collections.reverse(savedItemResponseList); //id 내림차순 정렬
        return new Message(true, "티끌 조회에 성공했습니다.", savedItemResponseList);
    }

    @Transactional
    public Message updateSavedItem(Long savedItemId, SavedItem.Update price, User user) throws ItemException {
        SavedItem savedItem = itemValidator.isValidSavedItem(savedItemId, user);

        //달성율 갱신
        GoalItem goalItem = savedItem.getGoalItem();
        int savedItemTotal = 0;
        for (SavedItem tempSavedItem : goalItem.getSavedItems()) {
            savedItemTotal += tempSavedItem.getPrice();
        }
        int updatePrice = savedItemTotal + price.getPrice();

        double decimal = ((double) updatePrice / goalItem.getTotal());
        double updateGoalPercent = MathFloor.PercentTenths(decimal);
        goalItem.setGoalPercent(updateGoalPercent);
        savedItem.update(price.getPrice());

        return new Message(true, "티끌 수정에 성공했습니다.");
    }

    @Transactional
    public Message deleteSavedItem(Long savedItemId, User user) throws ItemException {
        SavedItem savedItem = itemValidator.isValidSavedItem(savedItemId, user);

        //달성율 갱신
        GoalItem goalItem = savedItem.getGoalItem();
        int savedItemTotal = 0;
        for (SavedItem tempSavedItem : goalItem.getSavedItems()) {
            savedItemTotal += tempSavedItem.getPrice();
        }
        int updatePrice = savedItemTotal - savedItem.getPrice();
        double decimal = ((double) updatePrice / goalItem.getTotal());
        double updateGoalPercent = MathFloor.PercentTenths(decimal);

        savedItem.setGoalItem(null);
        savedItemRepository.deleteById(savedItemId);

        goalItem.setGoalPercent(updateGoalPercent);

        return new Message(true, "티끌 삭제에 성공했습니다.");
    }
}