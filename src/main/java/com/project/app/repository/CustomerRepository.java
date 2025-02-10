package com.project.app.repository;

import com.project.app.entity.Customer;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    @Query("Select c from Customer c where email = :email")
    Optional<Customer> findByEmail(String email);

    @Modifying
    @Transactional
    @Query("DELETE FROM Customer c WHERE c.email = :email")
    void delete(String email);
}
