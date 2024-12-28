package org.command.atm.repository.model;

public class Owed {

    private String id;
    private String name;
    private Double amount;
    private boolean remedy;
    private OwedType owedType;

    private Owed() {
    }

    public static Owed createOwed() {
        return new Owed();
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

    public boolean isRemedy() {
        return remedy;
    }

    public void setRemedy(boolean remedy) {
        this.remedy = remedy;
    }

    public OwedType getOwedType() {
        return owedType;
    }

    public void setOwedType(OwedType owedType) {
        this.owedType = owedType;
    }

}
