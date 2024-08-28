package com.banking.bank_project.service.impl;

import com.banking.bank_project.dto.BankResponse;
import com.banking.bank_project.dto.CreditDebitRequest;
import com.banking.bank_project.dto.EnquiryRequest;
import com.banking.bank_project.dto.TransferRequest;
import com.banking.bank_project.dto.UserRequest;

public interface UserService {
    
    BankResponse createAccount(UserRequest userRequest);

    BankResponse balanceEnquiry (EnquiryRequest request);

    String nameEnquiry (EnquiryRequest request);

    BankResponse creditAccount(CreditDebitRequest request);

    BankResponse debitAccount(CreditDebitRequest request);

    BankResponse transferAmount (TransferRequest request);
}
