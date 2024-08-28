package com.banking.bank_project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banking.bank_project.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction,String>{
    
}
