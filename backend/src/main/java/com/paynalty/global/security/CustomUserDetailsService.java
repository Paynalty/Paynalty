package com.paynalty.global.security;

import com.paynalty.domain.user.User;
import com.paynalty.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // username은 JWT의 subject인 tossId
        Long tossId = Long.parseLong(username);
        User user = userRepository.findByTossId(tossId)
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 유저를 찾을 수 없습니다. tossId=" + tossId));
        return new CustomUserDetails(user);
    }
}
