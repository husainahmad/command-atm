package org.command.atm.repository;

import java.util.ArrayList;
import java.util.List;

public class Customer {

    private String name;
    private boolean active;
    private Double balance;

    private List<Debit> debits = new ArrayList<>();
    private List<Credit> credits = new ArrayList<>();

    private Customer() {
    }

    public static Customer createCustomer() {
        return new Customer();
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Debit> getDebits() {
        return debits;
    }

    public void setDebits(List<Debit> debits) {
        this.debits = debits;
    }

    public List<Credit> getCredits() {
        return credits;
    }

    public void setCredits(List<Credit> credits) {
        this.credits = credits;
    }
}
