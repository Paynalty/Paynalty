package com.paynalty.domain.challengebank;

import com.paynalty.domain.challenge.Challenge;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "challenge_bank")
@Getter
@Setter
@NoArgsConstructor
public class ChallengeBank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id", nullable = false, unique = true)
    private Challenge challenge;

    @Column(name = "total_amount", nullable = false)
    private Integer totalAmount = 0;

    @Column(name = "status", length = 20)
    private String status;
}

