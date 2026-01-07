package com.paynalty.domain.challengeverification;

import com.paynalty.domain.challenge.Challenge;
import com.paynalty.domain.user.User;
import com.paynalty.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "challenge_verifications")
@Getter
@NoArgsConstructor  
public class ChallengeVerification extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id", nullable = false)
    private Challenge challenge;

    // 인증 주간 계산 등 비즈니스 로직용 날짜
    @Column(name = "verification_date", nullable = false)
    private java.time.LocalDate date;

    // 실제 인증 시각 (표시용)
    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;


    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Builder
    public ChallengeVerification(User user, Challenge challenge, java.time.LocalDate date, LocalDateTime verifiedAt, String imageUrl) {
        this.user = user;
        this.challenge = challenge;
        this.date = date;
        this.verifiedAt = verifiedAt;
        this.imageUrl = imageUrl;
    }

    // 인증 이미지 URL 수정
    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


}


