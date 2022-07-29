package com.hanghae0705.sbmoney.controller.user;

import com.hanghae0705.sbmoney.data.Message;
import com.hanghae0705.sbmoney.model.domain.user.User;
import com.hanghae0705.sbmoney.model.dto.RespDto;
import com.hanghae0705.sbmoney.model.dto.TokenDto;
import com.hanghae0705.sbmoney.model.dto.TokenRequestDto;
import com.hanghae0705.sbmoney.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // 회원가입
    @PostMapping("/api/user/register")
    public RespDto registerUser(@RequestBody User.RequestRegister requestRegisterDto){
        userService.saveUser(requestRegisterDto);
        return RespDto.builder()
                .result(true)
                .respMsg("회원가입에 성공했습니다.")
                .build();
    }

    // 아이디 중복검사
    @PostMapping("/api/user/register/checkUsername")
    public RespDto checkUsername(@RequestBody User.RequestCheckUsername requestCheckUsername){
        return userService.checkUser(requestCheckUsername.getUsername());
    }

    // 이메일 중복검사
    @PostMapping("/api/user/register/checkEmail")
    public RespDto checkEmail(@RequestBody User.RequestCheckEmail requestCheckEmail){
        return userService.checkEmail(requestCheckEmail.getEmail());
    }

    // 닉네임 중복검사
    @PostMapping("/api/user/register/checkNickname")
    public RespDto checkNickname(@RequestBody User.RequestCheckNickname requestCheckNickname){
        return userService.checkNickname(requestCheckNickname.getNickname());
    }

    // 수정 예정
    @PostMapping("/api/user/register/social")
    public Message registerSocialUser(@RequestBody User.RequestSocialRegister requestSocialRegister, HttpServletRequest request, HttpServletResponse response) {
        return userService.registerSocialUser(requestSocialRegister, request, response);
    }

    // 로그인
    @PostMapping("/api/user/login")
    public TokenDto login(@RequestBody User.RequestLogin requestLogin, HttpServletRequest request, HttpServletResponse response) {
        return userService.login(requestLogin, request, response);
    }

    // 로그인된 유저의 데이터를 반환
    @GetMapping("/api/myInfo")
    public Message getMyInfo(){
        return userService.getMyInfo();
    }

    // 채팅용 닉네임, 프로필 이미지 반환
    @GetMapping("/api/myChatInfo")
    public ResponseEntity<Message> getMyChatInfo(){
        return ResponseEntity.ok(userService.getNicknameAndImg());
    }

    // 액세스 토큰, 리프레시 토큰 재발급
    @PostMapping("/api/user/reissue")
    public TokenDto reissue(@RequestBody TokenRequestDto tokenRequestDto, HttpServletRequest request, HttpServletResponse response) {
        return userService.reissue(tokenRequestDto, request, response);
    }

    // 소셜 로그인 - 부가 정보 회원가입
    // 인증정보가 남아 있는 경우에만 할 수 있는 걸로 수정 필요
//    @PostMapping("/api/user/register/social")
//    public RespDto registerSocialUser(@RequestBody User.RequestSocialRegister requesetDto){
//        userService.updateSocialUser(requesetDto);
//    }

    // 아이디 찾기
    @PostMapping("/api/user/findId")
    public Message findUsername(@RequestBody User.RequestUserId requestUserId) {
        return userService.findUsername(requestUserId);
    }

    // 비밀번호 찾기
    @PostMapping("/api/user/findPassword")
    public RespDto findPassword(@RequestBody User.RequestPassword requestPassword) {
        return userService.findPassword(requestPassword);
    }

    @PostMapping("/api/user/changePassword")
    public RespDto changePassword(HttpServletRequest httpServletRequest, @RequestBody User.RequestChangePassword requestChangePassword) {
        return userService.changePassword(httpServletRequest, requestChangePassword);
    }

    // 회원 탈퇴
    @PostMapping("/api/user/resign")
    public ResponseEntity<Message> requestResign(@RequestBody User.RequestLogin requestLogin) {
        return ResponseEntity.ok(userService.requestResign(requestLogin));
    }

    //로그인 테스트
    @GetMapping("/user/login")
    public ModelAndView login() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/login.html");
        return modelAndView;
    }

}
