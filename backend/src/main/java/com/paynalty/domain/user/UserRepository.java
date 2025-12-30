package com.paynalty.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // email로 user 찾기
    @Query("SELECT u FROM User u WHERE u.email  LIKE %:identifier% OR u.name LIKE %:identifier% ")
    List<User> findByEmailOrName(@Param("identifier") String identifier);

    Optional<User> findByNameAndPhoneNum(String Name, String phoneNUm);
}

