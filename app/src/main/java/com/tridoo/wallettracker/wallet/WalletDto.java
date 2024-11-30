package com.tridoo.wallettracker.wallet;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

public class WalletDto {

    private static final String MISSING = "0";
    private final String currencyId;

    private final String avgPrice;
    private final BigDecimal price;

    private final String change;

    private final BigDecimal profit;


    public WalletDto(String currencyId, BigDecimal quantity, BigDecimal avgPrice, Optional<BigDecimal> price) {
        this.currencyId = currencyId;
        this.avgPrice = format(avgPrice);
        if (price.isPresent()) {
            this.price = price.get();
            this.change = price.get().subtract(avgPrice)
                    .divide(avgPrice, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal(100))
                    .setScale(1, RoundingMode.HALF_UP)
                    .toString();
            this.profit = price.get().subtract(avgPrice).multiply(quantity).setScale(0, RoundingMode.HALF_UP);
        } else {
            this.price = BigDecimal.ZERO;
            this.profit = BigDecimal.ZERO;
            this.change = MISSING;
        }
    }

    public String getAvgPrice() {
        return avgPrice;
    }

    public String getChange() {
        return change;
    }

    public BigDecimal getProfit() {
        return profit;
    }

    public String getCurrencyId() {
        return currencyId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getFormattedPrice() {
        return format(price);
    }

    private String format(BigDecimal bd) {
        if (bd.longValue() >= 1000) return bd.setScale(0, RoundingMode.HALF_UP).toString();
        if (bd.longValue() >= 100) return bd.setScale(1, RoundingMode.HALF_UP).toString();
        if (bd.longValue() >= 1) return bd.setScale(3, RoundingMode.HALF_UP).toString();

        return bd.setScale(6, RoundingMode.HALF_UP).toString();
    }
}
