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
    public UserDetails loadUserByUsername(String tossId) throws UsernameNotFoundException {
        try {
            return loadUserByTossId(Long.parseLong(tossId));
        } catch (NumberFormatException e) {
            throw new UsernameNotFoundException("올바르지 않은 형식의 tossId입니다: " + tossId);
        }
    }

    public UserDetails loadUserByTossId(Long tossId) {
        User user = userRepository.findByTossId(tossId)
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 유저를 찾을 수 없습니다. tossId=" + tossId));
        return new CustomUserDetails(user);
    }
}
