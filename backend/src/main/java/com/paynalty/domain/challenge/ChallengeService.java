package com.paynalty.domain.challenge;

import com.paynalty.domain.challengemember.ChallengeMember;
import com.paynalty.domain.challengemember.ChallengeMemberRepository;
import com.paynalty.domain.challengemember.ChallengeMemberService;
import com.paynalty.domain.challengeverification.ChallengeVerificationRepository;
import com.paynalty.domain.challengeverification.ChallengeVerificationService;
import com.paynalty.domain.user.User;
import com.paynalty.domain.user.UserRepository;
import com.paynalty.global.error.ChallengeErrorCode;
import com.paynalty.global.error.CustomException;
import com.paynalty.global.error.UserErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
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
    private final ChallengeVerificationService challengeVerificationService;


    @Transactional
    public Long create(ChallengeRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        // frequency 자동 계산 로직
        int calculatedFrequency;
        if (request.getDaysOfWeek() != null && !request.getDaysOfWeek().isEmpty()) {
            // 요일 지정 모드: 지정된 요일 수만큼 frequency 계산
            calculatedFrequency = request.getDaysOfWeek().size();
        } else {
            // frequency 직접 지정 모드: daysOfWeek가 null이거나 빈 리스트인 경우
            if (request.getFrequency() == null || request.getFrequency() <= 0) {
                throw new CustomException(ChallengeErrorCode.INVALID_FREQUENCY);
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
                .daysOfWeek(request.getDaysOfWeek())
                .build();
        
        Challenge savedChallenge = challengeRepository.save(challenge);

        // ChallengeMember 생성 (생성자 + 초대된 친구들)
        List<ChallengeMemberService.InviteFriendInfo> inviteFriendInfos = null;
        if (request.getInviteFriends() != null) {
            inviteFriendInfos = request.getInviteFriends().stream()
                    .map(friend -> new ChallengeMemberService.InviteFriendInfo(
                            friend.getName(), 
                            friend.getPhoneNumber()))
                    .toList();
        }
        challengeMemberService.addMembersToNewChallenge(user, savedChallenge, inviteFriendInfos);

        // 생성된 챌린지 ID 반환
        return savedChallenge.getId();
    }


    public List<ChallengeResponse> findByStatus(Long userId, ChallengeStatus status){
        // 사용자가 참여 중인 챌린지 중 특정 상태의 챌린지 불러오기
        List<Challenge> challenges = challengeRepository.findByUserIdAndStatus(userId, status);

        return challenges.stream()
                .map(challenge -> {
                    // 각 챌린지에 대해 추가 정보 계산
                    Integer totalParticipants = challengeMemberRepository.findByChallengeId(challenge.getId()).size();
                    
                    // 챌린지 상태에 따라 다른 처리
                    ChallengeStatus challengeStatus = challenge.calculateStatus();
                    
                    if (challengeStatus == ChallengeStatus.PENDING) {
                        // 시작 전 챌린지: 기본값 설정
                        return ChallengeResponse.from(
                            challenge,
                            VerificationStatus.NOT_VERIFIED,
                            totalParticipants,
                            0
                        );
                    } else if (challengeStatus == ChallengeStatus.COMPLETE) {
                        // 완료된 챌린지 - 전체 인증 횟수 표시
                        Integer totalVerificationCount = calculateTotalVerificationCount(challenge.getId(), userId);
                        return ChallengeResponse.from(
                            challenge,
                            VerificationStatus.NOT_VERIFIED,  // 완료되어 더 이상 인증 안함
                            totalParticipants,
                            totalVerificationCount  // 전체 인증 횟수
                        );
                    } else {
                        // 진행 중(ACTIVE) 챌린지
                        VerificationStatus verificationStatus = determineVerificationStatus(challenge.getId(), userId);
                        Integer weeklyProgressCount = calculateWeeklyProgressCount(challenge.getId(), userId);
                        
                        return ChallengeResponse.from(challenge, verificationStatus, totalParticipants, weeklyProgressCount);
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * 사용자가 참여 중인 챌린지 중 특정 상태의 챌린지 목록을 상세 정보로 조회합니다.
     * 
     * @param userId 사용자 ID
     * @param status 챌린지 상태 (PENDING, ACTIVE, COMPLETE)
     * @return 챌린지 상세 정보 목록
     */
    public List<ChallengeDetailResponse> findDetailByStatus(Long userId, ChallengeStatus status) {
        // 사용자가 참여 중인 챌린지 중 특정 상태의 챌린지 불러오기
        List<Challenge> challenges = challengeRepository.findByUserIdAndStatus(userId, status);

        return challenges.stream()
                .map(challenge -> {
                    // 챌린지 상태에 따라 다른 처리
                    ChallengeStatus challengeStatus = challenge.calculateStatus();
                    
                    if (challengeStatus == ChallengeStatus.PENDING) {
                        // 시작 전 챌린지: 기본값 설정
                        return ChallengeDetailResponse.builder()
                                .id(challenge.getId())
                                .title(challenge.getTitle())
                                .weeklyProgressCount(0)
                                .weeklyRequiredCount(challenge.getFrequency())
                                .penaltyAmount(challenge.getPenaltyAmount())
                                .verifyStart(challenge.getVerifyStartAt())
                                .verifyEnd(challenge.getVerifyEndAt())
                                .endAt(challenge.getEndDate())
                                .daysOfWeek(challenge.getDaysOfWeek())
                                .verificationStatus(VerificationStatus.NOT_VERIFIED)
                                .build();
                    } else if (challengeStatus == ChallengeStatus.COMPLETE) {
                        // 완료된 챌린지 - 마지막 주의 주간 인증 횟수 표시
                        Integer lastWeekProgressCount = calculateWeeklyProgressCountForDate(challenge.getId(), userId, challenge.getEndDate());
                        
                        return ChallengeDetailResponse.builder()
                                .id(challenge.getId())
                                .title(challenge.getTitle())
                                .weeklyProgressCount(lastWeekProgressCount)  // 마지막 주의 주간 인증 횟수
                                .weeklyRequiredCount(challenge.getFrequency())
                                .penaltyAmount(challenge.getPenaltyAmount())
                                .verifyStart(challenge.getVerifyStartAt())
                                .verifyEnd(challenge.getVerifyEndAt())
                                .endAt(challenge.getEndDate())
                                .daysOfWeek(challenge.getDaysOfWeek())
                                .verificationStatus(VerificationStatus.NOT_VERIFIED)
                                .build();
                    } else {
                        // 진행 중(ACTIVE) 챌린지
                        VerificationStatus verificationStatus = determineVerificationStatus(challenge.getId(), userId);
                        Integer weeklyProgressCount = calculateWeeklyProgressCount(challenge.getId(), userId);
                        
                        return ChallengeDetailResponse.builder()
                                .id(challenge.getId())
                                .title(challenge.getTitle())
                                .weeklyProgressCount(weeklyProgressCount)
                                .weeklyRequiredCount(challenge.getFrequency())
                                .penaltyAmount(challenge.getPenaltyAmount())
                                .verifyStart(challenge.getVerifyStartAt())
                                .verifyEnd(challenge.getVerifyEndAt())
                                .endAt(challenge.getEndDate())
                                .daysOfWeek(challenge.getDaysOfWeek())
                                .verificationStatus(verificationStatus)
                                .build();
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * 특정 챌린지의 상세 정보를 조회합니다.
     * 챌린지 ID를 받아 해당 챌린지의 모든 상세 정보를 ChallengeResponse로 반환합니다.
     *
     * @param challengeId 챌린지 ID
     * @param userId 사용자 ID
     * @return ChallengeResponse (챌린지 상세 정보)
     */
    public ChallengeResponse getChallengeDetail(Long challengeId, Long userId) {
        // 1단계: challengeId로 챌린지 조회
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new CustomException(ChallengeErrorCode.CHALLENGE_NOT_FOUND));

        // 2단계: 기본 정보
        Integer totalParticipants = challengeMemberRepository.findByChallengeId(challengeId).size();
        ChallengeStatus challengeStatus = challenge.calculateStatus();

        // 3단계: 챌린지 상태에 따라 다른 처리
        if (challengeStatus == ChallengeStatus.PENDING) {
            // 시작 전 챌린지
            return ChallengeResponse.from(
                challenge,
                VerificationStatus.NOT_VERIFIED,
                totalParticipants,
                0
            );
        } else if (challengeStatus == ChallengeStatus.COMPLETE) {
            // 완료된 챌린지 - 전체 인증 횟수 표시
            Integer totalVerificationCount = calculateTotalVerificationCount(challengeId, userId);
            return ChallengeResponse.from(
                challenge,
                VerificationStatus.NOT_VERIFIED,
                totalParticipants,
                totalVerificationCount  // 전체 인증 횟수
            );
        } else {
            // 진행 중(ACTIVE) 챌린지
            VerificationStatus verificationStatus = determineVerificationStatus(challengeId, userId);
            Integer weeklyProgressCount = calculateWeeklyProgressCount(challengeId, userId);

            return ChallengeResponse.from(challenge, verificationStatus, totalParticipants, weeklyProgressCount);
        }
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
                .orElseThrow(() -> new CustomException(ChallengeErrorCode.CHALLENGE_NOT_FOUND));

        // 2단계: 챌린지 상태 확인
        ChallengeStatus challengeStatus = challenge.calculateStatus();
        
        // 3단계: 상태에 따라 다른 처리
        if (challengeStatus == ChallengeStatus.PENDING) {
            // 시작 전 챌린지
            return ChallengeDetailResponse.builder()
                    .id(challenge.getId())
                    .title(challenge.getTitle())
                    .weeklyProgressCount(0)
                    .weeklyRequiredCount(challenge.getFrequency())
                    .penaltyAmount(challenge.getPenaltyAmount())
                    .verifyStart(challenge.getVerifyStartAt())
                    .verifyEnd(challenge.getVerifyEndAt())
                    .endAt(challenge.getEndDate())
                    .verificationStatus(VerificationStatus.NOT_VERIFIED)
                    .build();
        } else if (challengeStatus == ChallengeStatus.COMPLETE) {
            // 완료된 챌린지 - 마지막 주의 주간 인증 횟수 표시
            Integer lastWeekProgressCount = calculateWeeklyProgressCountForDate(challengeId, userId, challenge.getEndDate());
            
            return ChallengeDetailResponse.builder()
                    .id(challenge.getId())
                    .title(challenge.getTitle())
                    .weeklyProgressCount(lastWeekProgressCount)  // 마지막 주의 주간 인증 횟수
                    .weeklyRequiredCount(challenge.getFrequency())
                    .penaltyAmount(challenge.getPenaltyAmount())
                    .verifyStart(challenge.getVerifyStartAt())
                    .verifyEnd(challenge.getVerifyEndAt())
                    .endAt(challenge.getEndDate())
                    .verificationStatus(VerificationStatus.NOT_VERIFIED)
                    .build();
        }
        
        // 4단계: 진행 중(ACTIVE) 챌린지 - 이번주 인증 횟수 조회
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(DayOfWeek.MONDAY); // 이번주 월요일
        LocalDate weekEnd = weekStart.plusDays(6); // 이번주 일요일
        
        Long currentWeeklyCount = challengeVerificationRepository.countWeeklyVerifications(
                challengeId,
                userId,
                weekStart,
                weekEnd
        );


        // 6단계: 오늘의 인증 상태 확인
        VerificationStatus verificationStatus = determineVerificationStatus(challenge, userId);

        // 7단계: ChallengeDetailResponse 생성 및 반환
        return ChallengeDetailResponse.builder()
                .id(challenge.getId())
                .title(challenge.getTitle())
                .weeklyProgressCount(currentWeeklyCount.intValue())
                .weeklyRequiredCount(challenge.getFrequency())
                .penaltyAmount(challenge.getPenaltyAmount())
                .verifyStart(challenge.getVerifyStartAt())
                .verifyEnd(challenge.getVerifyEndAt())
                .endAt(challenge.getEndDate())
                .verificationStatus(verificationStatus)
                .build();
    }

    // update
//    public ChallengeResponse edit(){
//        // 수정할 챌린지 entity 찾기
//        // updateRequest 의 필드에 entity 값 설정
//        // 필드값이 설정된 update를 사용자에게 보여줌
//        // 사용자가 보고 수정할 내용 변경
//        // 변경 된 내용 entity의 update 매서드 활요하여 적용 . (저장소에 save안해도 자동 저장?)
//        // 변경된 챌린지entity 내용을 response객체로 반환하여 보여줌
//
//    }


    // ---------------------------------------------------------------------------------------------



     /**
      * 시작일이 유효한지 확인합니다.
      * - 시작일이 오늘보다 이후여야 함 (오늘 포함 불가)
      *
      * @param startDate 시작일
      * @throws CustomException 시작일이 유효하지 않은 경우
      */
     private void validateStartDate(LocalDate startDate) {
         LocalDate today = LocalDate.now();

         if (startDate.isBefore(today)) {
             throw new CustomException(ChallengeErrorCode.INVALID_START_DATE_PAST);
         }

         if (startDate.isEqual(today)) {
             throw new CustomException(ChallengeErrorCode.INVALID_START_DATE_TODAY);
         }
     }

     /**
      * 마감일이 유효한지 확인합니다.
      * - 마감일이 오늘보다 이후여야 함 (오늘 포함 불가)
      *
      * @param endDate 마감일
      * @throws CustomException 마감일이 유효하지 않은 경우
      */
     private void validateEndDate(LocalDate endDate) {
         LocalDate today = LocalDate.now();

         if (endDate.isBefore(today)) {
             throw new CustomException(ChallengeErrorCode.INVALID_END_DATE_PAST);
         }

         if (endDate.isEqual(today)) {
             throw new CustomException(ChallengeErrorCode.INVALID_END_DATE_TODAY);
         }
     }

     /**
      * 시작일과 마감일의 관계가 유효한지 확인합니다.
      * - 마감일이 시작일보다 이후여야 함
      * 챌린지 시작일,마감일 유효성
      * @param startDate 시작일
      * @param endDate 마감일
      * @throws CustomException 날짜 범위가 유효하지 않은 경우
      */
     private void validateDateRange(LocalDate startDate, LocalDate endDate) {
         if (endDate.isBefore(startDate) || endDate.isEqual(startDate)) {
             throw new CustomException(ChallengeErrorCode.INVALID_DATE_RANGE);
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
      * @throws CustomException 인증 시간이 유효하지 않은 경우
      */
     private void validateVerificationTime(LocalTime verifyStartAt, LocalTime verifyEndAt) {
         // 시작 시간이 마감 시간보다 이후이거나 같으면 오류
         if (verifyStartAt.isAfter(verifyEndAt) || verifyStartAt.equals(verifyEndAt)) {
             throw new CustomException(ChallengeErrorCode.INVALID_VERIFICATION_TIME);
         }
     }


    /**
     * 이번 주 사용자의 인증 횟수를 계산합니다.
     * 
     * @param challengeId 챌린지 ID
     * @param userId 사용자 ID
     * @return 이번 주 인증 횟수
     */
    private Integer calculateWeeklyProgressCount(Long challengeId, Long userId) {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(DayOfWeek.MONDAY); // 이번주 월요일
        LocalDate weekEnd = weekStart.plusDays(6); // 이번주 일요일
        
        Long count = challengeVerificationRepository.countWeeklyVerifications(
                challengeId,
                userId,
                weekStart,
                weekEnd
        );
        
        return count.intValue();
    }

    /**
     * 특정 날짜를 기준으로 해당 주의 인증 횟수를 계산합니다.
     * 완료된 챌린지의 마지막 주 인증 횟수를 계산할 때 사용됩니다.
     * 
     * @param challengeId 챌린지 ID
     * @param userId 사용자 ID
     * @param referenceDate 기준 날짜 (이 날짜가 포함된 주의 인증 횟수 계산)
     * @return 해당 주의 인증 횟수
     */
    private Integer calculateWeeklyProgressCountForDate(Long challengeId, Long userId, LocalDate referenceDate) {
        LocalDate weekStart = referenceDate.with(DayOfWeek.MONDAY); // 기준 날짜가 포함된 주의 월요일
        LocalDate weekEnd = weekStart.plusDays(6); // 해당 주의 일요일
        
        Long count = challengeVerificationRepository.countWeeklyVerifications(
                challengeId,
                userId,
                weekStart,
                weekEnd
        );
        
        return count.intValue();
    }

    /**
     * 사용자의 전체 인증 횟수를 계산합니다.
     * 완료된 챌린지에서 사용됩니다.
     * 
     * @param challengeId 챌린지 ID
     * @param userId 사용자 ID
     * @return 전체 인증 횟수
     */
    private Integer calculateTotalVerificationCount(Long challengeId, Long userId) {
        Long count = challengeVerificationRepository.countTotalVerifications(challengeId, userId);
        return count.intValue();
    }

    /**
     * 오늘의 인증 상태를 판단합니다.
     * 
     * 로직:
     * 1. 챌린지의 daysOfWeek에 오늘 요일이 포함되어 있는지 확인
     * 2. 포함되어 있다면:
     *    - 해당 사용자의 오늘 인증 내역이 있는지 확인
     *    - 있으면 VERIFIED (인증함), 없으면 NOT_VERIFIED (인증안함)
     * 3. 포함되어 있지 않다면: NOT_VERIFIED (인증안함)
     *
     * @param challenge 챌린지 객체
     * @param userId 사용자 ID
     * @return VerificationStatus (VERIFIED 또는 NOT_VERIFIED)
     */
    private VerificationStatus determineVerificationStatus(Challenge challenge, Long userId) {
        // 1단계: daysOfWeek가 null이거나 비어있으면 NOT_VERIFIED 반환
        if (challenge.getDaysOfWeek() == null || challenge.getDaysOfWeek().isEmpty()) {
            return VerificationStatus.NOT_VERIFIED;
        }

        // 2단계: 오늘의 요일을 DayOfWeekType으로 변환
        DayOfWeek todayDayOfWeek = LocalDate.now().getDayOfWeek();
        DayOfWeekType todayDayOfWeekType = DayOfWeekType.from(todayDayOfWeek);

        // 3단계: 오늘이 챌린지 인증 요일에 포함되어 있는지 확인
        if (challenge.getDaysOfWeek().contains(todayDayOfWeekType)) {
            // 오늘이 인증 요일이면 해당 사용자의 오늘 인증 내역 확인
            boolean hasVerifiedToday = challengeVerificationService.checkVerification(challenge.getId(), userId);
            return VerificationStatus.from(hasVerifiedToday);
        }

        // 4단계: 오늘이 인증 요일이 아니면 NOT_VERIFIED
        return VerificationStatus.NOT_VERIFIED;
    }

    /**
     * 오늘의 인증 상태를 판단합니다. (challengeId로 조회)
     * 
     * @param challengeId 챌린지 ID
     * @param userId 사용자 ID
     * @return VerificationStatus (VERIFIED 또는 NOT_VERIFIED)
     */
    private VerificationStatus determineVerificationStatus(Long challengeId, Long userId) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new CustomException(ChallengeErrorCode.CHALLENGE_NOT_FOUND));
        return determineVerificationStatus(challenge, userId);
    }

}

