package com.paynalty.domain.challengeverification;

import com.paynalty.domain.challenge.Challenge;
import com.paynalty.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "challenge_verification")
@Getter
@Setter
@NoArgsConstructor  
public class ChallengeVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id", nullable = false)
    private Challenge challenge;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "status", length = 20)
    private String status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}

