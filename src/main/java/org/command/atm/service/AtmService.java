package org.command.atm.service;

import org.command.atm.model.*;
import org.command.atm.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
public class AtmService {

    private final CustomerRepository customerRepository;

    public AtmService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer login(String name) {
        setAllInactive();
        Customer customer = getCustomer(name);
        if (customer == null) {
            customer = Customer.createCustomer();
            customer.setName(name);
            customer.setBalance(0.0);
            insert(customer);
        }
        customer.setActive(true);
        update(customer);
        return customer;
    }

    public void logout(Customer customer) {
        customer.setActive(false);
        customer.setDebits(new HashMap<>());
        update(customer);
    }

    private void setAllInactive() {
        customerRepository.setAllInactive();
    }

    private void update(Customer customer) {
        customerRepository.update(customer);
    }

    private void insert(Customer customer) {
        customerRepository.insert(customer);
    }

    public Customer deposit(Double amount) {
        Customer customer = getActiveCustomer();

        customer.setBalance(customer.getBalance() + amount);
        update(customer);

        List<Owed> owedList = customer.getOweds().values().stream()
                .filter(owed -> !owed.isRemedy() && owed.getOwedType()==OwedType.TO)
                .toList();

        if (!owedList.isEmpty()) {
            return transferByOwedList(customer, owedList);
        }

        return customer;
    }

    public Customer getActiveCustomer() {
        return customerRepository.getActive();
    }

    public Customer getCustomer(String name) {
        return customerRepository.getCustomerByName(name);
    }

    private Customer transferByOwedList(Customer customer, List<Owed> owedList) {
        transferList(customer, owedList);
        return getActiveCustomer();
    }

    private void transferList(Customer customer, List<Owed> oweds) {
        oweds.forEach(owed -> {
            Customer sourceCustomer = getCustomer(customer.getName());
            Customer targetCustomer = getCustomer(owed.getName());
            transfer(sourceCustomer, targetCustomer, owed.getAmount(), owed);
        });
    }

    public void transfer(Customer sourceCustomer, Customer targetCustomer, Double amount, Owed owed) {

        sourceCustomer.setDebits(new HashMap<>());
        String id = UUID.randomUUID().toString();

        List<Owed> owedListFrom = sourceCustomer.getOweds().values().stream()
                .filter(owedFrom -> !owedFrom.isRemedy() && owedFrom.getOwedType()==OwedType.FROM
                && owedFrom.getName().equals(targetCustomer.getName())) .toList();

        if (!owedListFrom.isEmpty()) {
            payDebtOwed(sourceCustomer, targetCustomer, amount, owedListFrom, id);
            return;
        }

        double actualTransfer = amount;
        double actualBalance = sourceCustomer.getBalance() - amount;

        if (sourceCustomer.getBalance()<amount) {
            actualTransfer = sourceCustomer.getBalance();
            double owedAmount = amount - sourceCustomer.getBalance();
            putOwedToCustomer(id, sourceCustomer, targetCustomer, owedAmount, OwedType.TO);
            putOwedToCustomer(id, targetCustomer, sourceCustomer, owedAmount, OwedType.FROM);
            actualBalance = 0.0;
        }

        double targetBalance = targetCustomer.getBalance() + actualTransfer;

        if (owed!=null) {
            remedyPreviousOwed(sourceCustomer, targetCustomer, owed.getId());
            if (owed.getOwedType().equals(OwedType.FROM)) {
                targetBalance = targetCustomer.getBalance();
            }
        }

        sourceCustomer.getDebits().put(id, createDebit(id, targetCustomer.getName(),
                actualTransfer, owed!=null ? DebitType.TRANSFER_OWED : DebitType.TRANSFER));

        sourceCustomer.setBalance(actualBalance);
        update(sourceCustomer);
        targetCustomer.setBalance(targetBalance);
        update(targetCustomer);
    }

    private void payDebtOwed(Customer customer, Customer targetCustomer, Double amount,
                             List<Owed> owedList, String id) {

        for (Owed owedFrom : owedList) {
            if (amount<=0) break;

            double owedAmount = getOwedAmount(owedFrom, amount);
            amount = getAmount(owedFrom, amount, owedAmount);

            putOwedToCustomer(id, customer, targetCustomer, owedAmount, OwedType.FROM);
            putOwedToCustomer(id, targetCustomer, customer, owedAmount, OwedType.TO);

            remedyPreviousOwed(customer, targetCustomer, owedFrom.getId());
        }

        targetCustomer.setBalance(targetCustomer.getBalance() + amount);
        update(targetCustomer);
    }

    private void remedyPreviousOwed(Customer customer, Customer targetCustomer, String key) {
        customer.getOweds().get(key).setRemedy(true);
        targetCustomer.getOweds().get(key).setRemedy(true);
    }

    private void putOwedToCustomer(String key, Customer customer, Customer targetCustomer,
                                   Double amount, OwedType owedType) {
        Owed owed = Owed.createOwed();
        owed.setId(key);
        owed.setName(targetCustomer.getName());
        owed.setOwedType(owedType);
        owed.setAmount(amount);
        owed.setRemedy(false);
        customer.getOweds().put(key, owed);
    }

    private static Debit createDebit(String id, String name, Double amount, DebitType debitType) {
        Debit debit = Debit.createDebit();
        debit.setId(id);
        debit.setAmount(amount);
        debit.setName(name);
        debit.setDebitType(debitType);
        return debit;
    }

    public double getOwedAmount(Owed owed, double amount) {
        if (owed.getAmount() >= amount) {
            return owed.getAmount() - amount;
        } else {
            return owed.getAmount();
        }
    }

    public double getAmount(Owed owed, double amount, double owedAmount) {
        if (owed.getAmount() >= amount) {
            return 0.0;
        } else {
            return amount - owedAmount;
        }
    }

}
