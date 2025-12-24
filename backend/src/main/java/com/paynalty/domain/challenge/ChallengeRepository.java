package com.paynalty.domain.challenge;

import com.paynalty.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
    @Query("SELECT c FROM Challenge c JOIN FETCH c.user u WHERE u.id = :id AND c.status = :status")
    List<Challenge> findByEmailAndStatus(@Param("id") Long id, @Param("status") ChallengeStatus status);


    List<Challenge> user(User user);

    @Query("SELECT cv FROM ChallengeVerification cv WHERE cv.challenge.id = :challengeId AND cv.user.id = :userId")
    Boolean findByChallengeIdAndUserId(@Param("challengeId") Long challengeId, @Param("userId") Long userId);
}

