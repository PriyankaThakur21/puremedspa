package com.project.app.repository;

import com.project.app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoginRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.username = :username OR u.phoneNumber = :phoneNumber OR u.email = :email")
    Optional<User> findByUserNameOrPhoneNumberOrEmail(String username, String phoneNumber, String email);

    @Query("Select u from User u where u.username = :username")
    Optional<User> findByUsername(String username);
}
