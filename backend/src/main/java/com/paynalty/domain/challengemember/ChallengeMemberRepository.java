package com.paynalty.domain.challengemember;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChallengeMemberRepository extends JpaRepository<ChallengeMember, Long> {

    /**
     * 특정 사용자와 챌린지에 대한 ChallengeMember를 조회합니다.
     *
     * @param userId 사용자 ID
     * @param challengeId 챌린지 ID
     * @return ChallengeMember (없으면 Optional.empty())
     */
    @Query("SELECT cm FROM ChallengeMember cm " +
           "WHERE cm.user.id = :userId AND cm.challenge.id = :challengeId")
    Optional<ChallengeMember> findByUserIdAndChallengeId(
            @Param("userId") Long userId,
            @Param("challengeId") Long challengeId
    );
}
