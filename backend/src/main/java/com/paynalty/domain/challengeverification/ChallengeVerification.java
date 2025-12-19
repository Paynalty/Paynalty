package com.paynalty.domain.challengeverification;

import com.paynalty.domain.challenge.Challenge;
import com.paynalty.domain.user.User;
import com.paynalty.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "challenge_verification")
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

    // 인증 날짜(월,화,수...)
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "status", length = 20)
    private String status;


    @Builder
    public ChallengeVerification(User user, Challenge challenge, String imageUrl, String status) {
        this.user = user;
        this.challenge = challenge;
        this.date = LocalDate.now();
        this.imageUrl = imageUrl;
        this.status = status;
    }


}


