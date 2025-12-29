package com.paynalty.domain.challengewindow;

// run every 10 minutes
// set time zone to KST
public class ChallengeWindowEnforcer {

    // STEP1. (1)ChallengeWindow.-status가 PENDING이고 (2)ChallengeWindow.-EndAt이 현재 LocalDateTime보다 늦은 records 확인

    // STEP2. Challenge 성공 여부 판단 후 상태 업데이트
    // TODO 1: 재현님 Challenge 달성 여부 검증 방식 확정 필요 (12/29 15:15)
    // TODO 2: 재현님, 민구님 FE ‘현재’ 진행해야 하는 Challenge의 sub task 어떻게 받기를 원하는지 확인
    // 유저가 챌린지 상태 업데이트 → ChallengeWindowEnforcer에서 업로드 된 내역 있는지 확인
    // 어느 table에서 해당 정보를 확인하면 되는지 확인

    // STEP3. TODO 2 정리되면 마지막 로직 결정
    // enforcer는 단순 벌금 체크만 할 것인지 혹은 기타 기능을 부여할 것인지

}