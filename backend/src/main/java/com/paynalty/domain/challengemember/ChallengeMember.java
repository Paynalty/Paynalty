package com.paynalty.domain.challengemember;

import com.paynalty.domain.challenge.Challenge;
import com.paynalty.domain.challenge.ChallengeStatus;
import com.paynalty.domain.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "challenge_members")
@Getter
@Setter
@NoArgsConstructor
public class ChallengeMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id", nullable = false)
    private Challenge challenge;

    @CreationTimestamp
    @Column(name = "joined_at", nullable = false, updatable = false)
    private LocalDateTime joinedAt;

    // 당일 인증 여부 - enum으로 교체. 테스트 위해 ChallengeStatus 사용
//    @Column(name = "is_success", length = 20)
//    private String isSuccess;

    @Enumerated(EnumType.STRING)
    @Column
    private ChallengeStatus isSuccess;

    @Column(name = "end_at")
    private LocalDate endAt;

    // 역할 : 생성자 or 참가자
    @Column(name = "role")
    private MemberRole role;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Builder
    public ChallengeMember(User user, Challenge challenge,ChallengeStatus isSuccess, LocalDate endAt, MemberRole role){
        this.user = user;
        this.challenge = challenge;
        this.joinedAt = LocalDateTime.now();
        this.isSuccess = isSuccess;
        this.role = role;
        this.endAt = endAt;
        this.isActive = true;
    }

    public void withdraw() {
        this.isActive = false;
    }

    public void rejoin() {
        this.isActive = true;
    }
}

