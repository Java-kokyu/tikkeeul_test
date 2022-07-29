package com.hanghae0705.sbmoney.controller.user;

import com.hanghae0705.sbmoney.data.Message;
import com.hanghae0705.sbmoney.model.domain.user.Favorite;
import com.hanghae0705.sbmoney.model.domain.user.User;
import com.hanghae0705.sbmoney.service.user.FavoriteService;
import com.hanghae0705.sbmoney.service.user.MyProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping(value = "/api/mypage")
public class MyPageController {
    private final MyProfileService myProfileService;
    private final FavoriteService favoriteService;

    public MyPageController(MyProfileService myProfileService, FavoriteService favoriteService) {
        this.myProfileService = myProfileService;
        this.favoriteService = favoriteService;
    }

    @GetMapping("/profile")
    public ResponseEntity<Message> getProfile() {
        return ResponseEntity.ok(myProfileService.getProfile());
    }

    @PutMapping("/profile")
    public ResponseEntity<Message> updateProfile(@RequestPart User.RequestProfile changeInfo,
                                                 @RequestPart(value = "image", required = false) MultipartFile profileImg) throws IOException {
        return ResponseEntity.ok(myProfileService.updateProfile(changeInfo, profileImg));
    }

    @GetMapping("/favorite")
    public ResponseEntity<Message> getFavorite() {
        return ResponseEntity.ok(favoriteService.getFavorite());
    }

    @PutMapping("/favorite/{favoriteItemId}")
    public ResponseEntity<Message> updateFavorite(@PathVariable Long favoriteItemId, @RequestBody Favorite.UpdateFavorite request) {
        return ResponseEntity.ok(favoriteService.updateFavorite(favoriteItemId, request));
    }

    @PostMapping("/favorite")
    public ResponseEntity<Message> addFavorite(@RequestBody Favorite.Request request) {
        return ResponseEntity.ok(favoriteService.addFavorite(request));
    }

    @DeleteMapping("/favorite/{favoriteItemId}")
    public ResponseEntity<Message> deleteFavorite(@PathVariable Long favoriteItemId) {
        return ResponseEntity.ok(favoriteService.deleteFavorite(favoriteItemId));
    }
}