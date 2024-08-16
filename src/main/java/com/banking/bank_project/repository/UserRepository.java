package com.banking.bank_project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banking.bank_project.entity.User;


public interface UserRepository extends JpaRepository<User,Long> {
    Boolean existsByEmail(String email);

    Boolean existsByAccountNumber(String accountNumber);

    User findByAccountNumber(String accountNumber);
}
