package com.payment.account.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CustomerDto {
    @NotBlank(message = "BVN is required")
    @Size(min = 11, max = 11, message = "BVN must be exactly 11 characters long")
    private String bvn;

    @NotBlank(message = "NIN is required")
    @Size(min = 11, max = 11, message = "NIN must be exactly 11 characters long")
    private String nin;

    public String getNin() {
        return nin;
    }

    public void setNin(String nin) {
        this.nin = nin;
    }

    public String getBvn() {
        return bvn;
    }

    public void setBvn(String bvn) {
        this.bvn = bvn;
    }
}
