package org.command.atm.repository.model;

import java.util.HashMap;
import java.util.Map;

public class Customer {

    private String name;
    private boolean active;

    private Double balance;

    private Map<String, Debit> debits = new HashMap<>();
    private Map<String, Owed> oweds = new HashMap<>();

    private Customer() {
    }

    public static Customer createCustomer() {
        return new Customer();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Map<String, Debit> getDebits() {
        return debits;
    }

    public void setDebits(Map<String, Debit> debits) {
        this.debits = debits;
    }

    public Map<String, Owed> getOweds() {
        return oweds;
    }

    public void setOweds(Map<String, Owed> oweds) {
        this.oweds = oweds;
    }

}
