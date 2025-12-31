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
public class UserService {
    
    private final UserRepository userRepository;

    @Transactional
    public UserResponse create(UserRequest request){
        User user = User.builder()
                .tossId(request.getTossId())
                .name(request.getName())
                .email(request.getEmail())
                .phoneNum(request.getPhoneNum())
                .build();

        User saved = userRepository.save(user);

        return UserResponse.from(saved);
    }

    public User findByNameAndPhoneNum(String name, String phoneNum){
        User user = userRepository.findByNameAndPhoneNum(name,phoneNum).orElseThrow();
        return user;
    }

    // String 이 email 또는 name 에 포함된 모든 유저 찾기
    public List<UserResponse> findByNameAndEmail(String identify){
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

    //userId 로 user찾기
    public User getById(Long userId){
        return userRepository.findById(userId).orElseThrow(()->new CustomException(UserErrorCode.USER_NOT_FOUND));
    }

}

