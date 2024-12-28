package org.command.atm.configuration;

import org.command.atm.repository.CustomerRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AccountConfig {

    @Bean
    CustomerRepository customerRepository() {
        return new CustomerRepository();
    }
}
