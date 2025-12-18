package com.paynalty.domain.challengemember;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChallengeMemberRepository extends JpaRepository<ChallengeMember, Long> {
    
    boolean existsByChallengeIdAndUserId(Long challengeId, Long userId);
    
    List<ChallengeMember> findByChallengeId(Long challengeId);
    
    List<ChallengeMember> findByUserId(Long userId);
}
