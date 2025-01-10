package com.payment.payment.service;

import com.payment.payment.dtos.NameLookUpRequest;
import com.payment.payment.dtos.BillsPaymentRequest;
import com.payment.payment.dtos.TransferRequest;
import com.payment.shared.dtos.ResponseDto;

public interface PaymentService {
    ResponseDto<?> transfer(TransferRequest transferRequest);

    ResponseDto<?> getBanks();

    ResponseDto<?> accountNameLookUp(NameLookUpRequest nameLookUpRequest);

    ResponseDto<?> getBillers();

    ResponseDto<?> getProductsForBiller(String biller);

    ResponseDto<?> processBillPayment(BillsPaymentRequest request);
}
