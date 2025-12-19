package com.paynalty.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * email 또는 name으로 사용자를 찾는 메서드
     * 현재는 email만 비교합니다.
     * name 필드가 User 엔티티에 추가되면 아래 쿼리를 다음과 같이 수정하세요:
     * @Query("SELECT u FROM User u WHERE u.email = :identifier OR u.name = :identifier")
     */
    @Query("SELECT u FROM User u WHERE u.email = :identifier")
    Optional<User> findByEmailOrName(@Param("identifier") String identifier);
}

