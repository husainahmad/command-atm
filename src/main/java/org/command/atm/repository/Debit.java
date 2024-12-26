package org.command.atm.repository;

public class Debit {

    private String name;
    private Double owed;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getOwed() {
        return owed;
    }

    public void setOwed(Double owed) {
        this.owed = owed;
    }
}
