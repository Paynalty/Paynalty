package com.paynalty.domain.challenge;

import com.paynalty.domain.challengebank.ChallengeBank;
import com.paynalty.domain.challengemember.ChallengeMember;
import com.paynalty.domain.challengeverification.ChallengeVerification;
import com.paynalty.domain.penalty.Penalty;
import com.paynalty.domain.user.User;
import com.paynalty.global.BaseTimeEntity;
import jakarta.persistence.*;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "challenges")
@Getter
@NoArgsConstructor
public class Challenge extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", length = 50, nullable = false)
    private String title;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "category", length = 30)
    private String category;

    // 시작 일
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    // 종료 일
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;


    @Column(name = "penalty_amount")
    private int penaltyAmount;

    @Column(name = "status", length = 20)
    private String status;

    // 인증 주기 - 주 몇 회
    @Column(name = "frequency")
    private int frequency;

    // 인증 가능 시작 시간 (시/분만 사용)
    @Column(name = "verify_start_at")
    private LocalTime verifyStartAt;

    // 인증 가능 종료 시간 (시/분만 사용)
    @Column(name = "verify_end_at")
    private LocalTime verifyEndAt;

    @Column(name = "verify_count")
    private int verifyCount;

    // 인증 방식. (사진,텍스트,체크)
    @Enumerated(EnumType.STRING)
    @Column(name = "verification_type", length = 20)
    private VerificationType verificationType;

    // 테스트용 userId
//    @Column(name = "user_id", nullable = false)
//    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder
    public Challenge(String title, String description, String category
            , LocalDate startDate, LocalDate endDate, Integer frequency
            , Integer penaltyAmount, String status
            , VerificationType verificationType
            , User user
            , LocalTime verifyStartAt, LocalTime verifyEndAt){
        this.title = title;
        this.description = description;
        this.category = category;
        this.startDate = startDate;
        this.endDate = endDate;
        this.frequency = frequency;
        this.penaltyAmount = penaltyAmount;
        this.status = status;
        this.user = user;
        this.verificationType = verificationType;
        this.verifyStartAt = verifyStartAt;
        this.verifyEndAt = verifyEndAt;
    }

    // 관계 설정
    // orphanRemoval 는 Many데이터를 사용하기위해 남기려면 orphanRemoval = false 설정하여, 부모삭제시에도 데이터 유지
    // 사용할 데이터가 아니면 굳이 설정할 필요 없음
    
    
    @OneToMany(mappedBy = "challenge", cascade = CascadeType.ALL)
    private List<ChallengeMember> challengeMembers = new ArrayList<>();

    @OneToMany(mappedBy = "challenge", cascade = CascadeType.ALL)
    private List<ChallengeVerification> challengeVerifications = new ArrayList<>();

    // 첼린지가 삭제되더라고 자신이 여태 지불한 벌금 내역이 알고싶다면 orphanRemoval 설정 필요
    @OneToMany(mappedBy = "challenge", cascade = CascadeType.ALL)
    private List<Penalty> penalties = new ArrayList<>();

    @OneToOne(mappedBy = "challenge", cascade = CascadeType.ALL)
    private ChallengeBank challengeBank;

}
