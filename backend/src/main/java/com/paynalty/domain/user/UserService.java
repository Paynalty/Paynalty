package com.paynalty.domain.user;

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
        List<User> users = userRepository.findByEmailOrName(identify);
        return users.stream().map(UserResponse::from).toList();
    }

}

