package com.paynalty.domain.user;

import com.paynalty.domain.challengemember.ChallengeMember;
import com.paynalty.domain.challengeverification.ChallengeVerification;
import com.paynalty.domain.penalty.Penalty;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "profile_image_url", length = 500, nullable = true)
    private String profileImageUrl;

    @Column(name = "toss_id")
    private Long tossId;

    @Column(name = "nickname", length = 50)
    private String nickname;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public User(String profileImageUrl,Long tossId, String nickname ){
        this.profileImageUrl = profileImageUrl;
        this.tossId = tossId;
        this.nickname = nickname;
        this.createdAt = LocalDateTime.now();
    }


    // 관계 설정
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<ChallengeMember> challengeMembers = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<ChallengeVerification> challengeVerifications = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Penalty> penalties = new ArrayList<>();
}

