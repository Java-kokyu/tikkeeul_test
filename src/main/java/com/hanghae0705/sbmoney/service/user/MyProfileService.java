package com.hanghae0705.sbmoney.service.user;

import com.hanghae0705.sbmoney.data.Message;
import com.hanghae0705.sbmoney.exception.ApiException;
import com.hanghae0705.sbmoney.exception.ApiRequestException;
import com.hanghae0705.sbmoney.model.domain.user.User;
import com.hanghae0705.sbmoney.repository.user.UserRepository;
import com.hanghae0705.sbmoney.security.SecurityUtil;
import com.hanghae0705.sbmoney.service.S3Uploader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;

@Service
public class MyProfileService {
    private final UserRepository userRepository;
    private final S3Uploader s3Uploader;

    private String errorMsg;

    public MyProfileService(UserRepository userRepository, S3Uploader s3Uploader) {
        this.userRepository = userRepository;
        this.s3Uploader = s3Uploader;
    }

    public Message getProfile() {
        User.ResponseProfile responseProfile = new User.ResponseProfile();
        responseProfile.setUsername(getUser().getUsername());
        responseProfile.setNickname(getUser().getNickname());
        responseProfile.setProfileImg(getUser().getProfileImg());
        responseProfile.setIntroDesc(getUser().getIntroDesc());
        responseProfile.setEmail(getUser().getEmail());
        responseProfile.setNickname(getUser().getNickname());
        return new Message(true, "조회에 성공했습니다", responseProfile);
    }

    // 한글자 닉네임을 봐줘야하나
    @Transactional
    public Message updateProfile(User.RequestProfile changeInfo, MultipartFile profileImg) throws IOException {
        String imgUrl;
        try {
            checkStrLengthIsValid(changeInfo.getNickname(), 12, false);
            checkStrLengthIsValid(changeInfo.getIntroDesc(), 100, true);
            checkStrLengthIsValid(changeInfo.getEmail(), 100, false);
            if (profileImg != null) {
                imgUrl = s3Uploader.upload(profileImg, "static");
                getUser().updateProfile(changeInfo, imgUrl);
            } else {
                getUser().updateProfile(changeInfo);
            }
            return new Message(true, "프로필이 변경되었습니다");
        } catch (Exception e) {
            return new Message(false, errorMsg);
        }
    }

    private User getUser() {
        ApiRequestException e = new ApiRequestException(ApiException.NOT_EXIST_USER);
        errorMsg = e.getMessage();
        return userRepository.findByUsername(SecurityUtil.getCurrentUsername()).orElseThrow(() -> e);
    }

    private void checkStrLengthIsValid(String target, int max, boolean ableZero) {
        if(!ableZero) {
            if (target.trim().length() <= 0 || target.trim().length() > max) {
                ApiRequestException e = new ApiRequestException(ApiException.NOT_VALID_DATA);
                errorMsg = e.getMessage();
                throw e;
            }
        } else {
            if (target.trim().length() > max) {
                ApiRequestException e = new ApiRequestException(ApiException.NOT_VALID_DATA);
                errorMsg = e.getMessage();
                throw e;
            }
        }
    }
}