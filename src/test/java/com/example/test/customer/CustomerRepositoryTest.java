package com.example.test.customer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CustomerRepositoryTest {

    @Autowired
    CustomerRepository underTest;

    @Test
    void itShouldSaveCustomer() {
        //Given
        Customer customer = new Customer();
        customer.setName("Tyler");
        customer.setPhoneNumber("18710251730");
        //When
        underTest.save(customer);
        //Then
        Optional<Customer> customerOptional = underTest.findById(customer.getId());
        assertThat(customerOptional)
                .containsSame(customer);
    }


}