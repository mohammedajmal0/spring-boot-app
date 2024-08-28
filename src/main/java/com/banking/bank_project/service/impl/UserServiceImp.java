package com.banking.bank_project.service.impl;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.banking.bank_project.dto.AccountInfo;
import com.banking.bank_project.dto.BankResponse;
import com.banking.bank_project.dto.CreditDebitRequest;
import com.banking.bank_project.dto.EmailDetails;
import com.banking.bank_project.dto.EnquiryRequest;
import com.banking.bank_project.dto.TransactionDto;
import com.banking.bank_project.dto.TransferRequest;
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

    @Autowired
    TransactionService transactionService;

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

    @Override
    public BankResponse balanceEnquiry(EnquiryRequest request) {
        //check if provided account number exists
        boolean isAccountExists=userRepository.existsByAccountNumber(request.getAccountNumber());
        if(!isAccountExists){
            return BankResponse.builder()
            .accountInfo(null)
            .responseCode(AccountUtils.ACCOUNT_NOT_EXISTS_CODE)
            .responseMessage(AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE)
            .build();
        }
        User foundUser=userRepository.findByAccountNumber(request.getAccountNumber());

        return BankResponse.builder()
        .responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
        .responseMessage(AccountUtils.ACCOUNT_FOUND_MESSAGE)
        .accountInfo(AccountInfo.builder()
            .accountBalance(foundUser.getAccountBalance())
            .accountName(foundUser.getFirstName()+" "+foundUser.getLastName())
            .accountNumber(foundUser.getAccountNumber())
        .build())
        .build();
    }

    @Override
    public String nameEnquiry(EnquiryRequest request) {
        boolean isAccountExists=userRepository.existsByAccountNumber(request.getAccountNumber());
        if(!isAccountExists){
            return "Account doesnt exists";
        }
        User founUser=userRepository.findByAccountNumber(request.getAccountNumber());
        return founUser.getFirstName()+" "+founUser.getLastName();
    }

    @Override
    public BankResponse creditAccount(CreditDebitRequest request) {
        boolean isAccountExists=userRepository.existsByAccountNumber(request.getAccountNumber());
        if(!isAccountExists){
            return BankResponse.builder()
            .accountInfo(null)
            .responseCode(AccountUtils.ACCOUNT_NOT_EXISTS_CODE)
            .responseMessage(AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE)
            .build();
        }

        User userToCredit=userRepository.findByAccountNumber(request.getAccountNumber());
        userToCredit.setAccountBalance(userToCredit.getAccountBalance().add(request.getAmount()));
        userRepository.save(userToCredit);

        TransactionDto transactionDto=TransactionDto.builder()
        .accountNumber(userToCredit.getAccountNumber())
        .amount(request.getAmount())
        .transactionType("credit")
        .build();

       transactionService.saveTransaction(transactionDto);
        return BankResponse.builder()
        .accountInfo(AccountInfo.builder()
            .accountBalance(userToCredit.getAccountBalance())
            .accountName(userToCredit.getFirstName())
            .accountNumber(userToCredit.getAccountNumber())
            .build())
        .responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
        .responseMessage(AccountUtils.ACCOUNT_FOUND_MESSAGE)
        .build();
    }

    @Override
    public BankResponse debitAccount(CreditDebitRequest request) {
        boolean isAccountExists=userRepository.existsByAccountNumber(request.getAccountNumber());
        if(!isAccountExists){
            return BankResponse.builder()
            .accountInfo(null)
            .responseCode(AccountUtils.ACCOUNT_NOT_EXISTS_CODE)
            .responseMessage(AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE)
            .build();
        }
        BigDecimal amountToDebit=request.getAmount();
        User user=userRepository.findByAccountNumber(request.getAccountNumber());
        BigDecimal availableBalance=user.getAccountBalance();
        if(amountToDebit.compareTo(availableBalance)==1){
            return BankResponse.builder()
            .accountInfo(null)
            .responseCode(AccountUtils.ACCOUNT_NOT_EXISTS_CODE)
            .responseMessage("ammount cant be greater")
            .build();
        }

        user.setAccountBalance(availableBalance.add(amountToDebit.negate()));
        userRepository.save(user);

        TransactionDto transactionDto=TransactionDto.builder()
        .accountNumber(user.getAccountNumber())
        .amount(request.getAmount())
        .transactionType("debit")
        .build();

       transactionService.saveTransaction(transactionDto);
        return BankResponse.builder()
             .accountInfo(AccountInfo.builder()
            .accountBalance(user.getAccountBalance())
            .accountName(user.getFirstName())
            .accountNumber(user.getAccountNumber())
            .build())
        .responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
        .responseMessage("account debited successfully") // alternatively we can create a string in account utils
        .build();
    }

    @Override
    public BankResponse transferAmount(TransferRequest request) {
       boolean isFromAccountExists=userRepository.existsByAccountNumber(request.getFromAccountNumber());
       boolean isToAccountExists=userRepository.existsByAccountNumber(request.getToAccountNumber());
       if(!isFromAccountExists || !isToAccountExists){
        return BankResponse.builder()
        .responseCode(AccountUtils.ACCOUNT_NOT_EXISTS_CODE)
        .responseMessage(AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE)
        .accountInfo(null)
        .build();
       }
       User sender=userRepository.findByAccountNumber(request.getFromAccountNumber());
       User receiver=userRepository.findByAccountNumber(request.getToAccountNumber());
       BigDecimal amountToSend=request.getAmount();
       if(amountToSend.compareTo(sender.getAccountBalance())==1){
        return BankResponse.builder()
            .accountInfo(null)
            .responseCode(AccountUtils.ACCOUNT_NOT_EXISTS_CODE)
            .responseMessage("ammount cant be greater")
            .build();
       }
       sender.setAccountBalance(sender.getAccountBalance().subtract(amountToSend));
       receiver.setAccountBalance(receiver.getAccountBalance().add(amountToSend));
       userRepository.save(sender);
       userRepository.save(receiver);
       EmailDetails debitAlert=EmailDetails.builder()
       .recipient(sender.getEmail())
       .subject("Debit Alert")
       .messageBody("Amount debited : "+request.getAmount()+"\n"+"transfered to : "+sender.getFirstName())
       .build();
       emailService.sendEmail(debitAlert);
       // crediting in receiver
       TransactionDto transactionDto=TransactionDto.builder()
        .accountNumber(receiver.getAccountNumber())
        .amount(request.getAmount())
        .transactionType("credit")
        .build();
       transactionService.saveTransaction(transactionDto);
      // debit from sender
       TransactionDto transactionDtoSender=TransactionDto.builder()
        .accountNumber(sender.getAccountNumber())
        .amount(request.getAmount())
        .transactionType("debit")
        .build();

       transactionService.saveTransaction(transactionDtoSender);
       return BankResponse.builder()
       .accountInfo(AccountInfo.builder()
        .accountBalance(sender.getAccountBalance())
        .accountName(sender.getFirstName())
        .build())
       .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
       .responseMessage("Transfered Successfully")
       .build();
    }
    
    // balance enquiry , name enquiry , credit , debit , transfer
}
