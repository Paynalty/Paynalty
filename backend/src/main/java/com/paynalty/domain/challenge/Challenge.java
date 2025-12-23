package com.paynalty.domain.challenge;

import com.paynalty.domain.challengebank.ChallengeBank;
import com.paynalty.domain.challengemember.ChallengeMember;
import com.paynalty.domain.challengeverification.ChallengeVerification;
import com.paynalty.domain.user.User;
import com.paynalty.global.BaseTimeEntity;
import jakarta.persistence.*;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "challenges")
@Getter
@NoArgsConstructor
public class Challenge extends BaseTimeEntity {

    // 챌린지 상태 상수
    public static final String STATUS_PENDING = "pending";      // 시작 전
    public static final String STATUS_ACTIVE = "active";        // 진행 중
    public static final String STATUS_COMPLETE = "complete";  // 완료됨

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", length = 50, nullable = false)
    private String title;

    // 시작 일 - 종료일 + startOption에 의해 값 설정
    // 
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    // 종료 일
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;


    @Column(name = "penalty_amount")
    private Long penaltyAmount;

    @Column(name = "status", length = 20)
    private String status;


    // 요일 목록 (MON, TUE, WED, THU, FRI, SAT, SUN)
    @Column
    private List<DayOfWeekType> dayOfWeeks;

    // 인증 주기 - 주 몇 회
    @Column(name = "frequency")
    private Integer frequency;

    // 인증 가능 시작 시간 (시/분만 사용)
    @Column(name = "verify_start_at")
    private LocalTime verifyStartAt;

    // 인증 가능 종료 시간 (시/분만 사용)
    @Column(name = "verify_end_at")
    private LocalTime verifyEndAt;

    // 인증 타입 (PHOTO, TEXT, VOTE)
    @Enumerated(EnumType.STRING)
    @Column(name = "verification_type", length = 20)
    private VerificationType verificationType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder
    public Challenge(String title
            , LocalDate startDate, LocalDate endDate, Integer frequency
            , Long penaltyAmount, String status
            , VerificationType verificationType
            , User user
            , LocalTime verifyStartAt, LocalTime verifyEndAt
            , List<DayOfWeekType> dayOfWeeks){
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.frequency = frequency;
        this.penaltyAmount = penaltyAmount;
        this.status = status;
        this.verificationType = verificationType;
        this.user = user;
        this.verifyStartAt = verifyStartAt;
        this.verifyEndAt = verifyEndAt;
        this.dayOfWeeks = dayOfWeeks;
    }

    // 관계 설정
    // orphanRemoval 는 Many데이터를 사용하기위해 남기려면 orphanRemoval = false 설정하여, 부모삭제시에도 데이터 유지
    // 사용할 데이터가 아니면 굳이 설정할 필요 없음
    
    
    @OneToMany(mappedBy = "challenge", cascade = CascadeType.ALL)
    private List<ChallengeMember> challengeMembers = new ArrayList<>();

    @OneToMany(mappedBy = "challenge", cascade = CascadeType.ALL)
    private List<ChallengeVerification> challengeVerifications = new ArrayList<>();

    // Penalty는 이제 ChallengeMember를 통해 접근하므로 Challenge와의 직접 관계 제거
    // 벌금 내역은 ChallengeMember -> Penalty 경로로 조회 가능

    @OneToOne(mappedBy = "challenge", cascade = CascadeType.ALL)
    private ChallengeBank challengeBank;

    /**
     * 챌린지의 현재 상태를 시작일과 종료일을 기준으로 자동 계산합니다.
     * 
     * @return "pending" (시작 전), "progress" (진행 중), "completed" (완료됨)
     */
    public String calculateStatus() {
        return calculateStatus(this.startDate, this.endDate);
    }

    /**
     * 챌린지의 현재 상태를 시작일과 종료일을 기준으로 자동 계산합니다 (static 메서드).
     * 인스턴스 생성 전에도 사용 가능합니다.
     * 
     * @param startDate 시작일
     * @param endDate 종료일
     * @return STATUS_PENDING (시작 전), STATUS_ACTIVE (진행 중), STATUS_COMPLETED (완료됨)
     */
    public static String calculateStatus(LocalDate startDate, LocalDate endDate) {
        LocalDate today = LocalDate.now();
        
        if (today.isBefore(startDate)) {
            return STATUS_PENDING;  // 시작 전
        } else if (today.isAfter(endDate)) {
            return STATUS_COMPLETE;  // 완료됨
        } else {
            return STATUS_ACTIVE;  // 진행 중
        }
    }

}
