package com.payment.auth.dtos.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BaseResponse<T> {
    private boolean isSuccessful;
    private String message;
    private T data;
    private int statusCode;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp = LocalDateTime.now();

    public static Object ok(Object object) {

        return BaseResponse.builder()
                .data(object)
                .statusCode(200)
                .isSuccessful(true)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static BaseResponse<String> ok(String object) {

        return BaseResponse.<String>builder()
                .data(object)
                .statusCode(200)
                .isSuccessful(true)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static BaseResponse<Long> ok(Long data) {
        return BaseResponse.<Long>builder()
                .message("Successful")
                .data(data)
                .statusCode(200)
                .isSuccessful(true)
                .timestamp(LocalDateTime.now())
                .build();
    }
}