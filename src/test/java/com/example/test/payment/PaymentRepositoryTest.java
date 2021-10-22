package com.example.test.payment;

import com.example.test.customer.Customer;
import com.example.test.customer.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PaymentRepositoryTest {

    @Autowired
    PaymentRepository underTest;

    @Autowired
    CustomerRepository customerRepository;

    @Test
    void itShouldSavePayment() {
        //Given
        Customer customer = new Customer();
        customer.setPhoneNumber("18710251730");
        customer.setName("Tyler");
        customerRepository.save(customer);

        Payment payment = new Payment();
        payment.setAmount(new BigDecimal(100));
        payment.setCurrency(Currency.RMB);
        payment.setSource("debit card");
        payment.setCustomer(customer);

        //When
        underTest.save(payment);
        //Then
        Optional<Payment> paymentOptional = underTest.findById(payment.getId());
        assertThat(paymentOptional).containsSame(payment);

        System.out.println(payment);
    }

    @Test
    void itShouldFindPaymentAndFetchCustomer() {
        //Given
        Customer customer = new Customer();
        customer.setPhoneNumber("18710251730");
        customer.setName("Tyler");
        customerRepository.save(customer);

        Payment payment = new Payment();
        payment.setAmount(new BigDecimal(100));
        payment.setCurrency(Currency.RMB);
        payment.setSource("debit card");
        payment.setCustomer(customer);
        underTest.save(payment);

        //When
        Optional<Payment> paymentOptional = underTest.findById(payment.getId());

        //Then
        assertThat(paymentOptional)
                .isPresent()
                .contains(payment);
        Customer customer2 = paymentOptional.get().getCustomer();

        assertThat(customer2).isEqualTo(customer);
    }
}