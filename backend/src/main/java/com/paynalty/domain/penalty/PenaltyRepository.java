package com.paynalty.domain.penalty;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PenaltyRepository extends JpaRepository<Penalty, Long> {

    /**
     * 챌린지 내에 나의 벌금 이력 조회
     * ChallengeMember와 User, Challenge를 함께 fetch하여 N+1 문제를 방지합니다.
     *
     * @param challengeId 챌린지 ID
     * @param tossId 사용자 토스 ID
     * @return 벌금 리스트
     */
    @Query("""
        SELECT p 
        FROM Penalty p
        JOIN FETCH p.challengeMember cm
        JOIN FETCH cm.user
        JOIN FETCH cm.challenge
        WHERE cm.challenge.id = :challengeId 
        AND cm.user.tossId = :tossId
        ORDER BY p.createdAt DESC
        """)
    List<Penalty> findByChallengeIdAndTossId(Long challengeId, Long tossId);

}


