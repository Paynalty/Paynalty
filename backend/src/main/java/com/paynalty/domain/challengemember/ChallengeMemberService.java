package com.paynalty.domain.challengemember;

import com.paynalty.domain.challenge.Challenge;
import com.paynalty.domain.challenge.ChallengeRepository;
import com.paynalty.domain.user.User;
import com.paynalty.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChallengeMemberService {
    private final ChallengeMemberRepository challengeMemberRepository;
    private final ChallengeRepository challengeRepository;
    private final UserService userService;


}
