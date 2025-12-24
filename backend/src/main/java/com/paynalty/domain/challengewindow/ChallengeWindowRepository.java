package com.paynalty.domain.challengewindow;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface ChallengeWindowRepository extends JpaRepository<ChallengeWindow, Long> {


    List<ChallengeWindow> findByChallengeIdInAndUserIdInAndWindowStartBetween(
            Set challengeIds, Set userIds, LocalDateTime searchRangeStart, LocalDateTime searchRangeEnd
    );

}
