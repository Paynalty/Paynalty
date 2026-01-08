package com.paynalty.domain.penalty;

import com.paynalty.domain.challengemember.ChallengeMember;
import com.paynalty.domain.challengemember.ChallengeMemberRepository;
import com.paynalty.domain.challenge.Challenge;
import com.paynalty.global.error.ChallengeMemberErrorCode;
import com.paynalty.global.error.CustomException;
import com.paynalty.global.error.PenaltyErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PenaltyService {

    private final PenaltyRepository penaltyRepository;
    private final ChallengeMemberRepository challengeMemberRepository;


    /**
     * ChallengeMember와 금액을 직접 받아 벌금을 생성합니다.
     * (내부 서비스 간 호출용)
     *
     * @param challengeMember 챌린지 멤버
     * @param amount 벌금 금액
     * @return 생성된 벌금
     */
    @Transactional
    public Penalty create(ChallengeMember challengeMember, Long amount) {
        // 벌금 금액 검증
        if (amount == null || amount <= 0) {
            throw new CustomException(PenaltyErrorCode.INVALID_PENALTY_AMOUNT);
        }

        // 벌금 생성
        Penalty penalty = Penalty.builder()
                .challengeMember(challengeMember)
                .penaltyAmount(amount)
                .build();

        return penaltyRepository.save(penalty);
    }

    /**
     * 본인 벌금 내역 조회
     *
     * @param challengeId 챌린지 ID
     * @param tossId 사용자 토스 ID
     * @return 벌금 내역 리스트
     */
    @Transactional(readOnly = true)
    public List<PenaltyResponse> getMyPenalty(Long challengeId, Long tossId) {
        List<Penalty> penalties = penaltyRepository.findByChallengeIdAndTossId(challengeId, tossId);
        return penalties.stream()
                .map(PenaltyResponse::from)
                .toList();
    }

    /**
     * 챌린지의 모든 벌금 내역 조회
     * 사용자가 해당 챌린지에 참여하고 있는지 검증 후 모든 벌금 내역을 반환합니다.
     *
     * @param challengeId 챌린지 ID
     * @param tossId 사용자 토스 ID (검증용)
     * @return 벌금 내역 리스트 (최신순 정렬)
     * @throws CustomException 챌린지에 참여하지 않은 경우
     */
    @Transactional(readOnly = true)
    public List<PenaltyResponse> getAllPenalties(Long challengeId, Long tossId) {
        // 사용자가 해당 챌린지에 참여하는지 검증
        ChallengeMember member = challengeMemberRepository
                .findByChallengeIdAndUserTossIdWithFetch(challengeId, tossId)
                .orElseThrow(() -> new CustomException(ChallengeMemberErrorCode.CHALLENGE_MEMBER_NOT_FOUND));

        // 해당 챌린지의 모든 벌금 내역 조회
        List<Penalty> penalties = penaltyRepository.findAllByChallengeId(challengeId);
        
        return penalties.stream()
                .map(PenaltyResponse::from)
                .toList();
    }

    /**
     * 패널티 결제 완료 처리
     * 패널티의 paid 필드를 true로 변경하고, paidAt을 현재 시간으로 설정합니다.
     *
     * @param penaltyId 패널티 ID
     * @param tossId 사용자 토스 ID (권한 검증용)
     * @return 결제 완료된 패널티 응답
     * @throws CustomException 패널티가 없거나, 이미 결제되었거나, 권한이 없는 경우
     */
    @Transactional
    public PenaltyResponse paidComplete(Long penaltyId, Long tossId) {
        // penaltyId로 패널티 찾기
        Penalty penalty = penaltyRepository.findById(penaltyId)
                .orElseThrow(() -> new CustomException(PenaltyErrorCode.PENALTY_NOT_FOUND));

        // 해당 penalty의 paid가 이미 true면 오류코드. 이미 결제 되었습니다 표시
        if (penalty.getPaid()) {
            throw new CustomException(PenaltyErrorCode.PENALTY_ALREADY_PAID);
        }

        // 해당 패널티의 챌린지 멤버 -> 사용자 tossId가 현재 로그인한 사용자의 토스 id와 같은지 검증
        if (!penalty.getChallengeMember().getUser().getTossId().equals(tossId)) {
            // 같지 않다면 '사용자가 해당 패널티의 권한이 없습니다' 오류코드 전달
            throw new CustomException(PenaltyErrorCode.UNAUTHORIZED_PENALTY_ACCESS);
        }

        // 같으면 해당 패널티의 paid true로 변경, 결제시간을 현재로 수정
        penalty.setPaid(true);
        penalty.setPaidAt(java.time.LocalDateTime.now());

        // 저장소 쿼리 실행
        Penalty savedPenalty = penaltyRepository.save(penalty);

        return PenaltyResponse.from(savedPenalty);
    }

    /**
     * 사용자의 미납 패널티 조회
     * 특정 챌린지에서 tossId와 관련된 패널티 데이터 중 paid 값이 false인 패널티만 조회합니다.
     *
     * @param challengeId 챌린지 ID
     * @param tossId 사용자 토스 ID
     * @return 미납 패널티 내역 리스트 (최신순 정렬)
     */
    @Transactional(readOnly = true)
    public List<PenaltyResponse> getAllNonPaidPenalty(Long challengeId, Long tossId) {
        // 특정 챌린지에서 tossId와 관련된 패널티 데이터 중 paid 값이 false인 패널티만 불러오기
        List<Penalty> penalties = penaltyRepository.findByChallengeIdAndTossIdAndPaidFalse(challengeId, tossId);
        return penalties.stream()
                .map(PenaltyResponse::from)
                .toList();
    }

    // 챌린지 서비스 476번에 사용할 매서드 만들기
    public PenaltyOverview getPenaltyOverview(Long challengeId){
        // 벌금 총합
        Long total = penaltyRepository.sumAmountByChallengeId(challengeId);
        // 납부 총합
        Long paid = penaltyRepository.sumAmountByChallengeIdAndPaid(challengeId);
        // 미납 총합
        Long nonPaid = penaltyRepository.sumAmountByChallengeIdAndNonPaid(challengeId);

        return PenaltyOverview.builder()
                .totalPenalty(total)
                .paidPenalty(paid)
                .nonPaidPenalty(nonPaid)
                .build();
    }

    /**
     * 특정 멤버의 주간 패널티 현황 리스트 생성
     * 챌린지 기간을 주 단위로 나누어 각 주의 패널티 정보를 생성합니다.
     *
     * @param challengeMember 챌린지 멤버
     * @return 주간 패널티 현황 리스트
     */
    public List<WeeklyPenalty> getWeeklyPenaltiesByMember(ChallengeMember challengeMember) {
        Challenge challenge = challengeMember.getChallenge();
        LocalDate startDate = challenge.getStartDate();
        LocalDate endDate = challenge.getEndDate();
        
        List<WeeklyPenalty> weeklyPenalties = new ArrayList<>();
        
        // 첫 주 계산: 시작일이 월요일이 아니라면 시작일 ~ 시작일이 포함된 그 주의 일요일
        LocalDate firstWeekStart = startDate;
        LocalDate firstWeekEnd;
        
        if (startDate.getDayOfWeek() == DayOfWeek.MONDAY) {
            // 시작일이 월요일이면 첫 주는 월요일 ~ 일요일
            firstWeekEnd = startDate.with(DayOfWeek.SUNDAY);
        } else {
            // 시작일이 월요일이 아니면 시작일 ~ 시작일이 포함된 그 주의 일요일
            firstWeekEnd = startDate.with(DayOfWeek.SUNDAY);
        }
        
        // 첫 주가 마감일을 포함하는지 확인 (챌린지가 1주일 미만인 경우)
        if (firstWeekEnd.isAfter(endDate) || firstWeekEnd.isEqual(endDate)) {
            // 첫 주가 마감일을 포함하면 첫 주만 생성하고 종료
            firstWeekEnd = endDate;
            WeeklyPenalty firstWeek = createWeeklyPenalty(challengeMember, firstWeekStart, firstWeekEnd);
            weeklyPenalties.add(firstWeek);
            return weeklyPenalties;
        }
        
        // 첫 주 패널티 생성
        WeeklyPenalty firstWeek = createWeeklyPenalty(challengeMember, firstWeekStart, firstWeekEnd);
        weeklyPenalties.add(firstWeek);
        
        // 중간 주들 계산 (월요일 ~ 일요일)
        LocalDate currentWeekStart = firstWeekEnd.plusDays(1); // 다음 주 월요일
        
        // 마지막 주 전까지의 주들 처리
        while (currentWeekStart.isBefore(endDate) || currentWeekStart.isEqual(endDate)) {
            LocalDate currentWeekEnd = currentWeekStart.with(DayOfWeek.SUNDAY);
            
            // 마감일이 포함된 주인지 확인
            if (currentWeekEnd.isAfter(endDate)) {
                // 마지막 주: 마감일이 일요일이 아니면 마감일이 포함된 그 주의 월요일 ~ 마감일
                if (endDate.getDayOfWeek() == DayOfWeek.MONDAY) {
                    // 마감일이 월요일이면 월요일 하루만
                    currentWeekStart = endDate;
                    currentWeekEnd = endDate;
                } else {
                    // 마감일이 포함된 그 주의 월요일 ~ 마감일
                    currentWeekStart = endDate.with(DayOfWeek.MONDAY);
                    currentWeekEnd = endDate;
                }
                
                WeeklyPenalty lastWeek = createWeeklyPenalty(challengeMember, currentWeekStart, currentWeekEnd);
                weeklyPenalties.add(lastWeek);
                break;
            } else if (currentWeekEnd.isEqual(endDate)) {
                // 마감일이 일요일인 경우
                WeeklyPenalty week = createWeeklyPenalty(challengeMember, currentWeekStart, currentWeekEnd);
                weeklyPenalties.add(week);
                break;
            } else {
                // 중간 주
                WeeklyPenalty week = createWeeklyPenalty(challengeMember, currentWeekStart, currentWeekEnd);
                weeklyPenalties.add(week);
                currentWeekStart = currentWeekEnd.plusDays(1);
            }
        }
        
        return weeklyPenalties;
    }

    /**
     * 특정 주간 기간의 패널티 정보를 생성합니다.
     *
     * @param challengeMember 챌린지 멤버
     * @param weekStart 주간 시작일
     * @param weekEnd 주간 종료일
     * @return 주간 패널티 현황
     */
    private WeeklyPenalty createWeeklyPenalty(ChallengeMember challengeMember, LocalDate weekStart, LocalDate weekEnd) {
        // 해당 주간의 패널티 조회
        List<Penalty> penalties = penaltyRepository.findByChallengeMemberIdAndWeekRange(
                challengeMember.getId(),
                weekStart,
                weekEnd
        );
        
        // 금액 계산
        Long totalAmount = penalties.stream()
                .mapToLong(Penalty::getPenaltyAmount)
                .sum();
        
        Long paidAmount = penalties.stream()
                .filter(Penalty::getPaid)
                .mapToLong(Penalty::getPenaltyAmount)
                .sum();
        
        Long nonPaidAmount = penalties.stream()
                .filter(p -> !p.getPaid())
                .mapToLong(Penalty::getPenaltyAmount)
                .sum();
        
        // PenaltyResponse 리스트 생성
        List<PenaltyResponse> penaltyResponses = penalties.stream()
                .map(PenaltyResponse::from)
                .toList();
        
        // LocalDateTime으로 변환 (startAt: 해당 주 시작일 00:00, endAt: 해당 주 종료일 23:59:59)
        LocalDateTime startAt = weekStart.atStartOfDay();
        LocalDateTime endAt = weekEnd.atTime(LocalTime.MAX);
        
        return WeeklyPenalty.builder()
                .startAt(startAt)
                .endAt(endAt)
                .totalAmount(totalAmount)
                .paidAmount(paidAmount)
                .nonPaidAmount(nonPaidAmount)
                .penaltyList(penaltyResponses)
                .build();
    }

}

