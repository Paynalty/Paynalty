package com.paynalty.domain.challengemember;

import com.paynalty.domain.challenge.Challenge;
import com.paynalty.domain.challenge.ChallengeRepository;
import com.paynalty.domain.user.User;
import com.paynalty.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChallengeMemberService {
    private final ChallengeMemberRepository challengeMemberRepository;
    private final ChallengeRepository challengeRepository;
    private final UserService userService;

    /**
     * 연락처에서 선택한 친구들을 챌린지에 추가합니다.
     * 전화번호 목록을 받아서 해당하는 사용자들을 찾아 챌린지 멤버로 추가합니다.
     */
    @Transactional
    public List<ChallengeMemberResponse> addMembersFromContacts(AddMemberFromContactsRequest request) {
        // 챌린지 존재 확인
        Challenge challenge = challengeRepository.findById(request.getChallengeId())
                .orElseThrow(() -> new IllegalArgumentException("챌린지를 찾을 수 없습니다."));

        List<ChallengeMemberResponse> addedMembers = new ArrayList<>();
        List<String> notFoundPhoneNumbers = new ArrayList<>();

        for (String phoneNumber : request.getPhoneNumbers()) {
            // 전화번호로 사용자 찾기
            User user = userService.findByPhoneNumber(phoneNumber);
            
            if (user == null) {
                notFoundPhoneNumbers.add(phoneNumber);
                continue;
            }

            // 이미 챌린지 멤버인지 확인
            boolean alreadyExists = challengeMemberRepository.existsByChallengeIdAndUserId(
                    challenge.getId(), user.getId());

            if (alreadyExists) {
                continue; // 이미 추가된 멤버는 스킵
            }

            // 챌린지 멤버 생성 및 저장
            ChallengeMember challengeMember = new ChallengeMember();
            challengeMember.setUser(user);
            challengeMember.setChallenge(challenge);
            challengeMember.setJoinedAt(LocalDateTime.now());

            ChallengeMember saved = challengeMemberRepository.save(challengeMember);
            addedMembers.add(ChallengeMemberResponse.from(saved));
        }

        // 일부 전화번호로 사용자를 찾지 못한 경우 에러 처리
        if (!notFoundPhoneNumbers.isEmpty() && addedMembers.isEmpty()) {
            throw new IllegalArgumentException("연락처에서 사용자를 찾을 수 없습니다.");
        }

        return addedMembers;
    }

    /**
     * 챌린지 멤버 목록 조회
     */
    public List<ChallengeMemberResponse> getChallengeMembers(Long challengeId) {
        List<ChallengeMember> members = challengeMemberRepository.findByChallengeId(challengeId);
        return members.stream()
                .map(ChallengeMemberResponse::from)
                .collect(Collectors.toList());
    }
}
