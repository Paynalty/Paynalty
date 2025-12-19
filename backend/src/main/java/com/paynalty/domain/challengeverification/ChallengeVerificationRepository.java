package com.paynalty.domain.challengeverification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChallengeVerificationRepository extends JpaRepository<ChallengeVerification, Long> {

}
