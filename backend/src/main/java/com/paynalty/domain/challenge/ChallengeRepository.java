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
     * 사용자가 참여한 챌린지 중 특정 상태의 챌린지를 조회합니다.
     * ChallengeMember를 통해 참여 여부를 확인합니다.
     *
     * @param userId 사용자 ID
     * @param status 챌린지 상태 (PENDING, ACTIVE, COMPLETE)
     * @return 사용자가 참여한 챌린지 목록
     */
    @Query("""
        SELECT DISTINCT c 
        FROM Challenge c
        JOIN FETCH c.user
        JOIN ChallengeMember cm ON cm.challenge = c
        WHERE cm.user.id = :userId
        AND c.status = :status
    """)
    List<Challenge> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") ChallengeStatus status);

    // WHERE start_date <= today AND end_date >= until과 동일
    List<Challenge> findByStartDateLessThanEqualAndEndDateGreaterThanEqual(LocalDate periodStartDate, LocalDate periodEndDate);

    List<Challenge> user(User user);


}

