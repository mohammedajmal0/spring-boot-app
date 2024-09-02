package com.banking.bank_project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.banking.bank_project.dto.BankResponse;
import com.banking.bank_project.dto.CreditDebitRequest;
import com.banking.bank_project.dto.EnquiryRequest;
import com.banking.bank_project.dto.LoginDto;
import com.banking.bank_project.dto.TransferRequest;
import com.banking.bank_project.dto.UserRequest;
import com.banking.bank_project.service.impl.UserService;

@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @Autowired
    UserService userService;

    @PostMapping
    public BankResponse createAccount(@RequestBody UserRequest userRequest){
        return userService.createAccount(userRequest);
    }

    @PostMapping("/login")
    public BankResponse login(@RequestBody LoginDto loginDto) {
       return userService.login(loginDto);
    }
    

    @GetMapping("/balanceEnquiry")
    public BankResponse balanceEnquiry(@RequestBody EnquiryRequest enquiryRequest){
        return userService.balanceEnquiry(enquiryRequest);
    }

    @GetMapping("/nameEnquiry")
    public String nameEnquiry(@RequestBody EnquiryRequest enquiryRequest){
        return userService.nameEnquiry(enquiryRequest);
    }

    @PutMapping("/credit")
    public BankResponse creditAmount(@RequestBody CreditDebitRequest request){
        return userService.creditAccount(request);
    }

    @PutMapping("/debit")
    public BankResponse debitAmount(@RequestBody CreditDebitRequest request){
        return userService.debitAccount(request);
    }

    @PutMapping("/transfer")
    public BankResponse transferAmount(@RequestBody TransferRequest request){
        return userService.transferAmount(request);
    }
}
