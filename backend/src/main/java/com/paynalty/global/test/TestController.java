package com.paynalty.global.test;

import com.paynalty.domain.challenge.*;
import com.paynalty.domain.challengemember.ChallengeMember;
import com.paynalty.domain.challengemember.ChallengeMemberRepository;
import com.paynalty.domain.challengemember.MemberRole;
import com.paynalty.domain.challengeverification.ChallengeVerification;
import com.paynalty.domain.challengeverification.ChallengeVerificationRepository;
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
        Challenge c1 = createChallenge(currentUser, "더미: 건강한 아침 식사하기",
                today.plusDays(3), today.plusDays(24), 3, 10000L,
                LocalTime.of(9, 0), LocalTime.of(18, 0),
                List.of(DayOfWeekType.MON, DayOfWeekType.WED, DayOfWeekType.FRI));
        addMembers(c1, allUsers, currentUser);

        // C2: ACTIVE - 주 3회 조깅
        Challenge c2 = createChallenge(currentUser, "더미: 주 3회 조깅하기",
                today.minusDays(21), today.plusDays(14), 3, 10000L,
                LocalTime.of(9, 0), LocalTime.of(18, 0),
                List.of(DayOfWeekType.MON, DayOfWeekType.WED, DayOfWeekType.FRI));
        addMembers(c2, allUsers, currentUser);
        createVerifications(c2, allUsers, today, currentUser);

        // C3: ACTIVE - 하루 한 페이지 일기 쓰기 (저녁)
        Challenge c3 = createChallenge(currentUser, "더미: 하루 한 페이지 일기 쓰기",
                today.minusDays(21), today.plusDays(14), 3, 10000L,
                LocalTime.of(18, 0), LocalTime.of(23, 59),
                List.of(DayOfWeekType.MON, DayOfWeekType.WED, DayOfWeekType.FRI));
        addMembers(c3, allUsers, currentUser);
        createVerifications(c3, allUsers, today, currentUser);

        // C4: ACTIVE - 매일 물 2L 마시기 (매일)
        Challenge c4 = createChallenge(currentUser, "더미: 매일 물 2L 마시기",
                today.minusDays(21), today.plusDays(14), 7, 5000L,
                LocalTime.of(0, 0), LocalTime.of(23, 59),
                List.of(DayOfWeekType.MON, DayOfWeekType.TUE, DayOfWeekType.WED, DayOfWeekType.THU, DayOfWeekType.FRI, DayOfWeekType.SAT, DayOfWeekType.SUN));
        addMembers(c4, allUsers, currentUser);
        createVerifications(c4, allUsers, today, currentUser);

        // C5: COMPLETE - 영어 단어 외우기
        Challenge c5 = createChallenge(currentUser, "더미: 지난달 영어 단어 외우기",
                today.minusDays(40), today.minusDays(10), 5, 20000L,
                LocalTime.of(9, 0), LocalTime.of(22, 0),
                List.of(DayOfWeekType.MON, DayOfWeekType.TUE, DayOfWeekType.WED, DayOfWeekType.THU, DayOfWeekType.FRI));
        addMembers(c5, allUsers, currentUser);
        createVerificationsForCompleted(c5, allUsers, currentUser);

        // C6: ACTIVE - 스쿼트 50개 (화목토일)
        Challenge c6 = createChallenge(currentUser, "더미: 스쿼트 50개",
                today.minusDays(21), today.plusDays(14), 4, 15000L,
                LocalTime.of(9, 0), LocalTime.of(18, 0),
                List.of(DayOfWeekType.TUE, DayOfWeekType.THU, DayOfWeekType.SAT, DayOfWeekType.SUN));
        addMembers(c6, allUsers, currentUser);
        createVerifications(c6, allUsers, today, currentUser);

        return ResponseEntity.ok("테스트 유저(" + tossId + ")를 위한 풍부한 더미 데이터(챌린지 6개 및 인증 내역) 생성이 완료되었습니다.");
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
}
