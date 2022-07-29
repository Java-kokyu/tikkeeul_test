package com.hanghae0705.sbmoney.model.domain.user;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.hanghae0705.sbmoney.model.domain.baseEntity.BaseEntity;
import com.hanghae0705.sbmoney.model.domain.baseEntity.UserRoleEnum;
import com.hanghae0705.sbmoney.model.domain.item.GoalItem;
import lombok.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="USER_ID")
    private Long id;

    @NotNull
    @Column(unique = true)
    private String username;

    @NotNull
    private String password;

    @NotNull
    @Column(unique = true)
    private String nickname;

    @NotNull
    @Column(unique = true)
    private String email;

    @NotNull
    private String profileImg;

    @NotNull
    private String introDesc;

    @NotNull
    private String provider; //general, google, kakao - enum 으로 바꿀까..

    @NotNull
    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum role;

    @NotNull
    private LocalDateTime lastEntered;

    @OneToMany(mappedBy = "user")
    @JsonManagedReference(value = "user-fk")
    private List<GoalItem> goalItems;

    public User(String username, String password, UserRoleEnum role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    @Builder
    public User(Long id, String username, String password, String nickname, String email, String profileImg, String introDesc, String provider, UserRoleEnum role, LocalDateTime lastEntered) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.email = email;
        this.profileImg = profileImg;
        this.introDesc = introDesc;
        this.provider = provider;
        this.role = role;
        this.lastEntered = lastEntered;
    }

    public void changeNickname(String nickname){
        this.nickname = nickname;
    }

    public void changeEmail(String email){
        this.email = email;
    }

    public void changeProfileImg(String imgUrl){
        this.profileImg = imgUrl;
    }

    public void changeIntroDesc(String desc){
        this.introDesc = desc;
    }

    public void changeLastEntered(LocalDateTime lastEntered){
        this.lastEntered = lastEntered;
    }

    public void changePassword(String password) {
        this.password = password;
    }

    @Setter
    @Getter
    @NoArgsConstructor
    public static class RequestRegister {
        private String username;
        private String password;
        private String checkPassword;
        private String nickname;
        private String email;
        private String profileImg;
        private String provider;

        @Builder
        public RequestRegister(String username, String password, String checkPassword, String nickname, String email, String profileImg, String provider) {
            this.username = username;
            this.password = password;
            this.checkPassword = checkPassword;
            this.nickname = nickname;
            this.email = email;
            this.profileImg = profileImg;
            this.provider = provider;
        }
    }

    public void updateProfile(RequestProfile requestProfile, String profileImg) {
        this.nickname = requestProfile.getNickname();
        this.email = requestProfile.getEmail();
        this.introDesc = requestProfile.getIntroDesc();
        this.profileImg = profileImg;
    }

    public void updateProfile(RequestProfile requestProfile) {
        this.nickname = requestProfile.getNickname();
        this.email = requestProfile.getEmail();
        this.introDesc = requestProfile.getIntroDesc();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class RequestProfile {
        private String nickname;
        private String email;
        private String profileImg;
        private String introDesc;
    }

    @Getter
    @Setter
    @RequiredArgsConstructor
    public static class ResponseProfile{

        private String username;
        private String nickname;
        private String email;
        private String profileImg;
        private String introDesc;
    }
    @Setter
    @Getter
    @NoArgsConstructor
    public static class RequestSocialRegister {
        private String username;
        private String nickname;
        private String email;

        @Builder
        public RequestSocialRegister(String username, String nickname, String email) {
            this.username = username;
            this.nickname = nickname;
            this.email = email;
        }

        public UsernamePasswordAuthenticationToken toAuthentication(){
            return new UsernamePasswordAuthenticationToken(username, "");
        }
    }

    @Setter
    @Getter
    @NoArgsConstructor
    public static class RequestLogin {
        private String username;
        private String password;

        public UsernamePasswordAuthenticationToken toAuthentication(){
            return new UsernamePasswordAuthenticationToken(username, password);
        }
    }

    @Setter
    @Getter
    @NoArgsConstructor
    public static class RequestCheckUsername {
        private String username;

        @Builder
        public RequestCheckUsername(String username) {
            this.username = username;
        }
    }

    @Setter
    @Getter
    @NoArgsConstructor
    public static class RequestCheckEmail {
        private String email;

        @Builder
        public RequestCheckEmail(String email) {
            this.email = email;
        }
    }

    @Setter
    @Getter
    @NoArgsConstructor
    public static class RequestCheckNickname {
        private String nickname;

        @Builder
        public RequestCheckNickname(String nickname) {
            this.nickname = nickname;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class RequestUserId {
        private String email;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class RequestPassword {
        private String username;
        private String email;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class RequestChangePassword {
        private String username;
        private String password;
        private String checkPassword;
    }

    @Setter
    @Getter
    @NoArgsConstructor
    public static class Response {
        private String username;
        private String nickname;
        private String email;

        public static Response of(User user){
            return Response.builder()
                    .username(user.getUsername())
                    .nickname(user.getNickname())
                    .email(user.getEmail())
                    .build();
        }

        @Builder
        public Response(String username, String nickname, String email) {
            this.username = username;
            this.nickname = nickname;
            this.email = email;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ResponseFoundId {
        String userId;
        String provider;

        public static ResponseFoundId of(User user){
            return ResponseFoundId.builder()
                    .userId(user.getUsername())
                    .provider(user.getProvider())
                    .build();
        }
        @Builder
        public ResponseFoundId(String userId, String provider) {
            this.userId = userId;
            this.provider = provider;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ResponseNicknameAndImg {
        private String nickname;
        private String profileImg;

        public static ResponseNicknameAndImg of(User user){
            return ResponseNicknameAndImg.builder()
                    .nickname(user.getNickname())
                    .profileImg(user.getProfileImg())
                    .build();
        }

        @Builder
        public ResponseNicknameAndImg(String nickname, String profileImg) {
            this.nickname = nickname;
            this.profileImg = profileImg;
        }
    }
}
