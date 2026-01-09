package com.paynalty.domain.challenge;

import com.paynalty.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
    /**
     * 사용자가 참여한 챌린지를 조회합니다.
     * ChallengeMember를 통해 참여 여부를 확인합니다.
     * 상태 필터링은 Service 레이어에서 계산된 상태를 기준으로 수행합니다.
     *
     * @param tossId 사용자 토스 ID
     * @return 사용자가 참여한 챌린지 목록
     */
    @Query("""
        SELECT DISTINCT c 
        FROM Challenge c
        JOIN FETCH c.user
        JOIN ChallengeMember cm ON cm.challenge = c
        WHERE cm.user.tossId = :tossId
        AND cm.isActive = true
    """)
    List<Challenge> findByUserTossId(@Param("tossId") Long tossId);

    // WHERE start_date <= today AND end_date >= until과 동일
    List<Challenge> findByStartDateLessThanEqualAndEndDateGreaterThanEqual(LocalDate periodStartDate, LocalDate periodEndDate);

    List<Challenge> user(User user);


}

