package com.paynalty.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

}

