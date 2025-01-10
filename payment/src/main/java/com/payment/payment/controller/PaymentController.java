package com.payment.payment.controller;

import com.payment.payment.dtos.NameLookUpRequest;
import com.payment.payment.service.PaymentService;
import com.payment.payment.dtos.BillsPaymentRequest;
import com.payment.payment.dtos.PaymentRequest;
import com.payment.payment.dtos.TransferRequest;
import com.payment.shared.dtos.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/banks")
    public ResponseEntity<?> getBanks() {
        ResponseDto<?> resp = paymentService.getBanks();
        return new ResponseEntity<>(resp, resp.getHttpStatus());
    }

    @GetMapping("/billers")
    public ResponseEntity<?> getBillers() {
        ResponseDto<?> resp = paymentService.getBillers();
        return new ResponseEntity<>(resp, resp.getHttpStatus());
    }

    @GetMapping("/billers/{category}")
    public ResponseEntity<?> getBillers(@PathVariable String category) {
        ResponseDto<?> resp = paymentService.getProductsForBiller(category);
        return new ResponseEntity<>(resp, resp.getHttpStatus());
    }

    @PostMapping("/name-lookup")
    public ResponseEntity<?> nameLookUp(@RequestBody NameLookUpRequest nameLookUpRequest) {
        ResponseDto<?> resp = paymentService.accountNameLookUp(nameLookUpRequest);
        return new ResponseEntity<>(resp, resp.getHttpStatus());
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> makeTransfer(@RequestBody TransferRequest transferRequest) {
        ResponseDto<?> resp = paymentService.transfer(transferRequest);
        return new ResponseEntity<>(resp, resp.getHttpStatus());
    }

    @PostMapping("/bills-payment")
    public ResponseEntity<?> payBill(@RequestBody BillsPaymentRequest request) {
        ResponseDto<?> resp = paymentService.processBillPayment(request);
        return new ResponseEntity<>(resp, resp.getHttpStatus());
    }
}
