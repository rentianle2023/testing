package com.example.test.payment;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PaymentCharger {

    public PaymentCharge charge(String source, BigDecimal amount, Currency currency){
        //TODO charge the source of Payment
        PaymentCharge paymentCharge = new PaymentCharge();
        paymentCharge.setDebited(true);
        return paymentCharge;
    }
}
