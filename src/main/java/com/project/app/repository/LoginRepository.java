package com.project.app.repository;

import com.project.app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginRepository extends JpaRepository<User, Long> {

    @Query("Select count(u) from User u where u.username =:username")
    Integer countByUserName(String username);

    @Query("Select count(u) from User u where u.phoneNumber = :phoneNumber")
    Integer countByPhoneNumber(String phoneNumber);

    @Query("Select count(u) from User u where u.email= :email")
    Integer countByEmail(String email);

    @Query("Select u from User u where u.username = :username")
    User findByUsername(String username);
}
