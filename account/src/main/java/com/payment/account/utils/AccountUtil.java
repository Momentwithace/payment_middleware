package com.payment.account.utils;

import com.payment.shared.dtos.AccountType;

import java.security.SecureRandom;

public class AccountUtil {

    public static String generateAccountNumber(AccountType accountType) {


        return "0"+getAccountNumber(accountType);

    }

    private static String getAccountNumber(AccountType accountType) {
        String PREFIX;
        SecureRandom rnd = new SecureRandom();
        //savings
        return switch (accountType) {
            case FIXED_DEPOSIT -> {
                PREFIX = "999";
                yield PREFIX + String.format("%04d", rnd.nextInt(9999)) + "99";
            }
            case LOAN -> {
                PREFIX = "9";
                yield PREFIX + String.format("%02d", rnd.nextInt(99)) + "999999";
            }
            case CURRENT -> {
                PREFIX = "8";
                yield PREFIX + String.format("%08d", rnd.nextInt(99999999));
            }

            //savings
            default -> {
                PREFIX = "0";
                yield PREFIX + String.format("%08d", rnd.nextInt(99999999));
            }
        };
    }


}
