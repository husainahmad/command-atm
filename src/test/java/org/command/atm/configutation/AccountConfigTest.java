package org.command.atm.configutation;

import org.command.atm.configuration.AccountConfig;
import org.command.atm.repository.CustomerRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@ExtendWith(MockitoExtension.class)
class AccountConfigTest {

    @Test
    void testCustomerRepository() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AccountConfig.class);
        CustomerRepository customerRepository = context.getBean(CustomerRepository.class);
        Assertions.assertNotNull(customerRepository);
    }
}
