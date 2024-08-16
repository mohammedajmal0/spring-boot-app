package com.banking.bank_project.utils;

import java.time.LocalDateTime;
import java.util.Random;

public class AccountUtils {
    /**
     * accnumber: currentYear+currentTime+random6
     */
    public static String generateAccountNumber() {
        // Get last 2 digits of the current year
        String currentYear = String.valueOf(LocalDateTime.now().getYear()).substring(2);

        // Get the last 3 digits of current time in milliseconds
        String currentTime = String.valueOf(System.currentTimeMillis());
        String last3DigitsOfTime = currentTime.substring(currentTime.length() - 3);

        // Generate a random 6-digit number
        Random random = new Random();
        int random6 = 100000 + random.nextInt(900000);  // Ensures a 6-digit number

        // Combine all parts to form the account number
        System.out.println("random account number :"+currentYear+last3DigitsOfTime+random6);

        String accountNumber=currentYear + last3DigitsOfTime + random6;
        return accountNumber;
    }

    public static final String ACCOUNT_EXISTS_CODE="001";
    public static final String ACCOUNT_EXISTS_MESSAGE="This user already has an account";
    public static final String ACCOUNT_CREATED_CODE="002";
    public static final String ACCOUNT_CREATED_MESSAGE="account created!!";
    public static final String ACCOUNT_NOT_EXISTS_CODE="003";
    public static final String ACCOUNT_NOT_EXISTS_MESSAGE="account doesnt exists";
    public static final String ACCOUNT_FOUND_CODE="004";
    public static final String ACCOUNT_FOUND_MESSAGE="account found!";
}
