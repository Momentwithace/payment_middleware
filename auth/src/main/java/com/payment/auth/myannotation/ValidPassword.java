package com.payment.auth.myannotation;

import com.payment.auth.myannotation.validators.PasswordValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Target({FIELD})
@Constraint(validatedBy = PasswordValidator.class)
@Retention(RUNTIME)
public @interface ValidPassword {

    String message() default "password not valid";

//    Class<?>[] groups() default {};
//
//    Class<? extends Payload>[] payload() default {};

    boolean nullable() default false;
}