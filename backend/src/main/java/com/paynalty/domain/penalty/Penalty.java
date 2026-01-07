package com.paynalty.domain.penalty;

import com.paynalty.domain.challengemember.ChallengeMember;
import com.paynalty.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "penalties")
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
    private Long fixedAmount;

    // 결제 관련 정보
    @Column(nullable = false)
    private Boolean paid = false;

    @Column
    private LocalDateTime paidAt;

    @Column
    private String paymentOrderId;

    @Builder
    public Penalty(ChallengeMember challengeMember, Long fixedAmount) {
        this.challengeMember = challengeMember;
        this.fixedAmount = fixedAmount;
        this.paid = false; // 기본값 false
        this.paidAt = null; // 기본값 null
        this.paymentOrderId = null; // 기본값 null
    }
}

