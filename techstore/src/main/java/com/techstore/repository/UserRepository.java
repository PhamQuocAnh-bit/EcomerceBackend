package com.techstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.techstore.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);

    @Query("select u from User u join fetch u.role where u.email = :email")
    Optional<User> findByEmailWithRole(@Param("email") String email);
}
