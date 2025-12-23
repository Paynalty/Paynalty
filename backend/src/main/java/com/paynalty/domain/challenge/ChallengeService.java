package com.paynalty.domain.challenge;

import com.paynalty.domain.challengemember.ChallengeMember;
import com.paynalty.domain.challengemember.ChallengeMemberRepository;
import com.paynalty.domain.challengemember.ChallengeMemberService;
import com.paynalty.domain.challengeverification.ChallengeVerificationRepository;
import com.paynalty.domain.user.User;
import com.paynalty.domain.user.UserRepository;
import com.paynalty.global.error.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final UserRepository userRepository;
    private final ChallengeVerificationRepository challengeVerificationRepository;
    private final ChallengeMemberService challengeMemberService;
    private final ChallengeMemberRepository challengeMemberRepository;


    @Transactional
    public ChallengeResponse create(ChallengeRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));

        // frequency 자동 계산 로직
        int calculatedFrequency;
        if (request.getDayOfWeeks() != null && !request.getDayOfWeeks().isEmpty()) {
            // 요일 지정 모드: 지정된 요일 수만큼 frequency 계산
            calculatedFrequency = request.getDayOfWeeks().size();
        } else {
            // frequency 직접 지정 모드: dayOfweeks가 null이거나 빈 리스트인 경우
            if (request.getFrequency() == null || request.getFrequency() <= 0) {
                throw new IllegalArgumentException("dayOfWeeks가 없을 때는 frequency 값(양수)이 필수입니다");
            }
            calculatedFrequency = request.getFrequency();
        }

        // 시작일 유효성 검사
        validateStartDate(request.getStartDate());

        // 마감일 유효성 검사
        validateEndDate(request.getEndDate());

        // 시작일과 마감일 관계 유효성 검사
        validateDateRange(request.getStartDate(), request.getEndDate());

        // 인증 시간 유효성 검사
        // 프론트에서 디폴트 값(시작: 00:00, 마감: 23:59) 또는 사용자가 변경한 값을 전달
        if (request.getVerifyStartAt() != null && request.getVerifyEndAt() != null) {
            validateVerificationTime(request.getVerifyStartAt(), request.getVerifyEndAt());
        }

        // status 자동 계산 (시작일과 종료일 기준)
        ChallengeStatus status = Challenge.calculateStatus(request.getStartDate(),request.getEndDate());

        Challenge challenge = Challenge.builder()
                .title(request.getTitle())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .frequency(calculatedFrequency)
                .penaltyAmount(request.getPenaltyAmount())
                .status(status)
                .user(user)
                .verificationType(request.getVerificationType())
                .verifyStartAt(request.getVerifyStartAt())
                .verifyEndAt(request.getVerifyEndAt())
                .daysOfWeeks(request.getDayOfWeeks())
                .build();
        
        Challenge savedChallenge = challengeRepository.save(challenge);

        //challengeMember 생성 부분
        // 1. 생성자 본인 추가
        createChallengeMemberIfNotExists(user, savedChallenge);

        // 2. 초대된 친구들 추가
        if (request.getInviteFriends() != null) {
            for (ChallengeRequest.InviteFriend inviteFriend : request.getInviteFriends()) {
                // 이름과 전화번호로 사용자 찾기
                Optional<User> friendOptional = userRepository.findByNameAndPhoneNum(inviteFriend.getName(), inviteFriend.getPhoneNumber());
                
                if (friendOptional.isPresent()) {
                    User friend = friendOptional.get();
                    createChallengeMemberIfNotExists(friend, savedChallenge);
                }
                // 친구가 가입되어 있지 않은 경우에 대한 처리는 현재 요구사항에 없으므로 건너뜀 (추후 필요시 추가 가능)
            }
        }

        return ChallengeResponse.from(savedChallenge);
    }

    private void createChallengeMemberIfNotExists(User user, Challenge challenge) {
        // 이미 챌린지 멤버인지 확인
        boolean isAlreadyMember = challengeMemberRepository.findByUserIdAndChallengeId(user.getId(), challenge.getId()).isPresent();
        
        if (!isAlreadyMember) {
            ChallengeMember challengeMember = ChallengeMember.builder()
                    .user(user)
                    .challenge(challenge)
                    .isSuccess("PENDING") // 초기 상태 설정 (필요에 따라 변경)
                    .endAt(challenge.getEndDate())
                    .build();
            challengeMemberRepository.save(challengeMember);
        }
    }


    public List<ChallengeResponse> findByStatus(Long userId,ChallengeStatus status){
        // status 상태,사용자가 참여 중인 : 조건에 맞는 challenge 불러오기
        List<Challenge> challenges = challengeRepository.findByEmailAndStatus(userId,status);

        return challenges.stream().map(ChallengeResponse::from).collect(Collectors.toList());

    }

    /**
     * 사용자가 진행중인 챌린지 목록의 상세 정보를 조회합니다.
     * 진행중인 챌린지 목록을 가져온 후, 각 챌린지에 대해 상세 정보를 생성합니다.
     *
     * @param userId 사용자 ID
     * @return 진행중인 챌린지 상세 정보 목록
     */
    public List<ChallengeDetailResponse> getMyProgressChallengesDetail(Long userId) {
        // 1단계: 진행중인 챌린지 목록 조회
        List<ChallengeResponse> progressChallenges = findByStatus(userId, ChallengeStatus.ACTIVE);

        // 2단계: 각 챌린지에 대해 상세 정보 생성
        return progressChallenges.stream()
                .map(challengeResponse -> getMyChallengeDetail(challengeResponse.getId(), userId))
                .collect(Collectors.toList());
    }

    /**
     * 챌린지 상세 정보를 조회합니다.
     * 반환 정보:
     * 1. 현재 주간 인증 횟수 / 주간 총 인증 횟수
     * 2. 인증 실패 시 벌금
     * 3. 당일 인증 마감까지 남은 시간
     *
     * @param challengeId 챌린지 ID
     * @param userId 사용자 ID
     * @return ChallengeDetailResponse
     */
    public ChallengeDetailResponse getMyChallengeDetail(Long challengeId, Long userId) {
        // 1단계: Challenge 조회
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new IllegalArgumentException("챌린지를 찾을 수 없습니다: " + challengeId));

        // 2단계: 이번주 인증 횟수 조회
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(DayOfWeek.MONDAY); // 이번주 월요일
        LocalDate weekEnd = weekStart.plusDays(6); // 이번주 일요일
        
        Long currentWeeklyCount = challengeVerificationRepository.countWeeklyVerifications(
                challengeId,
                userId,
                weekStart,
                weekEnd
        );

        // 3단계: 당일 인증 마감까지 남은 시간 계산
        LocalTime verifyEndAt = challenge.getVerifyEndAt();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime deadline = LocalDateTime.of(today, verifyEndAt != null ? verifyEndAt : LocalTime.of(23, 59, 59));
        
        Duration remainingDuration;
        if (now.isAfter(deadline)) {
            // 이미 마감 시간이 지났다면 0초 반환
            remainingDuration = Duration.ZERO;
        } else {
            remainingDuration = Duration.between(now, deadline);
        }

        String remainingTimeFormatted = formatDuration(remainingDuration);

        // 4단계: ChallengeDetailResponse 생성 및 반환
        return ChallengeDetailResponse.builder()
                .challengeId(challenge.getId())
                .challengeTitle(challenge.getTitle())
                .currentWeeklyVerificationCount(currentWeeklyCount.intValue())
                .weeklyRequiredVerificationCount(challenge.getFrequency())
                .penaltyAmount(challenge.getPenaltyAmount())
                .remainingTimeFormatted(remainingTimeFormatted)
                .build();
    }


    // ---------------------------------------- 유효성 검사 -----------------------------------------------------


     /**
      * 시작일이 유효한지 확인합니다.
      * - 시작일이 오늘보다 이후여야 함 (오늘 포함 불가)
      *
      * @param startDate 시작일
      * @throws IllegalArgumentException 시작일이 유효하지 않은 경우
      */
     private void validateStartDate(LocalDate startDate) {
         LocalDate today = LocalDate.now();

         if (startDate.isBefore(today)) {
             throw new IllegalArgumentException("시작일은 이미 지난 날짜일 수 없습니다.");
         }

         if (startDate.isEqual(today)) {
             throw new IllegalArgumentException("시작일은 오늘 날짜일 수 없습니다. 최소 내일 이후로 설정해주세요.");
         }
     }

     /**
      * 마감일이 유효한지 확인합니다.
      * - 마감일이 오늘보다 이후여야 함 (오늘 포함 불가)
      *
      * @param endDate 마감일
      * @throws IllegalArgumentException 마감일이 유효하지 않은 경우
      */
     private void validateEndDate(LocalDate endDate) {
         LocalDate today = LocalDate.now();

         if (endDate.isBefore(today)) {
             throw new IllegalArgumentException("마감일은 이미 지난 날짜일 수 없습니다.");
         }

         if (endDate.isEqual(today)) {
             throw new IllegalArgumentException("마감일은 오늘 날짜일 수 없습니다. 최소 내일 이후로 설정해주세요.");
         }
     }

     /**
      * 시작일과 마감일의 관계가 유효한지 확인합니다.
      * - 마감일이 시작일보다 이후여야 함
      * 챌린지 시작일,마감일 유효성
      * @param startDate 시작일
      * @param endDate 마감일
      * @throws IllegalArgumentException 날짜 범위가 유효하지 않은 경우
      */
     private void validateDateRange(LocalDate startDate, LocalDate endDate) {
         if (endDate.isBefore(startDate) || endDate.isEqual(startDate)) {
             throw new IllegalArgumentException("마감일은 시작일보다 이후여야 합니다.");
         }
     }

     /**
      * 인증 시작 시간과 마감 시간에 대한 유효성 검사를 수행합니다.
      * 사용자가 디폴트 값이 아닌 다른 값을 입력한 경우에만 호출됩니다.
      * - 시작 시간이 마감 시간보다 이전인지 확인
      * - 시작 시간과 마감 시간이 같으면 안됨
      *
      * @param verifyStartAt 인증 시작 시간
      * @param verifyEndAt 인증 마감 시간
      * @throws IllegalArgumentException 인증 시간이 유효하지 않은 경우
      */
     private void validateVerificationTime(LocalTime verifyStartAt, LocalTime verifyEndAt) {
         // 시작 시간이 마감 시간보다 이후이거나 같으면 오류
         if (verifyStartAt.isAfter(verifyEndAt) || verifyStartAt.equals(verifyEndAt)) {
             throw new IllegalArgumentException("인증 시작 시간은 마감 시간보다 이전이어야 합니다.");
         }
     }


    /**
     * Duration을 "시:분:초" 형식의 문자열로 변환합니다.
     *
     * @param duration Duration 객체
     * @return "HH:mm:ss" 형식의 문자열
     */
    private String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}

