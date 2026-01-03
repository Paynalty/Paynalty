package com.paynalty.domain.user;

import com.paynalty.global.error.CustomException;
import com.paynalty.global.error.UserErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@lombok.extern.slf4j.Slf4j
public class UserService {

    private final UserRepository userRepository;

    // String 이 email 또는 name 에 포함된 모든 유저 찾기
    public List<UserResponse> findByNameAndEmail(String identify) {
        // 검색어 길이 검증 (최소 2자 이상)
        if (identify == null || identify.trim().isEmpty()) {
            throw new CustomException(UserErrorCode.SEARCH_KEYWORD_EMPTY);
        }

        if (identify.trim().length() < 2) {
            throw new CustomException(UserErrorCode.SEARCH_KEYWORD_TOO_SHORT);
        }

        List<User> users = userRepository.findByEmailOrName(identify.trim());
        return users.stream().map(UserResponse::from).toList();
    }

    public User getById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
    }

    public User getByTossId(Long tossId) {
        return userRepository.findByTossId(tossId)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
    }

    private User getOrCreateUser(Long tossId) {
        return userRepository.findByTossId(tossId)
                .orElseGet(() -> {
                    log.info("신규 사용자 생성: TossId={}", tossId);
                    User newUser = User.builder()
                            .tossId(tossId)
                            .build();
                    return userRepository.save(newUser);
                });
    }

    // 토스 토큰 저장/업데이트
    @Transactional
    public User saveTokens(Long tossId, String accessToken, String refreshToken) {
        User user = getOrCreateUser(tossId);

        if (user.getId() != null && userRepository.existsById(user.getId())) {
            log.debug("토큰 업데이트 진행: userId={}", user.getId());
        }

        user.updateTokens(accessToken, refreshToken);
        return userRepository.save(user);
    }

    // 토스 사용자 정보 저장/업데이트
    @Transactional
    public User saveUserInfo(Long tossId, String name, String phoneNum, String email) {
        User user = getOrCreateUser(tossId);

        log.info("사용자 정보 업데이트: userId={}, name={}, phone={}, email={}", user.getId(), name, phoneNum, email);
        user.updateUserInfo(name, phoneNum, email);
        return userRepository.save(user);
    }
}
