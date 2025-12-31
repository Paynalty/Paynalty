package com.paynalty.domain.challengeverification;

import com.paynalty.domain.challenge.Challenge;
import com.paynalty.domain.user.User;
import com.paynalty.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

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

    // 인증 날짜 (비즈니스 로직용, createdAt과 별개)
    @Column(name = "verification_date", nullable = false)
    private java.time.LocalDate date;


    @Column(name = "image_url", length = 500)
    private String imageUrl;

    // 대기중
    @Column(name = "status", length = 20)
    private VerificationStatus status;


    @Builder
    public ChallengeVerification(User user, Challenge challenge, java.time.LocalDate date, String imageUrl, VerificationStatus status) {
        this.user = user;
        this.challenge = challenge;
        this.date = date;
        this.imageUrl = imageUrl;
        this.status = status;
    }

    // 인증 이미지 URL 수정
    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


}


