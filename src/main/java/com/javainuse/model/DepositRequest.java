package com.javainuse.model;

import java.io.Serializable;

public class DepositRequest implements Serializable {
    private Double amount;

    public DepositRequest () {

    }

    public DepositRequest(Double amount) {
        this.amount = amount;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
