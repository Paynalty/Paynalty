package com.paynalty.domain.challengemember;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface ChallengeMemberRepository extends JpaRepository<ChallengeMember, Long> {

    List<ChallengeMember> findByUserId(Long userId);

    Optional<ChallengeMember> findByUserIdAndChallengeId(Long userId, Long challengeId);

    List<ChallengeMember> findByChallengeId(Long challengeId);

}
