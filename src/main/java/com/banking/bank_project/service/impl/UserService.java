package com.banking.bank_project.service.impl;

import com.banking.bank_project.dto.BankResponse;
import com.banking.bank_project.dto.UserRequest;

public interface UserService {
    
    BankResponse createAccount(UserRequest userRequest);
}
