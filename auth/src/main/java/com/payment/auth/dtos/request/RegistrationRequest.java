package com.payment.auth.dtos.request;

import com.payment.auth.myannotation.ValidEmailAddress;
import com.payment.auth.myannotation.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class RegistrationRequest {
    @NotBlank(message = "{email.not_blank}")
    @ValidEmailAddress
    private String email;
    @ValidPassword(message = "{password.not.strong}")
    private String password;
    @Size(max = 20, min = 2, message = "{firstname.accepted_length}")
    @NotBlank(message = "{firstname.not_blank}")
    private String firstName;
    @Size(max = 20, min = 2, message = "{lastname.accepted_length}")
    @NotBlank(message = "{lastname.not_blank}")
    private String lastName;
    @NotBlank(message = "BVN is required")
    private String bvn;
    @NotBlank(message = "NIN is required")
    private String nin;
    @NotBlank
    @Size(min = 4, max = 4)
    private String pin;

    public boolean isValid() {
        if (bvn == null || nin == null) {
            return false;
        }

        return bvn.matches("^[0-9]{11}$") &&
                nin.matches("^[0-9]{11}$");
    }

    public boolean validatePassword(String password) {
        if (password == null) {
            return false;
        }

        return password.length() >= 8 &&
                password.matches(".*[A-Z].*") &&
                password.matches(".*[a-z].*") &&
                password.matches(".*\\d.*") &&
                password.matches(".*[!@#$%^&*(),.?\":{}|<>].*");
    }
}
