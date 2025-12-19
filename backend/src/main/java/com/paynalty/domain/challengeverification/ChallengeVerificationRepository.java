package com.paynalty.domain.challengeverification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface ChallengeVerificationRepository extends JpaRepository<ChallengeVerification, Long> {

    /**
     * 특정 챌린지와 사용자의 이번주 인증 횟수를 조회합니다.
     * 이번주는 월요일부터 일요일까지입니다.
     *
     * @param challengeId 챌린지 ID
     * @param userId 사용자 ID
     * @param weekStart 이번주 시작일 (월요일)
     * @param weekEnd 이번주 종료일 (일요일)
     * @return 이번주 인증 횟수
     */
    @Query("SELECT COUNT(cv) FROM ChallengeVerification cv " +
           "WHERE cv.challenge.id = :challengeId " +
           "AND cv.user.id = :userId " +
           "AND cv.date >= :weekStart " +
           "AND cv.date <= :weekEnd")
    Long countWeeklyVerifications(
            @Param("challengeId") Long challengeId,
            @Param("userId") Long userId,
            @Param("weekStart") LocalDate weekStart,
            @Param("weekEnd") LocalDate weekEnd
    );
}
