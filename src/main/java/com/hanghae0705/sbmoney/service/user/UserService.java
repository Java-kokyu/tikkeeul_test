package com.hanghae0705.sbmoney.service.user;

import com.hanghae0705.sbmoney.data.Message;
import com.hanghae0705.sbmoney.exception.ApiException;
import com.hanghae0705.sbmoney.exception.ApiRequestException;
import com.hanghae0705.sbmoney.exception.ApiRuntimeException;
import com.hanghae0705.sbmoney.model.domain.board.Board;
import com.hanghae0705.sbmoney.model.domain.board.Comment;
import com.hanghae0705.sbmoney.model.domain.chat.entity.ChatRoom;
import com.hanghae0705.sbmoney.model.domain.user.RefreshToken;
import com.hanghae0705.sbmoney.model.domain.user.User;
import com.hanghae0705.sbmoney.model.domain.baseEntity.UserRoleEnum;
import com.hanghae0705.sbmoney.model.dto.RespDto;
import com.hanghae0705.sbmoney.model.dto.TokenRequestDto;
import com.hanghae0705.sbmoney.repository.board.BoardRepository;
import com.hanghae0705.sbmoney.repository.board.CommentRepository;
import com.hanghae0705.sbmoney.repository.chat.ChatRoomRepository;
import com.hanghae0705.sbmoney.repository.item.FavoriteRepository;
import com.hanghae0705.sbmoney.repository.item.GoalItemRepository;
import com.hanghae0705.sbmoney.repository.item.SavedItemRepository;
import com.hanghae0705.sbmoney.repository.user.RefreshTokenRepository;
import com.hanghae0705.sbmoney.repository.user.UserRepository;
import com.hanghae0705.sbmoney.security.SecurityUtil;
import com.hanghae0705.sbmoney.model.dto.TokenDto;
import com.hanghae0705.sbmoney.security.auth.UserDetailsImpl;
import com.hanghae0705.sbmoney.security.jwt.TokenProvider;
import com.hanghae0705.sbmoney.security.CookieUtils;
import com.hanghae0705.sbmoney.util.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.hanghae0705.sbmoney.security.filter.JwtFilter.AUTHORIZATION_HEADER;
import static com.hanghae0705.sbmoney.security.filter.JwtFilter.BEARER_PREFIX;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final GoalItemRepository goalItemRepository;
    private final SavedItemRepository savedItemRepository;
    private final FavoriteRepository favoriteRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final BCryptPasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MailService mailService;

    public void saveUser(User.RequestRegister requestRegisterDto) {
        userRepository.save(User.builder()
                .id(null)
                .username(requestRegisterDto.getUsername())
                .password(passwordEncoder.encode(requestRegisterDto.getPassword()))
                .nickname(requestRegisterDto.getNickname())
                .email(requestRegisterDto.getEmail())
                .introDesc("티끌모아 태산!")
                .profileImg("https://d29fhpw069ctt2.cloudfront.net/icon/image/84587/preview.svg")
                .lastEntered(LocalDateTime.now())
                .provider("general")
                .role(UserRoleEnum.USER)
                .build());
    }

//    public void updateSocialUser(User.RequestSocialRegister requesetDto, HttpServletRequest request) {
//        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
//        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
//            if (tokenProvider.validateToken(bearerToken.substring(7))) {
//                userRepository.updateEmailNickname(requesetDto);
//                TokenDto tokenDto = tokenProvider.generateAccessToken(authentication);
//
//            RefreshToken refreshToken = RefreshToken.builder()
//                    .key(authentication.getName())
//                    .value(tokenProvider.generateRefreshToken())
//                    .build();
//
//            refreshTokenRepository.save(refreshToken);
//
//            CookieUtils.deleteCookie(request, response, "refreshToken");
//            CookieUtils.addCookie(response, "refreshToken", refreshToken.getValue(), TokenProvider.JWT_REFRESH_TOKEN_VALID_MILLI_SEC);
//
//            } else {
//                userRepository.delete();
//            }
//        }
//    }

    public RespDto checkUser(String username) {
        Optional<User> found = userRepository.findByUsername(username);

        if (found.isEmpty()) {
            return RespDto.builder()
                    .result(true)
                    .respMsg("중복된 아이디가 없습니다. 회원가입이 가능합니다.")
                    .build();
        } else {
            return RespDto.builder()
                    .result(false)
                    .respMsg("중복된 아이디가 있어 회원가입이 불가능합니다.")
                    .build();
        }
    }

    public RespDto checkNickname(String nickname) {
        Optional<User> found = userRepository.findByNickname(nickname);

        if (found.isEmpty()) {
            return RespDto.builder()
                    .result(true)
                    .respMsg("중복된 닉네임이 없습니다. 회원가입이 가능합니다.")
                    .build();
        } else {
            return RespDto.builder()
                    .result(false)
                    .respMsg("중복된 닉네임이 있어 회원가입이 불가능합니다.")
                    .build();
        }
    }

    public RespDto checkEmail(String email) {
        Optional<User> found = userRepository.findByEmail(email);

        if (found.isEmpty()) {
            return RespDto.builder()
                    .result(true)
                    .respMsg("중복된 이메일이 없어 가입이 가능합니다.")
                    .build();
        } else {
            return RespDto.builder()
                    .result(false)
                    .respMsg("중복된 이메일이 있어 가입이 불가능합니다.")
                    .build();
        }
    }

    public Message findUsername(User.RequestUserId requestUserId) {
        User.ResponseFoundId found = userRepository.findByEmail(requestUserId.getEmail())
                .map(User.ResponseFoundId::of)
                .orElseThrow(() -> new ApiRequestException(ApiException.NOT_EXIST_EMAIL));

        if(found.getProvider().equals("general")) {
            found.setUserId(found.getUserId().substring(0, 3) + "***");
            return Message.builder()
                    .result(true)
                    .respMsg("가입된 회원입니다.")
                    .data(found)
                    .build();
        } else if(found.getProvider().equals("kakao")) {
            return Message.builder()
                    .result(true)
                    .respMsg("카카오로 가입된 회원입니다.")
                    .data(found)
                    .build();
        } else {
            return Message.builder()
                    .result(true)
                    .respMsg("구글로 가입된 회원입니다.")
                    .data(found)
                    .build();
        }
    }

    public RespDto findPassword(User.RequestPassword requestPassword) {

        User found = userRepository.findByUsername(requestPassword.getUsername())
                .orElseThrow(() -> new ApiRequestException(ApiException.NOT_EXIST_USER));

        // username의 email과 클라이언트에서 보낸 email이 일치하는지 검사
        if (found.getEmail().equals(requestPassword.getEmail())) {
            // 소셜로 가입된 회원이면 메일 발송하지 않기
            if (!found.getProvider().equals("general")) {
                return RespDto.builder()
                        .result(true)
                        .respMsg(found.getProvider()+" 로 가입된 회원입니다.")
                        .build();
            } else {
                String email =  found.getEmail();

                // 메일 인증용 토큰 발급 및 메일 발송
                // 1. 익명 사용자용 authentication 생성
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                System.out.println("anonymous authentication :"+authentication);

                // 2. 인증 정보를 기반으로 JWT 토큰 생성(3분 짜리)
                TokenDto tokenDto = tokenProvider.generateAccessToken(authentication, 60 * 3 * 1000);

                // 3. 메일 발송
                mailService.sendMail(email, tokenDto.getAccessToken(), requestPassword.getUsername());

                return RespDto.builder()
                        .result(true)
                        .respMsg("메일을 발송했습니다.")
                        .build();
            }
        } else {
            return RespDto.builder()
                    .result(false)
                    .respMsg("가입된 정보가 없습니다.")
                    .build();
        }
    }

    @Transactional
    public RespDto changePassword(HttpServletRequest request, User.RequestChangePassword requestChangePassword) {
        // 토큰 꺼내기
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        String accessToken = null;
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
             accessToken = bearerToken.substring(7);
        }

        if (!tokenProvider.validateToken(request, accessToken)) {
            throw new RuntimeException("Token 이 유효하지 않습니다.");
        }

        String password = passwordEncoder.encode(requestChangePassword.getPassword());
        String username = requestChangePassword.getUsername();
        User found = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiRequestException(ApiException.NOT_EXIST_USER));

        // changePassword
        found.changePassword(password);
        return RespDto.builder()
                .result(true)
                .respMsg("비밀번호를 변경했습니다.")
                .build();
    }

    public Message getMyInfo() {
        User.Response resp = userRepository.findByUsername(SecurityUtil.getCurrentUsername())
                .map(User.Response::of)
                .orElseThrow(() -> new ApiRequestException(ApiException.NOT_EXIST_USER));

        return Message.builder()
                .result(true)
                .respMsg("로그인 유저 정보 조회에 성공하였습니다.")
                .data(resp)
                .build();
    }

    public Message getNicknameAndImg() {
        User.ResponseNicknameAndImg resp = userRepository.findByUsername(SecurityUtil.getCurrentUsername())
                .map(User.ResponseNicknameAndImg::of)
                .orElseThrow(() -> new ApiRequestException(ApiException.NOT_EXIST_USER));

        return Message.builder()
                .result(true)
                .respMsg("닉네임, 프로필 이미지 조회에 성공하였습니다.")
                .data(resp)
                .build();
    }

    @Transactional
    public TokenDto login(User.RequestLogin requestLogin, HttpServletRequest request, HttpServletResponse response) {
        // 1. Login ID/PW 를 기반으로 AuthenticationToken 생성
        UsernamePasswordAuthenticationToken authenticationToken = requestLogin.toAuthentication();

        // 2. 실제로 검증 (사용자 비밀번호 체크) 이 이루어지는 부분
        //    authenticate 메서드가 실행이 될 때 CustomUserDetailsService 에서 만들었던 loadUserByUsername 메서드가 실행됨
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        TokenDto tokenDto = tokenProvider.generateAccessToken(authentication);
        System.out.println("=====login 리프레시 토큰: " + tokenDto.getRefreshToken());

        // 4. RefreshToken DB에 저장
        RefreshToken refreshToken = RefreshToken.builder()
                .key(authentication.getName())
                .value(tokenDto.getRefreshToken())
                .build();

        refreshTokenRepository.save(refreshToken);

//        CookieUtils.deleteCookie(request, response, "refreshToken");
//        CookieUtils.addCookie(response, "refreshToken", refreshToken.getValue(), TokenProvider.JWT_REFRESH_TOKEN_VALID_MILLI_SEC);

        //5. 토큰 발급
        return tokenDto;
    }

    @Transactional
    public TokenDto reissue(TokenRequestDto tokenRequestDto, HttpServletRequest request, HttpServletResponse response) {
        // 1. Refresh Token 검증 - 만료 체크 jjwt 라이브러리가 해줌
        // 만약 리프레시 토큰을 탈취하고, reissue 요청을 날리면 1~5까지 다 뚫리는 거 아닌지?!
        // 방비: 탈취 방비(리프레시 토큰 http-only, secure 쿠키로 저장) & 탈취 후 방비(로그아웃 시 DB에서 리프레시 토큰 비워주기)
        // http-only는 자바스크립트로 조작 불가 / secure 쿠키는 https 가 아니면 전송하지 않는다.
//        Cookie refreshTokenCookie = CookieUtils.getCookie(request, "refreshToken")
//                .orElseThrow(()->new ApiRequestException(ApiException.NO_COOKIE));

//        if (!tokenProvider.validateToken(refreshTokenCookie.getValue())) {
//            throw new RuntimeException("Refresh Token 이 유효하지 않습니다.");
//        }

        if (!tokenProvider.validateToken(request, tokenRequestDto.getRefreshToken())) {
            System.out.println("=========오류 발생 refreshToken=========: " + tokenRequestDto.getRefreshToken());
            throw new ApiRequestException(ApiException.NOT_VALID_REFRESH_TOKEN);
        }

        // 2. Access Token 에서 username 가져오기
        Authentication authentication = tokenProvider.getAuthentication(tokenRequestDto.getAccessToken());

        // 3. 저장소에서 username 기반으로 Refresh Token 값 가져옴
        RefreshToken refreshToken = refreshTokenRepository.findByKey(authentication.getName())
                .orElseThrow(() -> new RuntimeException("로그아웃 된 사용자입니다."));

        // 4. Refresh Token 일치하는지 검사
//        if (!refreshToken.getValue().equals(refreshTokenCookie.getValue())) {
//            throw new RuntimeException("토큰의 유저 정보가 일치하지 않습니다.");
        if (!refreshToken.getValue().equals(tokenRequestDto.getRefreshToken())) {
            throw new RuntimeException("토큰의 유저 정보가 일치하지 않습니다.");
        }

        // 5. 새로운 토큰 생성
        TokenDto tokenDto = tokenProvider.generateAccessToken(authentication);
        System.out.println("=====reissue 리프레시 토큰 생성: " + tokenDto.getRefreshToken());

        // 6. 저장소 정보 업데이트
        RefreshToken newRefreshToken = refreshToken.updateValue(tokenDto.getRefreshToken());
        refreshTokenRepository.save(newRefreshToken);

//        CookieUtils.deleteCookie(request, response, "refreshToken");
//        CookieUtils.addCookie(response, "refreshToken", newRefreshToken.getValue(), TokenProvider.JWT_REFRESH_TOKEN_VALID_MILLI_SEC);

        // 토큰 발급
        return tokenDto;
    }

    @Transactional
    public Message registerSocialUser(User.RequestSocialRegister requestSocialRegister, HttpServletRequest request, HttpServletResponse response) {

        User found = userRepository.findByUsername(requestSocialRegister.getUsername())
                .orElseThrow(() -> new ApiRuntimeException(ApiException.NOT_EXIST_USER));

        if (found.getEmail().equals("")
                && requestSocialRegister.getUsername().equals(found.getUsername())) {

            UserDetailsImpl userDetails = new UserDetailsImpl(User.builder()
                    .username(found.getUsername())
                    .nickname(requestSocialRegister.getNickname())
                    .email(requestSocialRegister.getEmail())
                    .password(found.getPassword())
                    .profileImg(found.getProfileImg())
                    .introDesc(found.getIntroDesc())
                    .provider(found.getProvider())
                    .lastEntered(LocalDateTime.now())
                    .role(UserRoleEnum.USER)
                    .build());

            UsernamePasswordAuthenticationToken authenticationToken = requestSocialRegister.toAuthentication();

            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

            TokenDto tokenDto = tokenProvider.generateAccessToken(authentication);

            RefreshToken refreshToken = RefreshToken.builder()
                    .key(authentication.getName())
                    .value(tokenProvider.generateRefreshToken())
                    .build();

            refreshTokenRepository.save(refreshToken);

            CookieUtils.deleteCookie(request, response, "refreshToken");
            CookieUtils.addCookie(response, "refreshToken", refreshToken.getValue(), TokenProvider.JWT_REFRESH_TOKEN_VALID_MILLI_SEC);

            return Message.builder()
                    .result(true)
                    .respMsg("소셜 회원가입에 성공했습니다.")
                    .data(tokenDto)
                    .build();
        } else {
            return Message.builder()
                    .result(false)
                    .respMsg("이미 가입된 회원입니다.")
                    .build();
        }
    }

    @Transactional
    public Message requestResign(User.RequestLogin requestLogin) {
        User user = userRepository.findByUsername(requestLogin.getUsername())
                .orElseThrow(() -> new ApiRequestException(ApiException.NOT_EXIST_USER));
        Message message;

        if (passwordEncoder.matches(requestLogin.getPassword(), user.getPassword())) {
            // GoalItem, SavedItem, Favorite 삭제
            savedItemRepository.deleteAllByUserId(user.getId());
            log.info("savedItem 삭제 ok");
            goalItemRepository.deleteAllByUserId(user.getId());
            log.info("goalItem 삭제 ok");
            favoriteRepository.deleteAllByUserId(user.getId());
            log.info("favorite 삭제 ok");

            // Board, Comment, ChatRoom 유저 Null 업데이트
            updateBoardUserNull(user);
            updateCommentUserNull(user);
            updateChatRoomUserNull(user);

            // User 정보 삭제
            userRepository.delete(user);

            message = Message.builder()
                    .result(true)
                    .respMsg("회원탈퇴에 성공하였습니다.")
                    .build();
        } else {
            message = Message.builder()
                    .result(false)
                    .respMsg("아이디와 비밀번호가 일치하지 않습니다.")
                    .build();
        }
        return message;
    }

    public void updateBoardUserNull(User user) {
        List<Board> boardList = boardRepository.findAllByUserId(user.getId());
        boardList.forEach(Board::updateUserNull);
        boardRepository.saveAll(boardList);
    }

    public void updateCommentUserNull(User user) {
        List<Comment> commentList = commentRepository.findAllByUserId(user.getId());
        commentList.forEach(Comment::updateUserNull);
        commentRepository.saveAll(commentList);
    }

    public void updateChatRoomUserNull(User user) {
        List<ChatRoom> chatRoomList = chatRoomRepository.findAllByUserId(user.getId());
        chatRoomList.forEach(ChatRoom::updateUserNull);
        chatRoomRepository.saveAll(chatRoomList);
    }

}