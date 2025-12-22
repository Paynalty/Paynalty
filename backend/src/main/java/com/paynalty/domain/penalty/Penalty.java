package com.paynalty.domain.penalty;

import com.paynalty.domain.challengemember.ChallengeMember;
import com.paynalty.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "penalty")
@Getter
@Setter
@NoArgsConstructor
public class Penalty extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_member_id", nullable = false)
    private ChallengeMember challengeMember;

    @Column(nullable = false)
    private Integer fixedAmount;

    @Builder
    public Penalty(ChallengeMember challengeMember, Integer fixedAmount) {
        this.challengeMember = challengeMember;
        this.fixedAmount = fixedAmount;
    }
}

