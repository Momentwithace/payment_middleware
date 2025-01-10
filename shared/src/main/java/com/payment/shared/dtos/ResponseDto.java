package com.payment.shared.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Setter
@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class ResponseDto<T> {

    private String respCode;
    private String respDescription;
    private T respBody;
    private HttpStatus httpStatus;

    public ResponseDto(String description, String code, HttpStatus status) {
        respCode = code;
        httpStatus = status;
        respDescription = description;
    }
}
