package com.example.test.payment;

import com.example.test.customer.Customer;
import com.example.test.customer.CustomerRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final CustomerRepository customerRepository;
    private final PaymentCharger paymentCharger;

    private final List<Currency> availableCurrency = List.of(Currency.RMB,Currency.USD);

    @Autowired
    public PaymentService(PaymentRepository paymentRepository, CustomerRepository customerRepository, PaymentCharger paymentCharger) {
        this.paymentRepository = paymentRepository;
        this.customerRepository = customerRepository;
        this.paymentCharger = paymentCharger;
    }

    public void makePayment(Payment payment, Integer customerId){
        //1. check customer
        Optional<Customer> customerOptional = customerRepository.findById(customerId);
        if(customerOptional.isEmpty()){
            throw new IllegalArgumentException(String.format("customer with id=[%s] not exists",customerId));
        }

        //2. check currency
        Currency currency = payment.getCurrency();
        if(!availableCurrency.contains(currency)){
            throw new IllegalArgumentException(String.format("currency with [%s] is not supported right now",currency));
        }

        //3. charge card
        String debitSource = payment.getSource();
        BigDecimal debitAmount = payment.getAmount();
        PaymentCharge charge = paymentCharger.charge(debitSource, debitAmount,currency);

        //4. if debited?
        if(!charge.isDebited()){
            throw new IllegalStateException(String.format("charge failed for customer with id=[%s]",customerId));
        }

        //5. save payment
        Customer customer = customerOptional.get();
        payment.setCustomer(customer);
        paymentRepository.save(payment);
    }
}
