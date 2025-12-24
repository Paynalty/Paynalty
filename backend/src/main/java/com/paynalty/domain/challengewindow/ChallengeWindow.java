package com.paynalty.domain.challengewindow;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name="challenge_windows")
@NoArgsConstructor
public class ChallengeWindow {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "cw_seq")
    @TableGenerator(
            name = "cw_seq",
            table = "hibernate_sequences",
            pkColumnName = "sequence_name",
            valueColumnName = "next_val",
            pkColumnValue = "challenge_window",
            allocationSize = 50
    )
    private Long id;

    private Long challengeId;

    private Long userId;

    private LocalDateTime challengeWindowStart;

    private LocalDateTime challengeWindowEnd;

    @Enumerated(EnumType.STRING)
    private ChallengeWindowStatus challengeWindowStatus;

    @Builder
    public ChallengeWindow(Long challengeId, Long userId, LocalDateTime challengeWindowStart, LocalDateTime challengeWindowEnd, ChallengeWindowStatus challengeWindowStatus) {
        this.challengeId = challengeId;
        this.userId = userId;
        this.challengeWindowStart = challengeWindowStart;
        this.challengeWindowEnd = challengeWindowEnd;
        this.challengeWindowStatus = challengeWindowStatus;
    }

}
