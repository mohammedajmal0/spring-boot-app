package com.banking.bank_project.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.banking.bank_project.dto.TransactionDto;
import com.banking.bank_project.entity.Transaction;
import com.banking.bank_project.repository.TransactionRepository;

@Component
public class TransactionImpl implements TransactionService{

    @Autowired
    TransactionRepository transactionRepository;
    @Override
    public void saveTransaction(TransactionDto transactionDto) {
       Transaction transaction=Transaction.builder()
       .transactionType(transactionDto.getTransactionType())
       .accountNumber(transactionDto.getAccountNumber())
       .amount(transactionDto.getAmount())
       .status("SUCCESS")
       .build(); 

       transactionRepository.save(transaction);
       System.out.println("Transaction saved successfully");
    }
    
}
