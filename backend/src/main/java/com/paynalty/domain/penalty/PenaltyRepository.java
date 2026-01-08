package com.paynalty.domain.penalty;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
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
    List<Penalty> findByChallengeIdAndTossId(
            @Param("challengeId") Long challengeId,
            @Param("tossId") Long tossId
    );

    /**
     * 챌린지의 모든 벌금 내역 조회
     * ChallengeMember와 User, Challenge를 함께 fetch하여 N+1 문제를 방지합니다.
     *
     * @param challengeId 챌린지 ID
     * @return 벌금 리스트 (최신순 정렬)
     */
    @Query("""
        SELECT p 
        FROM Penalty p
        JOIN FETCH p.challengeMember cm
        JOIN FETCH cm.user
        JOIN FETCH cm.challenge
        WHERE cm.challenge.id = :challengeId
        ORDER BY p.createdAt DESC
        """)
    List<Penalty> findAllByChallengeId(@Param("challengeId") Long challengeId);

    /**
     * 특정 주에 생성된 벌금 개수를 조회합니다.
     * 주간 횟수 기반 챌린지에서 벌금 중복 생성을 방지하기 위해 사용됩니다.
     *
     * @param challengeId 챌린지 ID
     * @param tossId 사용자 토스 ID
     * @param weekStart 주간 시작일 (월요일)
     * @param weekEnd 주간 종료일 (일요일)
     * @return 해당 주에 생성된 벌금 개수
     */
    @Query("""
        SELECT COUNT(p) 
        FROM Penalty p
        JOIN p.challengeMember cm
        WHERE cm.challenge.id = :challengeId
        AND cm.user.tossId = :tossId
        AND CAST(p.createdAt AS date) >= :weekStart
        AND CAST(p.createdAt AS date) <= :weekEnd
        """)
    Long countWeeklyPenalties(
            @Param("challengeId") Long challengeId,
            @Param("tossId") Long tossId,
            @Param("weekStart") LocalDate weekStart,
            @Param("weekEnd") LocalDate weekEnd
    );

}


