package org.command.atm.model;

public class Debit {

    private String id;
    private String name;
    private Double amount;
    private DebitType debitType;

    private Debit() {
    }

    public static Debit createDebit() {
        return new Debit();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public DebitType getDebitType() {
        return debitType;
    }

    public void setDebitType(DebitType debitType) {
        this.debitType = debitType;
    }

}
