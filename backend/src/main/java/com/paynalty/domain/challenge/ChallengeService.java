package com.paynalty.domain.challenge;

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
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final UserRepository userRepository;
    private final ChallengeVerificationRepository challengeVerificationRepository;

    @Transactional
    public ChallengeResponse create(ChallengeRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));

        // frequency 자동 계산 로직
        // 1. designatedDays가 null이 아니고 비어있지 않으면 그 크기로 frequency 계산 (예: ["월", "수", "목", "토"] → frequency = 4)
        // 2. designatedDays가 null이거나 비어있으면 request의 frequency 값 사용 (요일 상관없이 주에 N회 인증)
        int calculatedFrequency;
        if (request.getDesignatedDays() != null && !request.getDesignatedDays().isEmpty()) {
            // 요일 지정 모드: 지정된 요일 수만큼 frequency 계산
            calculatedFrequency = request.getDesignatedDays().size();
        } else {
            // frequency 직접 지정 모드: designatedDays가 null이거나 빈 리스트인 경우
            if (request.getFrequency() == null || request.getFrequency() <= 0) {
                throw new IllegalArgumentException("designatedDays가 없을 때는 frequency 값(양수)이 필수입니다");
            }
            calculatedFrequency = request.getFrequency();
        }

        // 마감일 유효성 검사
        validateEndDate(request.getEndDate());

        // 인증 시간 유효성 검사
        // 프론트에서 디폴트 값(시작: 00:00, 마감: 23:59) 또는 사용자가 변경한 값을 전달
        if (request.getVerifyStartAt() != null && request.getVerifyEndAt() != null) {
            validateVerificationTime(request.getVerifyStartAt(), request.getVerifyEndAt());
        }

        // 시작일 자동 계산 로직
        // startOption에 따라 시작일 계산
        LocalDate calculatedStartDate = calculateStartDate(request.getStartOption());

        // 마감일이 시작일보다 이후인지 확인
        if (request.getEndDate().isBefore(calculatedStartDate) || request.getEndDate().isEqual(calculatedStartDate)) {
            throw new IllegalArgumentException("마감일은 시작일보다 이후여야 합니다.");
        }

        // status 자동 계산 (시작일과 종료일 기준)
        String calculatedStatus = Challenge.calculateStatus(calculatedStartDate, request.getEndDate());

        Challenge challenge = Challenge.builder()
                .title(request.getTitle())
                .startDate(calculatedStartDate)
                .endDate(request.getEndDate())
                .frequency(calculatedFrequency)
                .penaltyAmount(request.getPenaltyAmount())
                .status(calculatedStatus)
                .user(user)
                .verificationType(request.getVerificationType())
                .verifyStartAt(request.getVerifyStartAt())
                .verifyEndAt(request.getVerifyEndAt())
                .designatedDays(request.getDesignatedDays())
                .build();

        Challenge savedChallenge = challengeRepository.save(challenge);

        return ChallengeResponse.from(savedChallenge);
    }


    public List<ChallengeResponse> findByStatus(Long userId,String status){
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
        List<ChallengeResponse> progressChallenges = findByStatus(userId, "progress");

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



      // 마감일이 이미 지난 날인지 확인
      // 마감일이 오늘인지 확인
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
     * 시작 옵션에 따라 챌린지 시작일을 계산합니다.
     *
     * @param startOption "tomorrow" (내일부터 시작하기) 또는 "nextWeek" (다음주부터 시작하기)
     * @return 계산된 시작일
     * @throws IllegalArgumentException 잘못된 startOption 값인 경우
     */
    private LocalDate calculateStartDate(String startOption) {
        if (startOption == null || startOption.trim().isEmpty()) {
            throw new IllegalArgumentException("시작 옵션은 필수입니다. 'tomorrow' 또는 'nextWeek'를 입력해주세요.");
        }
        
        LocalDate today = LocalDate.now();
        
        if ("tomorrow".equalsIgnoreCase(startOption.trim())) {
            // 내일부터 시작하기
            return today.plusDays(1);
        } else if ("nextWeek".equalsIgnoreCase(startOption.trim())) {
            // 다음주 월요일부터 시작하기
            // TemporalAdjusters.next()는 오늘이 해당 요일이면 다음주 해당 요일을 반환
            return today.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        } else {
            throw new IllegalArgumentException("잘못된 시작 옵션입니다. 'tomorrow' 또는 'nextWeek'만 사용 가능합니다. 입력된 값: " + startOption);
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

