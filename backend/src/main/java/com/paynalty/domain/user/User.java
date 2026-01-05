package com.paynalty.domain.user;

import com.paynalty.domain.challengemember.ChallengeMember;
import com.paynalty.domain.challengeverification.ChallengeVerification;
import jakarta.persistence.*;
import lombok.*;
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

    @Column(name = "toss_id")
    private Long tossId;

    @Column
    private String name;

    @Column
    private String phoneNum;

    @Column
    private String email;

    @Column(name = "refresh_token", length = 1000)
    private String refreshToken;

    @Builder
    public User(String email, Long tossId, String name, String phoneNum ){
        this.email = email;
        this.tossId = tossId;
        this.name = name;
        this.phoneNum = phoneNum;

    }

    public void updateTokens(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void updateUserInfo(String name, String phoneNum, String email) {
        if (name != null) {
            this.name = name;
        }
        if (phoneNum != null) {
            this.phoneNum = phoneNum;
        }
        if (email != null) {
            this.email = email;
        }
    }

    // 관계 설정
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<ChallengeMember> challengeMembers = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<ChallengeVerification> challengeVerifications = new ArrayList<>();
}

