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

    private Long tossId;

    private LocalDateTime challengeWindowStart;

    private LocalDateTime challengeWindowEnd;

    @Enumerated(EnumType.STRING)
    private ChallengeWindowStatus challengeWindowStatus;

    @Builder
    public ChallengeWindow(Long challengeId, Long tossId, LocalDateTime challengeWindowStart, LocalDateTime challengeWindowEnd, ChallengeWindowStatus challengeWindowStatus) {
        this.challengeId = challengeId;
        this.tossId = tossId;
        this.challengeWindowStart = challengeWindowStart;
        this.challengeWindowEnd = challengeWindowEnd;
        this.challengeWindowStatus = challengeWindowStatus;
    }

    /**
     * ChallengeWindow의 상태를 변경합니다.
     * Enforcer에서 사용됩니다.
     *
     * @param status 변경할 상태
     */
    public void setChallengeWindowStatus(ChallengeWindowStatus status) {
        this.challengeWindowStatus = status;
    }

}
