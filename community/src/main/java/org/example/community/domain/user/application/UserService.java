package org.example.community.domain.user.application;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.example.community.domain.auth.repository.RefreshTokenRepository;
import org.example.community.domain.post.comment.repository.CommentRepository;
import org.example.community.domain.post.repository.LikeRepository;
import org.example.community.domain.post.repository.PostRepository;
import org.example.community.domain.user.User;
import org.example.community.domain.user.api.dto.request.UserCreateRequestDto;
import org.example.community.domain.user.api.dto.request.UserPasswordUpdateRequestDto;
import org.example.community.domain.user.api.dto.response.UserInfoDto;
import org.example.community.domain.user.api.dto.response.UserUpdateRequestDto;
import org.example.community.domain.user.repository.UserRepository;
import org.example.community.global.ImageValidator;
import org.example.community.global.exception.CustomException;
import org.example.community.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ImageValidator imageValidator;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    // 회원가입
    public void signup(UserCreateRequestDto userCreateRequestDto, MultipartFile profileImage) {
        // 이메일 중복 검사
        validateEmailDuplication(userCreateRequestDto.getEmail());

        // 닉네임 중복 검사
        validateNicknameDuplication(userCreateRequestDto.getNickname());

        // 사진 검증
        imageValidator.validate(profileImage);

        // 비밀번호, 비밀번호 확인 검증
        validatePassword(userCreateRequestDto.getPassword(), userCreateRequestDto.getCheckPassword());

        User user = User.builder()
                .email(userCreateRequestDto.getEmail())
                .password(userCreateRequestDto.getPassword())
                .nickname(userCreateRequestDto.getNickname())
                .profileImage(imageValidator.extractBytes(profileImage))
                .build();

        userRepository.save(user);
    }

    // 회원탈퇴
    public void deleteUser(Long loginUserId) {
        findUserById(loginUserId);

        // 회원과 연결된 객체 삭제
        postRepository.deleteByUserId(loginUserId);
        commentRepository.deleteByUserId(loginUserId);
        likeRepository.deleteByUserId(loginUserId);
        refreshTokenRepository.deleteByUserId(loginUserId);

        userRepository.deleteById(loginUserId);
    }

    // 이메일 중복 검사
    public void validateEmailDuplication(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new CustomException(ErrorCode.DUPLICATION_EMAIL);
        }
    }

    // 닉네임 중복 검사
    public void validateNicknameDuplication(String nickname) {
        if (userRepository.existsByNickname(nickname)) {
            throw new CustomException(ErrorCode.DUPLICATION_NICKNAME);
        }
    }

    // 회원 정보 조회
    public UserInfoDto getUserInfo(Long userId, Long loginUserId) {
        validateLogin(userId, loginUserId);
        return UserInfoDto.from(findUserById(userId));
    }

    // 회원 정보 수정 - 닉네임, 프로필 사진
    public void updateUserInfo(Long userId, Long loginUserId, UserUpdateRequestDto userUpdateRequestDto,
                               MultipartFile profileImage) {
        validateLogin(userId, loginUserId);

        User user = findUserById(userId);
        String nickname = (userUpdateRequestDto != null) ? userUpdateRequestDto.getNickname() : user.getNickname();

        if (userUpdateRequestDto != null && !nickname.equals(user.getNickname())) {
            validateNicknameDuplication(nickname);
        }

        byte[] image = (profileImage != null && !profileImage.isEmpty())
                ? imageValidator.extractBytes(profileImage)
                : user.getProfileImage();

        user.updateUserInfo(nickname, image);
        userRepository.save(user);
    }

    // 회원 비밀번호 수정
    public void updateUserPassword(Long userId, Long loginUserId,
                                   UserPasswordUpdateRequestDto userPasswordUpdateRequestDto) {
        validateLogin(userId, loginUserId);
        User user = findUserById(userId);
        matchPassword(user, userPasswordUpdateRequestDto.getCurrentPassword());
        validatePassword(userPasswordUpdateRequestDto.getPassword(), userPasswordUpdateRequestDto.getCheckPassword());
        user.updatePassword(userPasswordUpdateRequestDto.getPassword());
        userRepository.save(user);
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));
    }

    private void validatePassword(String password, String checkPassword) {
        if (!password.equals(checkPassword)) {
            throw new CustomException(ErrorCode.MISMATCH_PASSWORD);
        }
    }

    private void validateLogin(Long userId, Long loginUserId) {
        if (!Objects.equals(loginUserId, userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
    }

    private void matchPassword(User user, String currentPassword) {
        if (!user.getPassword().equals(currentPassword)) {
            throw new CustomException(ErrorCode.MISMATCH_PASSWORD);
        }
    }
}
