package com.paynalty.domain.challengeverification;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
           "AND cv.user.tossId = :tossId " +
           "AND cv.date >= :weekStart " +
           "AND cv.date <= :weekEnd")
    Long countWeeklyVerifications(
            @Param("challengeId") Long challengeId,
            @Param("tossId") Long tossId,
            @Param("weekStart") LocalDate weekStart,
            @Param("weekEnd") LocalDate weekEnd
    );

    /**
     * 특정 챌린지와 사용자의 전체 인증 횟수를 조회합니다.
     *
     * @param challengeId 챌린지 ID
     * @param userId 사용자 ID
     * @return 전체 인증 횟수
     */
    @Query("SELECT COUNT(cv) FROM ChallengeVerification cv " +
           "WHERE cv.challenge.id = :challengeId " +
           "AND cv.user.tossId = :tossId")
    Long countTotalVerifications(
            @Param("challengeId") Long challengeId,
            @Param("tossId") Long tossId
    );

    /**
     * 특정 챌린지와 사용자가 특정 날짜에 이미 인증했는지 확인합니다.
     * 1일 1회 인증 제한을 위해 사용됩니다.
     *
     * @param challengeId 챌린지 ID
     * @param userId 사용자 ID
     * @param date 확인할 날짜
     * @return 해당 날짜에 인증 기록이 있으면 true, 없으면 false
     */
    @Query("SELECT (COUNT(cv) > 0) FROM ChallengeVerification cv " +
           "WHERE cv.challenge.id = :challengeId " +
           "AND cv.user.tossId = :tossId " +
           "AND cv.date = :date")
    boolean existsByChallengeIdAndUserTossIdAndDate(
            @Param("challengeId") Long challengeId,
            @Param("tossId") Long tossId,
            @Param("date") LocalDate date
    );

    // 특정 챌린지와 사용자의 가장 최근 인증을 조회합니다.
    Optional<ChallengeVerification> findTopByChallengeIdAndUserTossIdOrderByDateDesc(Long challengeId, Long tossId);

    // 특정 챌린지의 모든 인증 데이터 중 가장 최근 인증을 조회합니다.
    Optional<ChallengeVerification> findTopByChallengeIdOrderByDateDescIdDesc(Long challengeId);

    /**
     * 특정 챌린지의 특정 사용자 인증 데이터를 최신순으로 페이징하여 조회합니다.
     * 무한 스크롤을 위해 Slice를 반환합니다.
     *
     * @param challengeId 챌린지 ID
     * @param userId 사용자 ID
     * @param pageable 페이징 정보 (page, size, sort)
     * @return Slice<ChallengeVerification> (다음 페이지 존재 여부 포함)
     */
    Slice<ChallengeVerification> findByChallengeIdAndUserTossIdOrderByDateDescIdDesc(
            Long challengeId, 
            Long tossId, 
            Pageable pageable
    );

    // 특정 챌린지의 모든 참여자 인증 데이터를 최신순으로 페이징하여 조회합니다.
    Slice<ChallengeVerification> findByChallengeIdOrderByDateDescIdDesc(
            Long challengeId,
            Pageable pageable
    );

    // 챌린지에 참여한 맴버들 총 인증 횟수 가져오기
    @Query("""
    SELECT new com.paynalty.domain.challengeverification
        .MembersVerificationCountResponse(
            u.id,
            u.name,
            COUNT(cv.id)
        )
    FROM ChallengeMember cm
    JOIN cm.user u
    LEFT JOIN ChallengeVerification cv
        ON cv.user = u AND cv.challenge = cm.challenge
    WHERE cm.challenge.id = :challengeId
    GROUP BY u.id, u.name
""")
    List<MembersVerificationCountResponse>
    countVerificationByChallengeMembers(@Param("challengeId") Long challengeId);

    /**
     * 챌린지에 참여한 멤버들의 특정 주간(월요일~일요일) 인증 횟수를 조회합니다.
     * 
     * @param challengeId 챌린지 ID
     * @param weekStart 주간 시작일 (월요일)
     * @param weekEnd 주간 종료일 (일요일)
     * @return 멤버별 주간 인증 횟수 리스트
     */
    @Query("""
    SELECT new com.paynalty.domain.challengeverification
        .MembersVerificationCountResponse(
            u.id,
            u.name,
            COUNT(cv.id)
        )
    FROM ChallengeMember cm
    JOIN cm.user u
    LEFT JOIN ChallengeVerification cv
        ON cv.user = u 
        AND cv.challenge = cm.challenge
        AND cv.date >= :weekStart
        AND cv.date <= :weekEnd
    WHERE cm.challenge.id = :challengeId
    GROUP BY u.id, u.name
""")
    List<MembersVerificationCountResponse>
    countWeeklyVerificationByChallengeMembers(
            @Param("challengeId") Long challengeId,
            @Param("weekStart") LocalDate weekStart,
            @Param("weekEnd") LocalDate weekEnd
    );

}
