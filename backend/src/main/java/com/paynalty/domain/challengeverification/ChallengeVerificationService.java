package com.paynalty.domain.challengeverification;

import com.paynalty.domain.challenge.Challenge;
import com.paynalty.domain.challenge.ChallengeRepository;
import com.paynalty.domain.challenge.ChallengeStatus;
import com.paynalty.domain.challenge.DayOfWeekType;
import com.paynalty.domain.challengemember.ChallengeMember;
import com.paynalty.domain.challengemember.ChallengeMemberRepository;
import com.paynalty.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChallengeVerificationService {
    private final ChallengeVerificationRepository challengeVerificationRepository;
    private final ChallengeMemberRepository challengeMemberRepository;
    private final ChallengeRepository challengeRepository;


    @Transactional
    public ChallengeVerificationResponse create(Long challengeId, Long userId, ChallengeVerificationRequest request) {
        // 1단계: 챌린지 멤버 확인 (Challenge + User 함께 조회하여 성능 최적화)
        ChallengeMember member = challengeMemberRepository
                .findByChallengeIdAndUserIdWithFetch(challengeId, userId)
                .orElseThrow(() -> new IllegalStateException("챌린지에 참여하지 않은 사용자입니다."));
        
        Challenge challenge = member.getChallenge();
        User user = member.getUser();

        // 2단계: 챌린지 상태 검증 (ACTIVE만 인증 가능)
        ChallengeStatus status = challenge.calculateStatus();
        if (status != ChallengeStatus.ACTIVE) {
            throw new IllegalStateException("인증 가능한 챌린지가 아닙니다. (현재 상태: " + status + ")");
        }

        // 3단계: 인증 시간대 검증  - 겹치는 로직 삭제
        LocalTime now = LocalTime.now();
        LocalTime startTime = challenge.getVerifyStartAt();
        LocalTime endTime = challenge.getVerifyEndAt();
        
        if (startTime != null && endTime != null) {
            if (now.isBefore(startTime) || now.isAfter(endTime)) {
                throw new IllegalStateException(
                    String.format("인증 가능 시간이 아닙니다. (가능 시간: %s ~ %s)", startTime, endTime)
                );
            }
        }

        // 4단계: 인증 요일 검증 (daysOfWeek가 설정된 경우만) - 삭제
        List<DayOfWeekType> allowedDays = challenge.getDaysOfWeek();
        if (allowedDays != null && !allowedDays.isEmpty()) {
            DayOfWeek todayDayOfWeek = LocalDate.now().getDayOfWeek();
            DayOfWeekType todayDayOfWeekType = DayOfWeekType.from(todayDayOfWeek);
            
            if (!allowedDays.contains(todayDayOfWeekType)) {
                throw new IllegalStateException(
                    "오늘은 인증 가능한 요일이 아닙니다. (인증 가능 요일: " + allowedDays + ")"
                );
            }
        }

        // 5단계: 중복 인증 검증 (1일 1회만 가능)
        LocalDate today = LocalDate.now();
        
        boolean alreadyVerifiedToday = challengeVerificationRepository
                .existsByChallengeIdAndUserIdAndDate(challengeId, user.getId(), today);
        
        if (alreadyVerifiedToday) {
            throw new IllegalStateException("오늘 이미 인증을 완료했습니다. (1일 1회만 가능)");
        }

        // 6단계: 주간 인증 횟수 제한 검증
        LocalDate weekStart = today.with(DayOfWeek.MONDAY);
        LocalDate weekEnd = today.with(DayOfWeek.SUNDAY);
        
        Long weeklyCount = challengeVerificationRepository
                .countWeeklyVerifications(challengeId, user.getId(), weekStart, weekEnd);
        
        Integer maxFrequency = challenge.getFrequency();
        if (weeklyCount >= maxFrequency) {
            throw new IllegalStateException(
                String.format("주간 인증 횟수를 초과했습니다. (%d/%d)", weeklyCount, maxFrequency)
            );
        }

        // 7단계: ChallengeVerification 생성
        ChallengeVerification challengeVerification = ChallengeVerification.builder()
                .user(user)
                .challenge(challenge)
                .date(today)
                .imageUrl(request.getImageUrl())
                .status(VerificationStatus.UNVERIFIED)
                .build();

        ChallengeVerification saved = challengeVerificationRepository.save(challengeVerification);

        return ChallengeVerificationResponse.from(saved);
    }

    // 인증 검증 중복 요소
    /**
     * 사용자가 오늘 해당 챌린지에 대해 인증했는지 확인합니다.
     *
     * @param challengeId 챌린지 ID
     * @param userId 사용자 ID
     * @return 오늘 인증했으면 true, 아니면 false
     */
    public Boolean checkVerification(Long challengeId, Long userId){
        LocalDate today = LocalDate.now();
        return challengeVerificationRepository.existsByChallengeIdAndUserIdAndDate(challengeId, userId, today);
    }


    // 해당 챌린지의 최신 인증 데이터 가져오기
    public ChallengeVerificationResponse getLatestVerification(Long challengeId) {
        ChallengeVerification cv = challengeVerificationRepository
                .findTopByChallengeIdOrderByDateDescIdDesc(challengeId)
                .orElseThrow(() -> new IllegalStateException("해당 챌린지에 인증 데이터가 없습니다."));
        return ChallengeVerificationResponse.from(cv);
    }

    // 맴버별 당일이 포함된 주간 인증 횟수 가져오기
    public List<MembersVerificationCountResponse> getChallengeMemberWeeklyVerificationCounts(Long challengeId) {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(DayOfWeek.MONDAY);
        LocalDate weekEnd = today.with(DayOfWeek.SUNDAY);

        return challengeVerificationRepository
                .countWeeklyVerificationByChallengeMembers(challengeId, weekStart, weekEnd);
    }


    // 인증 데이터 최신순 불러오기(사용자것만) // 현재 사용 안하는 매서드
    public Slice<ChallengeVerificationResponse> getMyVerifications(
            Long challengeId, 
            Long userId, 
            int page, 
            int size
    ) {
        // Pageable 생성 (페이지 번호, 크기만 지정 - 정렬은 메서드명으로 처리)
        Pageable pageable = PageRequest.of(page, size);
        
        // Slice<ChallengeVerification> 조회
        Slice<ChallengeVerification> verificationSlice = challengeVerificationRepository
                .findByChallengeIdAndUserIdOrderByDateDescIdDesc(challengeId, userId, pageable);
        
        // Slice<ChallengeVerification> → Slice<ChallengeVerificationResponse> 변환
        return verificationSlice.map(ChallengeVerificationResponse::from);
    }

    // 챌린지의 모든 참여자 인증 데이터를 최신순으로 페이징하여 조회합니다 (무한 스크롤)
    public Slice<ChallengeVerificationResponse> getAllVerifications(
            Long challengeId,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Slice<ChallengeVerification> verificationSlice = challengeVerificationRepository
                .findByChallengeIdOrderByDateDescIdDesc(challengeId, pageable);
        return verificationSlice.map(ChallengeVerificationResponse::from);
    }


}
