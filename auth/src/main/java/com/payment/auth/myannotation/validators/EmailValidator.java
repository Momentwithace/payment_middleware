package com.payment.auth.myannotation.validators;


import com.payment.auth.myannotation.ValidEmailAddress;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.mail.internet.InternetAddress;


public class EmailValidator implements ConstraintValidator<ValidEmailAddress, String> {

    private ValidEmailAddress validEmailAddress;

    @Override
    public void initialize(ValidEmailAddress constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        validEmailAddress = constraintAnnotation;
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (validEmailAddress.nullable() && s == null)
            return true;
        return isValidEmailAddress(s);
    }

    public static boolean isValidEmailAddress(String email) {
        boolean isValid = false;
        try {
            InternetAddress emailAddress = new InternetAddress(email);
            emailAddress.validate();

            // check that username in email  string contains at least on letter
            String emailPart0 = email.split("@")[0];
            isValid = emailPart0.matches(".*[a-zA-Z]+.*");
        } catch (Exception ignored) {
        }

        return isValid;
    }
}
