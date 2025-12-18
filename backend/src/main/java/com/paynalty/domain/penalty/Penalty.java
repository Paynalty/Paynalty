package com.paynalty.domain.penalty;

import com.paynalty.domain.challenge.Challenge;
import com.paynalty.domain.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "penalty")
@Getter
@Setter
@NoArgsConstructor
public class Penalty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id", nullable = false)
    private Challenge challenge;

    @Column(name = "amount", nullable = false)
    private Integer amount;

    @Column(name = "reason", length = 255)
    private String reason;

    /**
     * 벌금 결제 상태
     * - false: 아직 미결제 (예치만 된 상태 또는 결제 전)
     * - true : 결제 완료 (토스 결제 성공 및 검증 완료)
     */
    @Column(name = "paid", nullable = false)
    private boolean paid = false;

    /**
     * 실제 결제가 완료된 시각
     */
    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    /**
     * 토스 결제 아이디
     * - 토스 쪽 결제 결과와 매칭하기 위한 값
     */
    @Column(name = "payment_order_id", length = 100)
    private String paymentOrderId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}

