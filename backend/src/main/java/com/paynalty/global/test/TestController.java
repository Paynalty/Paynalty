package com.paynalty.global.test;

import com.paynalty.domain.challenge.Challenge;
import com.paynalty.domain.challenge.ChallengeRepository;
import com.paynalty.domain.challenge.ChallengeStatus;
import com.paynalty.domain.challenge.DayOfWeekType;
import com.paynalty.domain.challenge.VerificationType;
import com.paynalty.domain.challengemember.ChallengeMember;
import com.paynalty.domain.challengemember.ChallengeMemberRepository;
import com.paynalty.domain.challengemember.MemberRole;
import com.paynalty.domain.challengeverification.ChallengeVerification;
import com.paynalty.domain.challengeverification.ChallengeVerificationRepository;
import com.paynalty.domain.challengewindow.ChallengeWindow;
import com.paynalty.domain.challengewindow.ChallengeWindowRepository;
import com.paynalty.domain.challengewindow.ChallengeWindowStatus;
import com.paynalty.domain.penalty.Penalty;
import com.paynalty.domain.penalty.PenaltyRepository;
import com.paynalty.domain.user.User;
import com.paynalty.domain.user.UserRepository;
import com.paynalty.global.error.CustomException;
import com.paynalty.global.error.UserErrorCode;
import com.paynalty.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Tag(name = "Test", description = "개발 및 테스트용 API")
@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    private final UserRepository userRepository;
    private final ChallengeRepository challengeRepository;
    private final ChallengeMemberRepository challengeMemberRepository;
    private final ChallengeVerificationRepository challengeVerificationRepository;
    private final PenaltyRepository penaltyRepository;
    private final ChallengeWindowRepository challengeWindowRepository;

    @Operation(
            summary = "더미 데이터 생성",
            description = "현재 로그인한 사용자를 주축으로 7가지 다양한 패턴(요일, 시간, 상태)의 챌린지와 멤버, 인증 데이터를 생성합니다.\n\n" +
                    "🔐 JWT 토큰 인증 필수"
    )
    @PostMapping("/dummy-data")
    @Transactional
    public ResponseEntity<String> generateDummyData(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long tossId = userDetails.getUser().getTossId();
        User currentUser = userRepository.findByTossId(tossId)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        // 1. 추가 더미 유저 생성 (없을 경우)
        if (userRepository.count() < 15) {
            for (int i = 1; i <= 5; i++) {
                Long dummyTossId = 9000L + i;
                if (userRepository.findByTossId(dummyTossId).isEmpty()) {
                    userRepository.save(User.builder()
                            .name("더미유저" + i)
                            .phoneNum("010-9999-000" + i)
                            .email("dummy" + i + "@example.com")
                            .tossId(dummyTossId)
                            .build());
                }
            }
        }
        List<User> allUsers = userRepository.findAll();
        LocalDate today = LocalDate.now();

        // 챌린지 생성 패턴 (DataInitializer의 로직 이식)

        // C1: PENDING - 건강한 아침 식사하기
        Challenge c1 = createChallenge(currentUser, "건강한 아침 식사하기",
                today.plusDays(3), today.plusDays(24), 3, 10000L,
                LocalTime.of(9, 0), LocalTime.of(18, 0),
                List.of(DayOfWeekType.MON, DayOfWeekType.WED, DayOfWeekType.FRI));
        addMembers(c1, allUsers, currentUser);

        // C2: ACTIVE - 주 3회 조깅
        Challenge c2 = createChallenge(currentUser, "주 3회 조깅하기",
                today.minusDays(21), today.plusDays(14), 3, 10000L,
                LocalTime.of(9, 0), LocalTime.of(18, 0),
                List.of(DayOfWeekType.MON, DayOfWeekType.WED, DayOfWeekType.FRI));
        addMembers(c2, allUsers, currentUser);
        createVerifications(c2, allUsers, today, currentUser);
        createPenalties(c2, allUsers, currentUser, today); // 벌금 더미 데이터

        // C3: ACTIVE - 하루 한 페이지 일기 쓰기 (저녁)
        Challenge c3 = createChallenge(currentUser, "하루 한 페이지 일기 쓰기",
                today.minusDays(21), today.plusDays(14), 3, 10000L,
                LocalTime.of(18, 0), LocalTime.of(23, 59),
                List.of(DayOfWeekType.MON, DayOfWeekType.WED, DayOfWeekType.FRI));
        addMembers(c3, allUsers, currentUser);
        createVerifications(c3, allUsers, today, currentUser);
        createPenalties(c3, allUsers, currentUser, today); // 벌금 더미 데이터

        // C4: ACTIVE - 매일 물 2L 마시기 (매일)
        Challenge c4 = createChallenge(currentUser, "매일 물 2L 마시기",
                today.minusDays(21), today.plusDays(14), 7, 5000L,
                LocalTime.of(0, 0), LocalTime.of(23, 59),
                List.of(DayOfWeekType.MON, DayOfWeekType.TUE, DayOfWeekType.WED, DayOfWeekType.THU, DayOfWeekType.FRI, DayOfWeekType.SAT, DayOfWeekType.SUN));
        addMembers(c4, allUsers, currentUser);
        createVerifications(c4, allUsers, today, currentUser);
        createPenalties(c4, allUsers, currentUser, today); // 벌금 더미 데이터

        // C5: COMPLETE - 영어 단어 외우기
        Challenge c5 = createChallenge(currentUser, "지난달 영어 단어 외우기",
                today.minusDays(40), today.minusDays(10), 5, 20000L,
                LocalTime.of(9, 0), LocalTime.of(22, 0),
                List.of(DayOfWeekType.MON, DayOfWeekType.TUE, DayOfWeekType.WED, DayOfWeekType.THU, DayOfWeekType.FRI));
        addMembers(c5, allUsers, currentUser);
        createVerificationsForCompleted(c5, allUsers, currentUser);
        createPenalties(c5, allUsers, currentUser, today); // 벌금 더미 데이터

        // C6: ACTIVE - 스쿼트 50개 (화목토일)
        Challenge c6 = createChallenge(currentUser, "스쿼트 50개",
                today.minusDays(21), today.plusDays(14), 4, 15000L,
                LocalTime.of(9, 0), LocalTime.of(18, 0),
                List.of(DayOfWeekType.TUE, DayOfWeekType.THU, DayOfWeekType.SAT, DayOfWeekType.SUN));
        addMembers(c6, allUsers, currentUser);
        createVerifications(c6, allUsers, today, currentUser);
        createPenalties(c6, allUsers, currentUser, today); // 벌금 더미 데이터

        // C7: ACTIVE - 다른 사용자가 만든 챌린지 (참여중)
        User otherCreator = allUsers.stream()
                .filter(u -> !u.getTossId().equals(currentUser.getTossId()))
                .findFirst()
                .orElse(allUsers.get(0));

        Challenge c7 = createChallenge(otherCreator, "더미: 친구가 만든 챌린지",
                today.minusDays(5), today.plusDays(25), 7, 30000L,
                LocalTime.of(7, 0), LocalTime.of(23, 0),
                List.of(DayOfWeekType.MON, DayOfWeekType.TUE, DayOfWeekType.WED, DayOfWeekType.THU, DayOfWeekType.FRI));

        //         멤버 추가: 생성자(친구) + 본인(currentUser)
        challengeMemberRepository.save(ChallengeMember.builder()
                .user(otherCreator)
                .challenge(c7)
                .role(MemberRole.CREATOR)
                .isSuccess(c7.getStatus())
                .build());

        challengeMemberRepository.save(ChallengeMember.builder()
                .user(currentUser)
                .challenge(c7)
                .role(MemberRole.CHALLENGER)
                .isSuccess(c7.getStatus())
                .build());

        createVerifications(c7, allUsers, today, currentUser);
        createPenalties(c7, allUsers, currentUser, today);

        // ChallengeWindow 더미 데이터 생성
        createChallengeWindows(currentUser, today);

        return ResponseEntity.ok("테스트 유저(" + tossId + ")를 위한 풍부한 더미 데이터(챌린지 7개, 인증 내역, 벌금 내역, ChallengeWindow) 생성이 완료되었습니다.");
    }

    private Challenge createChallenge(User creator, String title, LocalDate start, LocalDate end, int freq, Long penalty, LocalTime vStart, LocalTime vEnd, List<DayOfWeekType> days) {
        return challengeRepository.save(Challenge.builder()
                .title(title)
                .startDate(start)
                .endDate(end)
                .frequency(freq)
                .penaltyAmount(penalty)
                .status(Challenge.calculateStatus(start, end))
                .verificationType(VerificationType.PHOTO)
                .verifyStartAt(vStart)
                .verifyEndAt(vEnd)
                .daysOfWeek(days)
                .user(creator)
                .build());
    }

    private void addMembers(Challenge challenge, List<User> allUsers, User currentUser) {
        // 생성자 본인 추가
        challengeMemberRepository.save(ChallengeMember.builder()
                .user(currentUser)
                .challenge(challenge)
                .role(MemberRole.CREATOR)
                .isSuccess(challenge.getStatus())
                .build());

        // 다른 유저 2~3명 추가
        allUsers.stream()
                .filter(u -> !u.getTossId().equals(currentUser.getTossId()))
                .limit(3)
                .forEach(u -> challengeMemberRepository.save(ChallengeMember.builder()
                        .user(u)
                        .challenge(challenge)
                        .role(MemberRole.CHALLENGER)
                        .isSuccess(challenge.getStatus())
                        .build()));
    }

    private void createVerifications(Challenge challenge, List<User> allUsers, LocalDate today, User currentUser) {
        // 시작일부터 어제까지의 인증 데이터
        for (LocalDate date = challenge.getStartDate(); date.isBefore(today); date = date.plusDays(1)) {
            final LocalDate d = date;

            // 1. 현재 사용자(본인) 인증 추가 (어제까지만)
            challengeVerificationRepository.save(ChallengeVerification.builder()
                    .user(currentUser)
                    .challenge(challenge)
                    .date(d)
                    .verifiedAt(d.atTime(10, 30)) // 오전 10시 30분
                    .imageUrl("temp.png")
                    .build());

            // 2. 다른 더미 유저들 인증 추가 (최대 2명)
            allUsers.stream()
                    .filter(u -> !u.getTossId().equals(currentUser.getTossId()))
                    .limit(2)
                    .forEach(u -> challengeVerificationRepository.save(ChallengeVerification.builder()
                            .user(u)
                            .challenge(challenge)
                            .date(d)
                            .verifiedAt(d.atTime(11, 45)) // 오전 11시 45분
                            .imageUrl("temp.png")
                            .build()));
        }
    }

    private void createVerificationsForCompleted(Challenge challenge, List<User> allUsers, User currentUser) {
        for (LocalDate date = challenge.getStartDate(); !date.isAfter(challenge.getEndDate()); date = date.plusDays(1)) {
            final LocalDate d = date;

            // 본인 포함 모든 멤버 인증 기록 생성
            challengeVerificationRepository.save(ChallengeVerification.builder()
                    .user(currentUser)
                    .challenge(challenge)
                    .date(d)
                    .verifiedAt(d.atTime(13, 0)) // 오후 1시
                    .imageUrl("temp.png")

                    .build());

            allUsers.stream()
                    .filter(u -> !u.getTossId().equals(currentUser.getTossId()))
                    .limit(2)
                    .forEach(u -> challengeVerificationRepository.save(ChallengeVerification.builder()
                            .user(u)
                            .challenge(challenge)
                            .date(d)
                            .verifiedAt(d.atTime(14, 20)) // 오후 2시 20분
                            .imageUrl("temp.png")
                            .build()));
        }
    }

    /**
     * 벌금 더미 데이터 생성
     * ACTIVE와 COMPLETE 챌린지에 대해 다양한 시나리오의 벌금 데이터를 생성합니다.
     *
     * @param challenge 챌린지
     * @param allUsers 전체 사용자 목록
     * @param currentUser 현재 로그인한 사용자
     * @param today 오늘 날짜
     */
    private void createPenalties(Challenge challenge, List<User> allUsers, User currentUser, LocalDate today) {
        // 1. 본인(currentUser)의 벌금 생성
        ChallengeMember currentMember = challengeMemberRepository
                .findByChallengeIdAndUserTossIdWithFetch(challenge.getId(), currentUser.getTossId())
                .orElse(null);

        if (currentMember != null) {
            Long penaltyAmount = challenge.getPenaltyAmount();

            // 미납 벌금들 (paid = false)
            // 오늘 벌금
            createPenaltyForMember(currentMember, penaltyAmount, today, false, null);

            // 어제 벌금
            createPenaltyForMember(currentMember, penaltyAmount, today.minusDays(1), false, null);

            // 3일 전 벌금
            createPenaltyForMember(currentMember, penaltyAmount, today.minusDays(3), false, null);

            // 7일 전 벌금
            createPenaltyForMember(currentMember, penaltyAmount, today.minusDays(7), false, null);

            // 결제 완료 벌금들 (paid = true, paidAt 설정)
            // 5일 전 벌금 (결제 완료) - 벌금 발생 다음날 결제
            LocalDateTime paidAt5 = today.minusDays(5).atTime(14, 30);
            createPenaltyForMember(currentMember, penaltyAmount, today.minusDays(6), true, paidAt5);

            // 10일 전 벌금 (결제 완료) - 벌금 발생 다음날 결제
            LocalDateTime paidAt10 = today.minusDays(10).atTime(16, 0);
            createPenaltyForMember(currentMember, penaltyAmount, today.minusDays(11), true, paidAt10);
        }

        // 2. 다른 멤버들의 벌금 생성 (미납 위주)
        List<User> otherUsers = allUsers.stream()
                .filter(u -> !u.getTossId().equals(currentUser.getTossId()))
                .limit(2)
                .toList();

        for (int i = 0; i < otherUsers.size(); i++) {
            User otherUser = otherUsers.get(i);
            ChallengeMember member = challengeMemberRepository
                    .findByChallengeIdAndUserTossIdWithFetch(challenge.getId(), otherUser.getTossId())
                    .orElse(null);

            if (member != null) {
                Long penaltyAmount = challenge.getPenaltyAmount();

                if (i == 0) {
                    // 다른 멤버 1: 2일 전, 4일 전 미납 벌금
                    createPenaltyForMember(member, penaltyAmount, today.minusDays(2), false, null);
                    createPenaltyForMember(member, penaltyAmount, today.minusDays(4), false, null);
                } else if (i == 1) {
                    // 다른 멤버 2: 6일 전, 8일 전 미납 벌금
                    createPenaltyForMember(member, penaltyAmount, today.minusDays(6), false, null);
                    createPenaltyForMember(member, penaltyAmount, today.minusDays(8), false, null);
                }
            }
        }
    }

    /**
     * 특정 멤버에 대한 벌금 생성
     *
     * @param member 챌린지 멤버
     * @param amount 벌금 금액
     * @param createdDate 벌금 생성 날짜
     * @param paid 결제 여부
     * @param paidAt 결제 시간 (paid가 true인 경우 필수)
     */
    private void createPenaltyForMember(ChallengeMember member, Long amount,
                                       LocalDate createdDate, boolean paid, LocalDateTime paidAt) {
        Penalty penalty = Penalty.builder()
                .challengeMember(member)
                .penaltyAmount(amount)
                .build();

        Penalty saved = penaltyRepository.save(penalty);

        // createdAt 수동 설정 (더미 데이터를 위해 과거 날짜로 설정)
        saved.setCreatedAt(createdDate.atTime(12, 0)); // 정오로 설정

        // paid 상태 설정
        if (paid && paidAt != null) {
            saved.setPaid(true);
            saved.setPaidAt(paidAt);
        }

        penaltyRepository.save(saved);
    }

    // ----- 01-09 수정 내용

    /**
     * ChallengeWindow 더미 데이터 생성
     * ACTIVE 챌린지의 멤버들에게 다양한 상태의 윈도우를 생성합니다.
     *
     * @param currentUser 현재 로그인한 사용자
     * @param today 오늘 날짜
     */
    private void createChallengeWindows(User currentUser, LocalDate today) {
        // ACTIVE 상태의 챌린지 하나 선택
        List<Challenge> activeChallenges = challengeRepository.findAll().stream()
                .filter(c -> c.getStatus() == ChallengeStatus.ACTIVE)
                .limit(1)
                .toList();

        if (activeChallenges.isEmpty()) {
            return;
        }

        Challenge challenge = activeChallenges.get(0);
        List<ChallengeMember> members = challengeMemberRepository.findByChallengeId(challenge.getId()).stream()
                .limit(2)  // 멤버 2명만 선택
                .toList();

        if (members.isEmpty()) {
            return;
        }

        // 각 멤버에게 윈도우 생성
        for (ChallengeMember member : members) {
            Long tossId = member.getUser().getTossId();
            LocalTime verifyStart = challenge.getVerifyStartAt();
            LocalTime verifyEnd = challenge.getVerifyEndAt();

            // 오늘 윈도우 (PENDING)
            challengeWindowRepository.save(ChallengeWindow.builder()
                    .challengeId(challenge.getId())
                    .tossId(tossId)
                    .challengeWindowStart(today.atTime(verifyStart))
                    .challengeWindowEnd(today.atTime(verifyEnd))
                    .challengeWindowStatus(ChallengeWindowStatus.PENDING)
                    .build());

            // 어제 윈도우 (SUCCESS)
            challengeWindowRepository.save(ChallengeWindow.builder()
                    .challengeId(challenge.getId())
                    .tossId(tossId)
                    .challengeWindowStart(today.minusDays(1).atTime(verifyStart))
                    .challengeWindowEnd(today.minusDays(1).atTime(verifyEnd))
                    .challengeWindowStatus(ChallengeWindowStatus.SUCCESS)
                    .build());

            // 그제 윈도우 (FAIL)
            challengeWindowRepository.save(ChallengeWindow.builder()
                    .challengeId(challenge.getId())
                    .tossId(tossId)
                    .challengeWindowStart(today.minusDays(2).atTime(verifyStart))
                    .challengeWindowEnd(today.minusDays(2).atTime(verifyEnd))
                    .challengeWindowStatus(ChallengeWindowStatus.FAIL)
                    .build());
        }
    }
}
