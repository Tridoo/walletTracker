package com.tridoo.wallettracker.stock;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

public class StockDto {
    private final String currencyId;
    private final String price;
    private final long roundedPrice;
    private final String dayChange;
    private final String weekChange;
    private final String monthChange;
    private final String yearChange;


    public StockDto(String currencyId, BigDecimal price, Optional<BigDecimal> dayChange, Optional<BigDecimal> weekChange, Optional<BigDecimal> monthChange, Optional<BigDecimal> yearChange) {
        this.currencyId = currencyId;
        this.price = price.setScale(2,RoundingMode.HALF_UP).toString();
        this.roundedPrice = price.longValue();
        this.dayChange = convertOptional(dayChange);
        this.weekChange = convertOptional(weekChange);
        this.monthChange = convertOptional(monthChange);
        this.yearChange = convertOptional(yearChange);
    }

    public String getCurrencyId() {
        return currencyId;
    }

    public String getPrice() {
        return price;
    }

    public long getRoundedPrice() {
        return roundedPrice;
    }

    public String getDayChange() {
        return dayChange;
    }

    public String getWeekChange() {
        return weekChange;
    }

    public String getMonthChange() {
        return monthChange;
    }

    public String getYearChange() {
        return yearChange;
    }

    private String convertOptional(Optional<BigDecimal> opt) {
        return opt.map(bigDecimal -> String.valueOf(bigDecimal.setScale(2, RoundingMode.HALF_UP))).orElse("-");
    }


}
