package com.hanghae0705.sbmoney.service.user;

import com.hanghae0705.sbmoney.data.Message;
import com.hanghae0705.sbmoney.exception.ApiException;
import com.hanghae0705.sbmoney.exception.ApiRequestException;
import com.hanghae0705.sbmoney.model.domain.item.Category;
import com.hanghae0705.sbmoney.model.domain.user.Favorite;
import com.hanghae0705.sbmoney.model.domain.item.Item;
import com.hanghae0705.sbmoney.model.domain.user.User;
import com.hanghae0705.sbmoney.repository.item.CategoryRepository;
import com.hanghae0705.sbmoney.repository.item.FavoriteRepository;
import com.hanghae0705.sbmoney.repository.item.ItemRepository;
import com.hanghae0705.sbmoney.repository.user.UserRepository;
import com.hanghae0705.sbmoney.security.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    private String errorMsg;
    public FavoriteService(FavoriteRepository favoriteRepository, @Lazy ItemRepository itemRepository, UserRepository userRepository, @Lazy CategoryRepository categoryRepository) {
        this.favoriteRepository = favoriteRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    // 나중에 Exception 추가해서 catch 상세하게 잡아내기
    public Message getFavorite() {
        List<Favorite.Response> responseList = new ArrayList<>();
        List<Favorite> tempArr = favoriteRepository.findByUser_Id(getUser().getId());
        for(Favorite favorite : tempArr) {
            Favorite.Response response = new Favorite.Response(favorite);
            responseList.add(response);
        }
        return new Message(true, "조회에 성공했습니다.", responseList);
    }

    @Transactional
    public Message addFavorite(Favorite.Request request) {

        Long favoriteItemId;
        ApiRequestException e1 = new ApiRequestException(ApiException.NOT_EXIST_DATA);
        try {
            Map<String, Long> favoriteItemIdResponse = new HashMap<>();
            if (getValueFromRepoById("category", request.getCategoryId()) == null) {
                log.info(e1.getMessage());
                throw e1;
            }
            checkPriceOverZero(request.getPrice());
            // DB에 없는 아이템을 추가할 경우 request.getitemId == -1
            if (request.getItemId() == -1) {
                Item item = new Item(request, (Category) getValueFromRepoById("category", request.getCategoryId()));
                Favorite favorite = new Favorite(request, getUser(), item);
                itemRepository.save(item);
                favoriteRepository.save(favorite);
                favoriteItemId = favorite.getId();
                favoriteItemIdResponse.put("favoriteItemId", favoriteItemId);
            } else {
                Favorite favorite = new Favorite(request, getUser(), (Item) getValueFromRepoById("item", request.getItemId()));
                favoriteRepository.save(favorite);
                favoriteItemId = favorite.getId();
                favoriteItemIdResponse.put("favoriteItemId", favoriteItemId);
            }


            return new Message(true, "추가에 성공했습니다.", favoriteItemIdResponse);
        } catch (Exception e) {
            return new Message(false, errorMsg);
        }
    }

    @Transactional
    public Message updateFavorite(Long favoriteItemId, Favorite.UpdateFavorite request) {
        try {
            Favorite favorite = (Favorite) getValueFromRepoById("favorite", favoriteItemId);
            compareTwoObjectsIsEqual(getUser().getUsername(), favorite.getUser().getUsername());
            checkPriceOverZero(request.getPrice());
            compareTwoObjectsIsNotEqual(request.getPrice(), favorite.getPrice());
            favorite.updateFavorite(request);
            return new Message(true, "수정에 성공했습니다");
        } catch (Exception e) {
            return new Message(false, errorMsg);
        }
    }

    @Transactional
    public Message deleteFavorite(Long favoriteItemId) {
        try {
            Favorite favorite = (Favorite) getValueFromRepoById("favorite", favoriteItemId);
            compareTwoObjectsIsEqual(getUser().getUsername(), favorite.getUser().getUsername());
            favoriteRepository.deleteById(favorite.getId());
            return new Message(true, "삭제에 성공했습니다");
        } catch (Exception e) {
            return new Message(false, errorMsg);
        }
    }

    public void getExpMsg(Exception e) {
        log.info(e.getMessage());
        errorMsg = e.getMessage();
    }

    public Object getValueFromRepoById(String repoName, Long id) {
        ApiRequestException e = new ApiRequestException(ApiException.NOT_EXIST_DATA);
        errorMsg = e.getMessage();
        switch (repoName) {
            case "category":
                return categoryRepository.findById(id).orElseThrow(() -> e);
            case "favorite":
                return favoriteRepository.findById(id).orElseThrow(() -> e);
            case "item":
                return itemRepository.findById(id).orElseThrow(() -> e);
        }
        return false;
    }

    private User getUser() {
        ApiRequestException e = new ApiRequestException(ApiException.NOT_MATCH_USER);
        errorMsg = e.getMessage();
        return userRepository.findByUsername(SecurityUtil.getCurrentUsername()).orElseThrow(() -> e);
    }

    // if문 쓰는게 더 보기 편한가
    private void compareTwoObjectsIsEqual(Object target1, Object target2) {
        if (!target1.equals(target2)) {
            IllegalArgumentException e = new IllegalArgumentException("두 인자가 일치하지 않습니다.");
            getExpMsg(e);
            throw e;
        }
    }

    private void compareTwoObjectsIsNotEqual(Object target1, Object target2) {
        if (target1.equals(target2)) {
            IllegalArgumentException e = new IllegalArgumentException("두 인자가 일치합니다.");
            getExpMsg(e);
            throw e;
        }
    }

    private void checkPriceOverZero(int price) {
        if(price <= 0) {
            ApiRequestException e = new ApiRequestException(ApiException.NOT_VALID_DATA);
            getExpMsg(e);
            throw e;
        }
    }

}
