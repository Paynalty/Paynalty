package com.paynalty.domain.user;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paynalty.domain.toss.TossApiClient;
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

        user.updateUserInfo(name, phoneNum, email);
        return userRepository.save(user);
    }

    // 회원 탈퇴 (Toss 연결 끊기 및 데이터 삭제)
    @Transactional
    public void withdraw(Long userId) {
        User user = getById(userId);

        if (user.getTossId() == null) {
            log.warn("Toss ID(userKey)가 없는 사용자입니다: userId={}", userId);
            // Toss ID가 없으면 그냥 DB에서 삭제 진행
        } else {
            try {
                // 1. Toss API로 연결 끊기 요청 (remove-by-user-key)
                String response = tossApiClient.removeByUserKey(user.getTossId());

                JsonNode responseNode = objectMapper.readTree(response);
                if (responseNode.has("resultType") && "FAIL".equals(responseNode.get("resultType").asText())) {
                    JsonNode errorNode = responseNode.get("error");
                    log.error("Toss 연결 끊기 실패: errorCode={}, reason={}",
                            errorNode.get("errorCode").asText(),
                            errorNode.get("reason").asText());
                    throw new CustomException(UserErrorCode.TOSS_API_ERROR);
                }

                log.info("Toss 연결 끊기 성공: userId={}, userKey={}", userId, user.getTossId());

            } catch (Exception e) {
                log.error("Toss 연결 끊기 처리 중 오류 발생", e);
                // 외부 API 호출 실패 시 데이터 일관성을 위해 트랜잭션 롤백
                // 사용자가 DB에서만 삭제되고 Toss에는 남아있는 상황을 방지
                if (e instanceof CustomException) {
                    throw (CustomException) e;
                }
                throw new CustomException(UserErrorCode.TOSS_API_ERROR);
            }
        }

        // 2. 사용자 정보 영구 삭제
        userRepository.delete(user);
    }

    @Transactional
    public void unlinkByTossId(Long tossId) {
        userRepository.findByTossId(tossId).ifPresent(user -> {
            userRepository.delete(user);
        });
    }

}
