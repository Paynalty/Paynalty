package com.paynalty.domain.challenge;

import com.paynalty.domain.challengebank.ChallengeBank;
import com.paynalty.domain.challengemember.ChallengeMember;
import com.paynalty.domain.challengeverification.ChallengeVerification;
import com.paynalty.domain.penalty.Penalty;
import jakarta.persistence.*;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "challenges")
@Getter
@NoArgsConstructor
public class Challenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", length = 50, nullable = false)
    private String title;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "category", length = 30)
    private String category;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "frequency")
    private Integer frequency;

    @Column(name = "penalty_amount")
    private Integer penaltyAmount;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;


    // 테스트용 userId
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Builder
    public Challenge(String title, String description, String category
            , LocalDate startDate, LocalDate endDate, Integer frequency
            , Integer penaltyAmount, String status
            ,Long userId){
        this.title = title;
        this.description = description;
        this.category = category;
        this.startDate = startDate;
        this.endDate = endDate;
        this.frequency = frequency;
        this.penaltyAmount = penaltyAmount;
        this.status = status;
        this.createdAt = LocalDateTime.now();
        this.userId = userId;
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
