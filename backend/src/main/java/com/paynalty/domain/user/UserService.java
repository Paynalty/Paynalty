package com.paynalty.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    
    private final UserRepository userRepository;

    /**
     * 전화번호로 사용자를 찾습니다.
     * 전화번호를 정규화하여 매칭합니다.
     * 
     * @param phoneNumber 전화번호 (하이픈, 공백 포함 가능)
     * @return 찾은 사용자, 없으면 null
     */
    public User findByPhoneNumber(String phoneNumber) {
        // 전화번호 정규화 (하이픈, 공백 제거)
        String normalizedPhone = phoneNumber.replaceAll("[^0-9]", "");
        
        // 정규화된 전화번호로 사용자 찾기
        return userRepository.findByPhoneNumber(normalizedPhone).orElse(null);
    }
}

