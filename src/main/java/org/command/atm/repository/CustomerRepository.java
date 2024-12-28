package org.command.atm.repository;

import org.command.atm.model.Customer;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Repository
public class CustomerRepository {

    private final Map<String, Customer> customerMap = new HashMap<>();

    public Customer getCustomerByName(String name) {
        return customerMap.get(name);
    }

    public void insert(Customer customer) {
        customerMap.put(customer.getName(), customer);
    }

    public void setAllInactive() {
        customerMap.forEach((s, customer) -> customer.setActive(false));
    }

    public void update(Customer customer) {
        customerMap.put(customer.getName(), customer);
    }

    public Customer getActive() {
        AtomicReference<Customer> activeCustomer = new AtomicReference<>();

        customerMap.forEach((s, customer) -> {
            if (customer.isActive()) {
                activeCustomer.set(customer);
            }
        });

        return activeCustomer.get();
    }

}
