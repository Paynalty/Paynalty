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

    /**
     * 챌린지 ID와 사용자 ID로 ChallengeMember를 조회합니다.
     * User와 Challenge를 함께 fetch하여 N+1 문제를 방지합니다.
     *
     * @param challengeId 챌린지 ID
     * @param userId 사용자 ID
     * @return ChallengeMember (User, Challenge 포함)
     */
    @Query("""
    SELECT cm
    FROM ChallengeMember cm
    JOIN FETCH cm.user
    JOIN FETCH cm.challenge
    WHERE cm.challenge.id = :challengeId
    AND cm.user.id = :userId
    """)
    Optional<ChallengeMember> findByChallengeIdAndUserIdWithFetch(
            @Param("challengeId") Long challengeId,
            @Param("userId") Long userId
    );

    @Query("""
    SELECT cm
    FROM ChallengeMember cm
    JOIN FETCH cm.user
    JOIN FETCH cm.challenge
    WHERE cm.challenge IN :challenges
    """)
    List<ChallengeMember> findWithUserAndChallengeIn(@Param("challenges") List<Challenge> challenges);



    

}
