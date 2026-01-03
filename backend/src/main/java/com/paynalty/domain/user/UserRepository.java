package com.paynalty.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * 이메일 또는 이름으로 사용자 검색
     * - 이메일: @ 앞부분(로컬 파트)만 검색
     * - 이름: 부분 일치 검색
     */
    @Query("""
        SELECT u FROM User u 
        WHERE SUBSTRING(u.email, 1, LOCATE('@', u.email) - 1) LIKE %:identifier%
           OR u.name LIKE %:identifier%
        """)
    List<User> findByEmailOrName(@Param("identifier") String identifier);


    Optional<User> findByNameAndPhoneNum(String Name, String phoneNUm);

    Optional<User> findByTossId(Long tossId);
}

