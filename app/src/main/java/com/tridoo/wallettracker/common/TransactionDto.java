package com.tridoo.wallettracker.common;

import java.math.BigDecimal;

public class TransactionDto{
    private long time;
    private BigDecimal quantity;
    private BigDecimal value;

    public TransactionDto(long time, BigDecimal quantity, BigDecimal value) {
        this.time = time;
        this.quantity = quantity;
        this.value = value;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }
}
