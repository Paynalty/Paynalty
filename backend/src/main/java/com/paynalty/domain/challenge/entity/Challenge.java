package com.example.paynalty.domain.challenge.entity;

import com.example.paynalty.domain.challengebank.entity.ChallengeBank;
import com.example.paynalty.domain.challengemember.entity.ChallengeMember;
import com.example.paynalty.domain.challengeverification.entity.ChallengeVerification;
import com.example.paynalty.domain.penalty.entity.Penalty;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "challenge")
@Getter
@Setter
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

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 관계 설정
    @OneToMany(mappedBy = "challenge", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChallengeMember> challengeMembers = new ArrayList<>();

    @OneToMany(mappedBy = "challenge", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChallengeVerification> challengeVerifications = new ArrayList<>();

    @OneToMany(mappedBy = "challenge", cascade = CascadeType.ALL, orphanRemoval = true)     
    private List<Penalty> penalties = new ArrayList<>();

    @OneToOne(mappedBy = "challenge", cascade = CascadeType.ALL, orphanRemoval = true)
    private ChallengeBank challengeBank;
}

