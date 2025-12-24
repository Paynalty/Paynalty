package com.paynalty.domain.challengemember;

import com.paynalty.domain.challenge.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

import java.util.List;
import java.util.Optional;


public interface ChallengeMemberRepository extends JpaRepository<ChallengeMember, Long> {

    List<ChallengeMember> findByUserId(Long userId);

    Optional<ChallengeMember> findByUserIdAndChallengeId(Long userId, Long challengeId);

    List<ChallengeMember> findByChallengeId(Long challengeId);

    @Query("""
    SELECT cm
    FROM ChallengeMember cm
    JOIN FETCH cm.user
    JOIN FETCH cm.challenge
    WHERE cm.challenge IN :challenges
    """)
    List<ChallengeMember> findWithUserAndChallengeIn(@Param("challenges") List<Challenge> challenges);

}
