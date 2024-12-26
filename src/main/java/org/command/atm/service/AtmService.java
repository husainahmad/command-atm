package org.command.atm.service;

import org.command.atm.repository.*;
import org.springframework.stereotype.Service;

@Service
public class AtmService {

    private final CustomerRepository customerRepository;

    public AtmService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer login(String name) {
        customerRepository.setAllInactive();
        Customer customer = customerRepository.getCustomerByName(name);
        if (customer == null) {
            customer = Customer.createCustomer();
            customer.setName(name);
            customer.setBalance(0.0);
            customerRepository.insert(customer);
        }
        customer.setActive(true);
        customerRepository.update(customer);
        return customer;
    }

    public void logout(Customer customer) {
        customer.setActive(false);
        customerRepository.update(customer);
    }

    public Customer deposit(Double amount) {
        Customer customer = customerRepository.getActive();
        Credit credit = extractedCredit(amount, CreditType.DEPOSIT, null);
        customer.getCredits().add(credit);
        customer.setBalance(customer.getBalance() + amount);
        customerRepository.update(customer);
        return customer;
    }

    public Customer getActiveCustomer() {
        return customerRepository.getActive();
    }

    public Customer getCustomer(String name) {
        return customerRepository.getCustomerByName(name);
    }

    public void transfer(Customer customer, Customer targetCustomer, Double amount) {

        double actualBalance = customer.getBalance() - amount;
        Credit credit = null;
        Debit debit = extractedDebit(targetCustomer, amount - customer.getBalance());
        CreditType creditType = CreditType.TRANSFER;

        if (customer.getBalance()<amount) {
            debit = extractedDebit(targetCustomer, amount - customer.getBalance());
            creditType = CreditType.OWED;
            actualBalance = 0.0;
        }

        customer.getDebits().add(debit);
        customer.setBalance(actualBalance);
        customerRepository.update(customer);

        credit = extractedCredit(amount, creditType, customer);
        targetCustomer.setBalance(targetCustomer.getBalance() + amount);
        targetCustomer.getCredits().add(credit);

        customerRepository.update(targetCustomer);
    }

    private Credit extractedCredit(Double amount, CreditType creditType, Customer customer) {
        Credit credit = Credit.createCredit();
        credit.setCreditType(creditType);
        credit.setFromName(customer==null ? null : customer.getName());
        credit.setProcessed(creditType.equals(CreditType.DEPOSIT));
        credit.setAmount(amount);
        return credit;
    }

    private Debit extractedDebit(Customer targetCustomer, Double owed) {
        Debit debit = new Debit();
        debit.setName(targetCustomer.getName());
        debit.setOwed(owed);
        return debit;
    }

}
