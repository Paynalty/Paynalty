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
                .email(request.getEmail())
                .profileImageUrl(request.getProfileImageUrl())
                .build();

        User saved = userRepository.save(user);

        return UserResponse.from(saved);

    }

}

