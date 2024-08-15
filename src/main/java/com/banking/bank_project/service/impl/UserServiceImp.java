package com.banking.bank_project.service.impl;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.banking.bank_project.dto.AccountInfo;
import com.banking.bank_project.dto.BankResponse;
import com.banking.bank_project.dto.EmailDetails;
import com.banking.bank_project.dto.UserRequest;
import com.banking.bank_project.entity.User;
import com.banking.bank_project.repository.UserRepository;
import com.banking.bank_project.utils.AccountUtils;

@Service
public class UserServiceImp implements UserService{

    @Autowired
    UserRepository userRepository;

    @Autowired
    EmailService emailService;

    @Override
    public BankResponse createAccount(UserRequest userRequest) {
        /**
         * creating an account-saving a new user into the db
         * check if user alr exists
         */
        if(userRepository.existsByEmail(userRequest.getEmail())){
            BankResponse response=BankResponse.builder()
            .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
            .responseMessage(AccountUtils.ACCOUNT_EXISTS_MESSAGE)
            .accountInfo(null)
            .build();
            return response;
        }
        User newUser=User.builder()
        .firstName(userRequest.getFirstName())
        .lastName(userRequest.getLastName())
        .gender(userRequest.getGender())
        .otherName(userRequest.getOtherName())
        .address(userRequest.getAddress())
        .stateOfOrigin(userRequest.getStateOfOrigin())
        .accountNumber(AccountUtils.generateAccountNumber())
        .accountBalance(BigDecimal.ZERO)
        .email(userRequest.getEmail())
        .phoneNumber(userRequest.getPhoneNumber())
        .status("ACTIVE")
        .build();

        User savedUser=userRepository.save(newUser);
        EmailDetails emailDetails=EmailDetails.builder()
        .recipient(savedUser.getEmail())
        .subject("Account created Successfully")
        .messageBody("Congratulations ur account has been successfully created.\nyour account details\n"+
        "Account name : "+savedUser.getFirstName() + " " + savedUser.getLastName()+"\n"+
        "Account Number : "+savedUser.getAccountNumber())
        .build();
        emailService.sendEmail(emailDetails);

        return BankResponse.builder()
        .responseCode(AccountUtils.ACCOUNT_CREATED_CODE)
        .responseMessage(AccountUtils.ACCOUNT_CREATED_MESSAGE)
        .accountInfo(AccountInfo.builder()
            .accountBalance(savedUser.getAccountBalance())
            .accountNumber(savedUser.getAccountNumber())
            .accountName(savedUser.getFirstName()+" "+savedUser.getLastName())
            .build())
        .build();
    }
    
    // balance enquiry , name enquiry , credit , debit , transfer
}
