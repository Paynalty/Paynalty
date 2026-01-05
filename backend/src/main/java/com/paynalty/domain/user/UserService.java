package com.paynalty.domain.user;

import com.paynalty.global.error.CustomException;
import com.paynalty.global.error.UserErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paynalty.domain.toss.TossApiClient;
import com.paynalty.domain.toss.dto.TossLoginResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@lombok.extern.slf4j.Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final TossApiClient tossApiClient;
    private final ObjectMapper objectMapper;

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
    public User saveTokens(Long tossId, String refreshToken) {
        User user = getOrCreateUser(tossId);

        if (user.getId() != null && userRepository.existsById(user.getId())) {
            log.debug("토큰 업데이트 진행: userId={}", user.getId());
        }

        user.updateTokens(refreshToken);
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

    // 토스 로그인 연결 끊기
    @Transactional
    public void unlinkUser(Long userId) {
        User user = getById(userId);

        if (user.getRefreshToken() == null) {
            log.info("이미 연결이 해제되었거나 토큰이 없는 사용자입니다: userId={}", userId);
            return;
        }

        try {
            // 1. Refresh Token으로 Access Token 재발급
            String refreshResponse = tossApiClient.refreshToken(user.getRefreshToken());

            JsonNode responseNode = objectMapper.readTree(refreshResponse);

            if (responseNode.has("resultType") && "FAIL".equals(responseNode.get("resultType").asText())) {
                log.error("토큰 재발급 실패: userId={}", userId);
                throw new CustomException(UserErrorCode.TOSS_API_ERROR);
            }

            JsonNode successNode = responseNode.get("success");
            TossLoginResponse loginResponse = objectMapper.treeToValue(successNode, TossLoginResponse.class);

            // 2. Access Token으로 연결 끊기 요청
            String unlinkResponse = tossApiClient.unlink(loginResponse.getAccessToken());

            JsonNode unlinkNode = objectMapper.readTree(unlinkResponse);
            if (unlinkNode.has("resultType") && "FAIL".equals(unlinkNode.get("resultType").asText())) {
                log.error("토스 연결 끊기 API 실패: {}", unlinkResponse);
                throw new CustomException(UserErrorCode.TOSS_API_ERROR);
            }

            log.info("토스 연결 끊기 성공: userId={}, userKey={}", userId, user.getTossId());

        } catch (Exception e) {
            log.error("토스 연결 끊기 중 오류 발생", e);
            if (e instanceof CustomException) {
                throw (CustomException) e;
            }
            throw new CustomException(UserErrorCode.TOSS_API_ERROR);
        }

        // 3. 사용자 정보 삭제
        userRepository.delete(user);
        log.info("사용자 정보 삭제 완료: userId={}", userId);
    }
}
