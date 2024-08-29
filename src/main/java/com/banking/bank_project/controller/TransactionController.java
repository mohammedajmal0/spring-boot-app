package com.banking.bank_project.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.banking.bank_project.entity.Transaction;
import com.banking.bank_project.service.impl.BankStatement;
import com.itextpdf.text.DocumentException;

import lombok.AllArgsConstructor;

import java.io.FileNotFoundException;
import java.util.List;
@RestController
@RequestMapping("/api/transaction")
@AllArgsConstructor
public class TransactionController {

    private BankStatement bankStatement;

    @GetMapping
    public List<Transaction> generateBankStatement(
        @RequestParam String accountNumber,
        @RequestParam String startDate,
        @RequestParam String endDate) throws FileNotFoundException, DocumentException{
        return bankStatement.generateStatement(accountNumber, startDate, endDate);
    }
}
