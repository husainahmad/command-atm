package org.command.atm.repository;

public class Credit {
    private String fromName;
    private CreditType creditType;
    private Double amount;
    private boolean processed;

    private Credit() {
    }

    public static Credit createCredit() {
        return new Credit();
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public CreditType getCreditType() {
        return creditType;
    }

    public void setCreditType(CreditType creditType) {
        this.creditType = creditType;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

}
