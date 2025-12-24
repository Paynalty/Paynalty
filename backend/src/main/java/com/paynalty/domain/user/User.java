package com.paynalty.domain.user;

import com.paynalty.domain.challengemember.ChallengeMember;
import com.paynalty.domain.challengeverification.ChallengeVerification;
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

    @Column(name = "toss_id")
    private Long tossId;

    @Column
    private String name;

    @Column
    private String phoneNum;

    @Column
    private String email;

    @Builder
    public User(String email, Long tossId, String name, String phoneNum ){
        this.email = email;
        this.tossId = tossId;
        this.name = name;
        this.phoneNum = phoneNum;

    }


    // 관계 설정
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<ChallengeMember> challengeMembers = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<ChallengeVerification> challengeVerifications = new ArrayList<>();

    // Penalty는 이제 ChallengeMember를 통해 접근하므로 User와의 직접 관계 제거
    // 벌금 내역은 ChallengeMember -> Penalty 경로로 조회 가능
}

