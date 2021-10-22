package com.example.test.payment;

import com.example.test.customer.Customer;
import com.example.test.customer.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PaymentServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PaymentCharger paymentCharger;

    @Mock
    private PaymentRepository paymentRepository;

    private PaymentService underTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        underTest = new PaymentService(paymentRepository,customerRepository,paymentCharger);
    }

    @Test
    void itShouldSavePaymentWithValidCustomerAndDebitStatus() {
        //Given
        Payment payment = new Payment(null,
                null,
                new BigDecimal(100),
                "debitCard",
                Currency.RMB);

        given(customerRepository.findById(1)).willReturn(Optional.of(mock(Customer.class)));
        given(paymentCharger.charge(payment.getSource(),payment.getAmount(),payment.getCurrency())).willReturn(new PaymentCharge(true));

        //When
        underTest.makePayment(payment,1);
        //Then
        then(paymentRepository).should().save(payment);
    }

    @Test
    void itShouldThrowExceptionWhenCustomerNotFind() {
        //Given
        Payment payment = new Payment(null,
                null,
                new BigDecimal(100),
                "debitCard",
                Currency.RMB);

        given(customerRepository.findById(1)).willReturn(Optional.empty());
        given(paymentCharger.charge(payment.getSource(), payment.getAmount(), payment.getCurrency()))
                .willReturn(new PaymentCharge(true));

        Integer customerId = 1;
        //When
        //Then
        assertThatThrownBy(() -> underTest.makePayment(payment,customerId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(String.format("customer with id=[%s] not exists",customerId));
    }

    @Test
    void itShouldThrowExceptionWhenCurrencyIsNotSupported() {
        //Given
        Currency currency = Currency.EUR;
        Integer customerId = 1;

        Payment payment = new Payment(null,
                null,
                new BigDecimal(100),
                "debitCard",
                currency);

        given(customerRepository.findById(1)).willReturn(Optional.of(mock(Customer.class)));
        given(paymentCharger.charge(payment.getSource(),payment.getAmount(),payment.getCurrency()))
                .willReturn(new PaymentCharge(true));

        //When
        //Then
        assertThatThrownBy(() -> underTest.makePayment(payment,customerId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(String.format("currency with [%s] is not supported right now",currency));
    }

    @Test
    void itShouldThrowExceptionWhenChargeFailed() {
        //Given
        Payment payment = new Payment(null,
                null,
                new BigDecimal(100),
                "debitCard",
                Currency.RMB);

        given(customerRepository.findById(1)).willReturn(Optional.of(mock(Customer.class)));
        given(paymentCharger.charge(payment.getSource(),payment.getAmount(),payment.getCurrency()))
                .willReturn(new PaymentCharge(false));

        Integer customerId = 1;
        //When
        //Then
        assertThatThrownBy(() -> underTest.makePayment(payment,customerId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("charge failed for customer with id=[%s]",customerId));
    }
}