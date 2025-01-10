package com.payment.auth.message;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorMessage {
    public static final String EMAIL_ALREADY_IN_USE = "Email already in use";
    public static final String BVN_ALREADY_EXISTS = "Bvn already exists";
    public static final String NIN_NOT_EXISTS = "Nin not exists";
    public static final String ACCOUNT_SERVICE_UNAVAILABLE = "Account service unavailable";
    public static final String NIN_ALREADY_EXISTS = "Nin already exists";
    public static final String  INVALID_PASSWORD_FORMAT ="Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one number, and one special character";
    public static final String  INVALID_BVN_NIN_FORMAT = "Invalid BVN or NIN format. Both must be exactly 11 digits.";


}
