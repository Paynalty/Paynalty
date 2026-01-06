package com.paynalty.domain.challengeverification;

import com.paynalty.domain.challenge.Challenge;
import com.paynalty.domain.challenge.ChallengeRepository;
import com.paynalty.domain.challenge.ChallengeStatus;
import com.paynalty.domain.challenge.DayOfWeekType;
import com.paynalty.domain.challengemember.ChallengeMember;
import com.paynalty.domain.challengemember.ChallengeMemberRepository;
import com.paynalty.domain.user.User;
import com.paynalty.global.error.ChallengeMemberErrorCode;
import com.paynalty.global.error.CustomException;
import com.paynalty.global.error.ChallengeVerificationErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    private final FileStorage fileStorage;


    @Transactional
    public ChallengeVerificationResponse create(Long challengeId, Long tossId, MultipartFile image) {
        // 1단계: 챌린지 멤버 확인 (Challenge + User 함께 조회하여 성능 최적화)
        ChallengeMember member = challengeMemberRepository
                .findByChallengeIdAndUserTossIdWithFetch(challengeId, tossId)
                .orElseThrow(() -> new CustomException(ChallengeMemberErrorCode.NOT_CHALLENGE_MEMBER));
        
        Challenge challenge = member.getChallenge();
        User user = member.getUser();

        // 2단계: 챌린지 상태 검증 (ACTIVE만 인증 가능)
        ChallengeStatus status = challenge.calculateStatus();
        if (status != ChallengeStatus.ACTIVE) {
            throw new CustomException(ChallengeVerificationErrorCode.CHALLENGE_NOT_ACTIVE);
        }

        // 3단계: 인증 시간대 검증
        LocalTime now = LocalTime.now();
        LocalTime startTime = challenge.getVerifyStartAt();
        LocalTime endTime = challenge.getVerifyEndAt();
        
        if (startTime != null && endTime != null) {
            if (now.isBefore(startTime) || now.isAfter(endTime)) {
                throw new CustomException(ChallengeVerificationErrorCode.OUTSIDE_VERIFICATION_TIME);
            }
        }

        // 4단계: 인증 요일 검증 (daysOfWeek가 설정된 경우만)
        List<DayOfWeekType> allowedDays = challenge.getDaysOfWeek();
        if (allowedDays != null && !allowedDays.isEmpty()) {
            DayOfWeek todayDayOfWeek = LocalDate.now().getDayOfWeek();
            DayOfWeekType todayDayOfWeekType = DayOfWeekType.from(todayDayOfWeek);
            
            if (!allowedDays.contains(todayDayOfWeekType)) {
                throw new CustomException(ChallengeVerificationErrorCode.NOT_ALLOWED_DAY);
            }
        }

        // 5단계: 중복 인증 검증 (1일 1회만 가능)
        LocalDate today = LocalDate.now();
        
        boolean alreadyVerifiedToday = challengeVerificationRepository
                .existsByChallengeIdAndUserTossIdAndDate(challengeId, tossId, today);
        
        if (alreadyVerifiedToday) {
            throw new CustomException(ChallengeVerificationErrorCode.ALREADY_VERIFIED_TODAY);
        }

        // 6단계: 주간 인증 횟수 제한 검증
        LocalDate weekStart = today.with(DayOfWeek.MONDAY);
        LocalDate weekEnd = today.with(DayOfWeek.SUNDAY);
        
        Long weeklyCount = challengeVerificationRepository
                .countWeeklyVerifications(challengeId, tossId, weekStart, weekEnd);
        
        Integer maxFrequency = challenge.getFrequency();
        if (weeklyCount >= maxFrequency) {
            throw new CustomException(ChallengeVerificationErrorCode.WEEKLY_FREQUENCY_EXCEEDED);
        }

        // 7단계: 이미지 파일 업로드
        String savedFileName = fileStorage.upload(image);

        // 8단계: ChallengeVerification 생성
        ChallengeVerification challengeVerification = ChallengeVerification.builder()
                .user(user)
                .challenge(challenge)
                .date(today)
                .verifiedAt(LocalDateTime.now())
                .imageUrl(savedFileName)
                .build();

        ChallengeVerification saved = challengeVerificationRepository.save(challengeVerification);

        return ChallengeVerificationResponse.from(saved);
    }

    // 인증 검증 중복 요소
    /**
     * 사용자가 오늘 해당 챌린지에 대해 인증했는지 확인합니다.
     *
     * @param challengeId 챌린지 ID
     * @param tossId 사용자 토스 ID
     * @return 오늘 인증했으면 true, 아니면 false
     */
    public Boolean checkVerification(Long challengeId, Long tossId){
        LocalDate today = LocalDate.now();
        return challengeVerificationRepository.existsByChallengeIdAndUserTossIdAndDate(challengeId, tossId, today);
    }


    // 해당 챌린지의 최신 인증 데이터 가져오기
    public ChallengeVerificationResponse getLatestVerification(Long challengeId) {
        ChallengeVerification cv = challengeVerificationRepository
                .findTopByChallengeIdOrderByDateDescIdDesc(challengeId)
                .orElseThrow(() -> new CustomException(ChallengeVerificationErrorCode.NO_VERIFICATION_DATA));
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
            Long tossId, 
            int page, 
            int size
    ) {
        // Pageable 생성 (페이지 번호, 크기만 지정 - 정렬은 메서드명으로 처리)
        Pageable pageable = PageRequest.of(page, size);
        
        // Slice<ChallengeVerification> 조회
        Slice<ChallengeVerification> verificationSlice = challengeVerificationRepository
                .findByChallengeIdAndUserTossIdOrderByDateDescIdDesc(challengeId, tossId, pageable);
        
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

    // 인증 데이터 수정 (이미지 파일 변경)
    @Transactional
    public ChallengeVerificationResponse update(Long verificationId, Long tossId, MultipartFile image) {
        // 1단계: 인증 데이터 조회
        ChallengeVerification verification = challengeVerificationRepository
                .findById(verificationId)
                .orElseThrow(() -> new CustomException(ChallengeVerificationErrorCode.VERIFICATION_NOT_FOUND));

        // 2단계: 본인 인증 데이터인지 확인
        if (!verification.getUser().getTossId().equals(tossId)) {
            throw new CustomException(ChallengeVerificationErrorCode.NOT_VERIFICATION_OWNER_FOR_UPDATE);
        }

        // 3단계: 당일 인증인지 확인
        LocalDate today = LocalDate.now();
        if (!verification.getDate().isEqual(today)) {
            throw new CustomException(ChallengeVerificationErrorCode.CANNOT_UPDATE_PAST_VERIFICATION);
        }

        // 4단계: 챌린지 정보 가져오기
        Challenge challenge = verification.getChallenge();

        // 5단계: 인증 시간대 검증
        LocalTime now = LocalTime.now();
        LocalTime startTime = challenge.getVerifyStartAt();
        LocalTime endTime = challenge.getVerifyEndAt();
        
        if (startTime != null && endTime != null) {
            if (now.isBefore(startTime) || now.isAfter(endTime)) {
                throw new CustomException(ChallengeVerificationErrorCode.CANNOT_UPDATE_OUTSIDE_TIME_RANGE);
            }
        }

        // 6단계: 인증 요일 검증 (daysOfWeek가 설정된 경우만)
        List<DayOfWeekType> allowedDays = challenge.getDaysOfWeek();
        if (allowedDays != null && !allowedDays.isEmpty()) {
            DayOfWeek todayDayOfWeek = today.getDayOfWeek();
            DayOfWeekType todayDayOfWeekType = DayOfWeekType.from(todayDayOfWeek);
            
            if (!allowedDays.contains(todayDayOfWeekType)) {
                throw new CustomException(ChallengeVerificationErrorCode.CANNOT_UPDATE_OUTSIDE_ALLOWED_DAYS);
            }
        }

        // 7단계: 기존 파일 삭제
        String oldFileName = verification.getImageUrl();
        if (oldFileName != null && !oldFileName.isEmpty()) {
            try {
                fileStorage.delete(oldFileName);
            } catch (Exception e) {
                // 파일 삭제 실패해도 계속 진행 (로깅만)
                // 실제 운영 환경에서는 로깅 추가 권장
            }
        }

        // 8단계: 새 이미지 파일 업로드
        String savedFileName = fileStorage.upload(image);

        // 9단계: 이미지 URL 수정
        verification.updateImageUrl(savedFileName);

        // 10단계: 변경 사항 저장 및 응답
        return ChallengeVerificationResponse.from(verification);
    }

    // 인증 데이터 삭제
    @Transactional
    public void delete(Long verificationId, Long tossId) {
        // 1단계: 인증 데이터 조회
        ChallengeVerification verification = challengeVerificationRepository
                .findById(verificationId)
                .orElseThrow(() -> new CustomException(ChallengeVerificationErrorCode.VERIFICATION_NOT_FOUND));

        // 2단계: 본인 인증 데이터인지 확인
        if (!verification.getUser().getTossId().equals(tossId)) {
            throw new CustomException(ChallengeVerificationErrorCode.NOT_VERIFICATION_OWNER_FOR_DELETE);
        }

        // 3단계: 파일 삭제
        String fileName = verification.getImageUrl();
        if (fileName != null && !fileName.isEmpty()) {
            try {
                fileStorage.delete(fileName);
            } catch (Exception e) {
                // 파일 삭제 실패해도 계속 진행 (로깅만)
                // 실제 운영 환경에서는 로깅 추가 권장
            }
        }

        // 4단계: 인증 데이터 삭제
        challengeVerificationRepository.delete(verification);
    }


}
