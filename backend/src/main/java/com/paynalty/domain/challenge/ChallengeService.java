package com.paynalty.domain.challenge;

import com.paynalty.domain.challengemember.ChallengeMember;
import com.paynalty.domain.challengemember.ChallengeMemberRepository;
import com.paynalty.domain.challengemember.ChallengeMemberResponse;
import com.paynalty.domain.challengemember.ChallengeMemberService;
import com.paynalty.domain.challengeverification.ChallengeVerificationRepository;
import com.paynalty.domain.challengeverification.ChallengeVerificationService;
import com.paynalty.domain.user.User;
import com.paynalty.domain.user.UserRepository;
import com.paynalty.domain.user.UserService;
import com.paynalty.global.error.ChallengeErrorCode;
import com.paynalty.global.error.ChallengeMemberErrorCode;
import com.paynalty.global.error.CustomException;
import com.paynalty.global.error.UserErrorCode;
import lombok.RequiredArgsConstructor;
import com.paynalty.domain.challengemember.MemberRole;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
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
    private final UserService userService;


    @Transactional
    public Long create(ChallengeRequest request, Long tossId) {
        User user = userRepository.findByTossId(tossId)
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
        // 변경 기존 : List<InvitionFriends> -> List<Long>

        challengeMemberService.addMembersToNewChallenge(user,savedChallenge,request.getTossIds());

        // 생성된 챌린지 ID 반환
        return savedChallenge.getId();
    }


    public List<ChallengeDetailResponse> findDetailByStatus(Long tossId, ChallengeStatus status) {
        // 사용자가 참여 중인 챌린지 중 특정 상태의 챌린지 불러오기
        List<Challenge> challenges = challengeRepository.findByUserTossIdAndStatus(tossId, status);

        return challenges.stream()
                .map(challenge -> {
                    // 챌린지 상태에 따라 다른 처리
                    ChallengeStatus challengeStatus = challenge.calculateStatus();
                    List<ChallengeMemberResponse> memberList = challengeMemberService.getMembersByChallengeId(challenge.getId());

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
                                .startAt(challenge.getStartDate())
                                .endAt(challenge.getEndDate())
                                .daysOfWeek(challenge.getDaysOfWeek())
                                .verificationType(challenge.getVerificationType())
                                .verificationStatus(VerificationStatus.NOT_VERIFIED)
                                .members(memberList)
                                .build();
                    } else if (challengeStatus == ChallengeStatus.COMPLETE) {
                        // 완료된 챌린지 - 마지막 주의 주간 인증 횟수 표시
                        Integer lastWeekProgressCount = calculateWeeklyProgressCountForDate(challenge.getId(), tossId, challenge.getEndDate());

                        return ChallengeDetailResponse.builder()
                                .id(challenge.getId())
                                .title(challenge.getTitle())
                                .weeklyProgressCount(lastWeekProgressCount)  // 마지막 주의 주간 인증 횟수
                                .weeklyRequiredCount(challenge.getFrequency())
                                .penaltyAmount(challenge.getPenaltyAmount())
                                .verifyStart(challenge.getVerifyStartAt())
                                .verifyEnd(challenge.getVerifyEndAt())
                                .startAt(challenge.getStartDate())
                                .endAt(challenge.getEndDate())
                                .daysOfWeek(challenge.getDaysOfWeek())
                                .verificationType(challenge.getVerificationType())
                                .verificationStatus(VerificationStatus.NOT_VERIFIED)
                                .members(memberList)
                                .build();
                    } else {
                        // 진행 중(ACTIVE) 챌린지
                        VerificationStatus verificationStatus = determineVerificationStatus(challenge.getId(), tossId);
                        Integer weeklyProgressCount = calculateWeeklyProgressCount(challenge.getId(), tossId);

                        return ChallengeDetailResponse.builder()
                                .id(challenge.getId())
                                .title(challenge.getTitle())
                                .weeklyProgressCount(weeklyProgressCount)
                                .weeklyRequiredCount(challenge.getFrequency())
                                .penaltyAmount(challenge.getPenaltyAmount())
                                .verifyStart(challenge.getVerifyStartAt())
                                .verifyEnd(challenge.getVerifyEndAt())
                                .startAt(challenge.getStartDate())
                                .endAt(challenge.getEndDate())
                                .daysOfWeek(challenge.getDaysOfWeek())
                                .verificationType(challenge.getVerificationType())
                                .verificationStatus(verificationStatus)
                                .members(memberList)
                                .build();
                    }
                })
                .collect(Collectors.toList());
    }



            // updateRequest 데이터 설정 후 사용자 한테 전달
    public ChallengeUpdateRequest getUpdateForm(Long challengeId, Long tossId){
        // 1. 챌린지 존재 확인
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new CustomException(ChallengeErrorCode.CHALLENGE_NOT_FOUND));

        // 2. 권한 확인 (생성자인지 확인)
        ChallengeMember member = challengeMemberRepository
                .findByChallengeIdAndUserTossIdWithFetch(challengeId, tossId)
                .orElseThrow(() -> new CustomException(ChallengeMemberErrorCode.NOT_CHALLENGE_MEMBER));

        if (member.getRole() != MemberRole.CREATOR) {
            throw new CustomException(ChallengeErrorCode.NOT_CHALLENGE_CREATOR_FOR_UPDATE);
        }

        List<ChallengeMember> challengeMembers = challengeMemberRepository.findByChallengeId(challengeId);
        List<Long> userIds = challengeMembers.stream()
                .map(ChallengeMember::getUser)
                .map(User::getId)
                .collect(Collectors.toList());

        // updateRequest 필드 값 설정 후 반환
        return new ChallengeUpdateRequest(challenge, userIds);
    }

        //update
        @Transactional
        public ChallengeDetailResponse update(Long challengeId ,ChallengeUpdateRequest request,Long tossId){
            // 1단계: 챌린지 존재 여부 확인
            Challenge challenge = challengeRepository.findById(challengeId)
                    .orElseThrow(()-> new CustomException(ChallengeErrorCode.CHALLENGE_NOT_FOUND));

            // 2단계: 사용자가 해당 챌린지의 멤버인지 확인 및 권한 확인
            ChallengeMember member = challengeMemberRepository
                    .findByChallengeIdAndUserTossIdWithFetch(challengeId, tossId)
                    .orElseThrow(() -> new CustomException(ChallengeMemberErrorCode.NOT_CHALLENGE_MEMBER));

            // 3단계: 생성자(CREATOR) 권한 확인 (수정 권한)
            if (member.getRole() != com.paynalty.domain.challengemember.MemberRole.CREATOR) {
                throw new CustomException(ChallengeErrorCode.NOT_CHALLENGE_CREATOR_FOR_UPDATE);
            }

            User user = member.getUser();  // ChallengeMember에서 User 가져오기

            // frequency 자동 계산 로직 (생성과 동일)
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

            // 이번주 인증 횟수 조회
            LocalDate today = LocalDate.now();
            LocalDate weekStart = today.with(DayOfWeek.MONDAY); // 이번주 월요일
            LocalDate weekEnd = weekStart.plusDays(6); // 이번주 일요일

            Long currentWeeklyCount = challengeVerificationRepository.countWeeklyVerifications(
                    challengeId,
                    tossId,
                    weekStart,
                    weekEnd
            );
            // 챌린지 시작일, 마감일 , 인증시간 에대 한 검증
            // 마감일
            validateEndDate(request.getEndDate());
            // 인증시간
            validateVerificationTime(request.getVerifyStartAt(),request.getVerifyEndAt());

            challenge.update(request);
            // 계산된 frequency 반영 (요일 변경 시 자동으로 frequency도 업데이트)
            challenge.updateFrequency(calculatedFrequency);

            VerificationStatus verificationStatus = determineVerificationStatus(challenge, tossId);

            // request 의 userIds 데이터 토대로 다시 챌린지 맴버 전환
            List<Long> userIds = request.getUserIds();
            challengeMemberService.addMembersToNewChallenge(user,challenge,userIds);

            // hallengeDetailResponse 생성 및 반환
            return ChallengeDetailResponse.builder()
                    .id(challenge.getId())
                    .title(challenge.getTitle())
                    .weeklyProgressCount(currentWeeklyCount.intValue())
                    .weeklyRequiredCount(challenge.getFrequency())
                    .penaltyAmount(challenge.getPenaltyAmount())
                    .verifyStart(challenge.getVerifyStartAt())
                    .verifyEnd(challenge.getVerifyEndAt())
                    .startAt(challenge.getStartDate())
                    .endAt(challenge.getEndDate())
                    .daysOfWeek(challenge.getDaysOfWeek())
                    .verificationType(challenge.getVerificationType())
                    .verificationStatus(verificationStatus)
                    .build();
        }

        // 사용자가 해당 챌린지 creator인지 확인후 삭제
        @Transactional
        public void delete(Long challengeId, Long tossId) {
            // 1단계: 챌린지 존재 여부 확인
            Challenge challenge = challengeRepository.findById(challengeId)
                    .orElseThrow(() -> new CustomException(ChallengeErrorCode.CHALLENGE_NOT_FOUND));

            // 2단계: 사용자가 해당 챌린지의 멤버인지 확인
            ChallengeMember member = challengeMemberRepository
                    .findByChallengeIdAndUserTossIdWithFetch(challengeId, tossId)
                    .orElseThrow(() -> new CustomException(ChallengeMemberErrorCode.NOT_CHALLENGE_MEMBER));

            // 3단계: 생성자(CREATOR) 권한 확인 (삭제 권한)
            if (member.getRole() != com.paynalty.domain.challengemember.MemberRole.CREATOR) {
                throw new CustomException(ChallengeErrorCode.NOT_CHALLENGE_CREATOR_FOR_DELETE);
            }

            // 4단계: 챌린지 삭제 (Cascade로 관련 데이터 자동 삭제)
            challengeRepository.delete(challenge);
        }




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
    private Integer calculateWeeklyProgressCount(Long challengeId, Long tossId) {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(DayOfWeek.MONDAY); // 이번주 월요일
        LocalDate weekEnd = weekStart.plusDays(6); // 이번주 일요일

        Long count = challengeVerificationRepository.countWeeklyVerifications(
                challengeId,
                tossId,
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
    private Integer calculateWeeklyProgressCountForDate(Long challengeId, Long tossId, LocalDate referenceDate) {
        LocalDate weekStart = referenceDate.with(DayOfWeek.MONDAY); // 기준 날짜가 포함된 주의 월요일
        LocalDate weekEnd = weekStart.plusDays(6); // 해당 주의 일요일

        Long count = challengeVerificationRepository.countWeeklyVerifications(
                challengeId,
                tossId,
                weekStart,
                weekEnd
        );

        return count.intValue();
    }


    /**
     * 오늘의 인증 상태를 판단합니다.
     *
     * 로직:
     * 1. 요일 지정 모드 (daysOfWeek가 있는 경우):
     *    - 오늘이 지정된 요일에 포함되어 있으면 → 오늘 인증 여부 확인
     *    - 오늘이 지정된 요일이 아니면 → NOT_VERIFIED (인증 불가능한 날)
     *
     * 2. 주간 횟수 모드 (daysOfWeek가 null이거나 빈 배열인 경우):
     *    - 모든 요일에 인증 가능 → 오늘 인증 여부만 확인
     *    - 주간 인증 횟수는 별도로 체크 (frequency 기준)
     *
     * @param challenge 챌린지 객체
     * @param userId 사용자 ID
     * @return VerificationStatus (VERIFIED 또는 NOT_VERIFIED)
     */
    private VerificationStatus determineVerificationStatus(Challenge challenge, Long tossId) {
        // Case 1: 주간 횟수 모드 (daysOfWeek가 null이거나 비어있으면)
        if (challenge.getDaysOfWeek() == null || challenge.getDaysOfWeek().isEmpty()) {
            // 모든 요일에 인증 가능 - 오늘 인증 여부만 확인
            boolean hasVerifiedToday = challengeVerificationService.checkVerification(challenge.getId(), tossId);
            return VerificationStatus.from(hasVerifiedToday);
        }

        // Case 2: 요일 지정 모드
        // 오늘의 요일을 DayOfWeekType으로 변환
        DayOfWeek todayDayOfWeek = LocalDate.now().getDayOfWeek();
        DayOfWeekType todayDayOfWeekType = DayOfWeekType.from(todayDayOfWeek);

        // 오늘이 챌린지 인증 요일에 포함되어 있는지 확인
        if (challenge.getDaysOfWeek().contains(todayDayOfWeekType)) {
            // 오늘이 인증 요일이면 해당 사용자의 오늘 인증 내역 확인
            boolean hasVerifiedToday = challengeVerificationService.checkVerification(challenge.getId(), tossId);
            return VerificationStatus.from(hasVerifiedToday);
        }

        // 오늘이 인증 요일이 아니면 NOT_VERIFIED (인증 불가능한 날)
        return VerificationStatus.NOT_VERIFIED;
    }

    /**
     * 오늘의 인증 상태를 판단합니다. (challengeId로 조회)
     *
     * @param challengeId 챌린지 ID
     * @param tossId toss ID
     * @return VerificationStatus (VERIFIED 또는 NOT_VERIFIED)
     */
    private VerificationStatus determineVerificationStatus(Long challengeId, Long tossId) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new CustomException(ChallengeErrorCode.CHALLENGE_NOT_FOUND));
        return determineVerificationStatus(challenge, tossId);
    }

}

