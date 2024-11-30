package com.tridoo.wallettracker.wallet;

import java.io.Serializable;
import java.math.BigDecimal;

public class WalletEntry implements Serializable {
    private String currencyId;
    private BigDecimal quantity;
    private BigDecimal avgPrice;

    public WalletEntry(String currencyId, BigDecimal quantity, BigDecimal avgPrice) {
        this.currencyId = currencyId;
        this.quantity = quantity;
        this.avgPrice = avgPrice;
    }

    public String getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(String currencyId) {
        this.currencyId = currencyId;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getAvgPrice() {
        return avgPrice;
    }

    public void setAvgPrice(BigDecimal avgPrice) {
        this.avgPrice = avgPrice;
    }
}
