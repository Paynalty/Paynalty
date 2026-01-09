package com.paynalty.domain.challengewindow;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface ChallengeWindowRepository extends JpaRepository<ChallengeWindow, Long> {


    List<ChallengeWindow> findByChallengeIdInAndTossIdInAndChallengeWindowStartBetween(
            Set<Long> challengeIds,
            Set<Long> tossIds,
            LocalDateTime searchRangeStart,
            LocalDateTime searchRangeEnd
    );

    /**
     * PENDING 상태이고 인증 종료 시간이 지난 ChallengeWindow를 조회합니다.
     * Enforcer에서 사용됩니다.
     *
     * @param status PENDING 상태
     * @param now 현재 시간 (인증 종료 시간과 비교)
     * @return 조건에 맞는 ChallengeWindow 목록
     */
    @Query("SELECT cw FROM ChallengeWindow cw " +
           "WHERE cw.challengeWindowStatus = :status " +
           "AND cw.challengeWindowEnd <= :now")
    List<ChallengeWindow> findByChallengeWindowStatusAndChallengeWindowEndLessThanEqual(
            @Param("status") ChallengeWindowStatus status,
            @Param("now") LocalDateTime now
    );

    /**
     * 특정 챌린지 ID로 ChallengeWindow 목록을 조회합니다.
     * 챌린지 삭제 시 관련 ChallengeWindow를 먼저 삭제하기 위해 사용됩니다.
     *
     * @param challengeId 챌린지 ID
     * @return 해당 챌린지의 ChallengeWindow 목록
     */
    List<ChallengeWindow> findByChallengeId(Long challengeId);

}
