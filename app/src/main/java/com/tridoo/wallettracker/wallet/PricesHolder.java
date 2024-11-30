package com.tridoo.wallettracker.wallet;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class PricesHolder {
    private static final PricesHolder holder = new PricesHolder();
    private final Map<String, BigDecimal> prices = new HashMap<>();

    private PricesHolder() {
    }

    public static PricesHolder getInstance() {
        return holder;
    }

    public BigDecimal getPrice(String id) {
        return prices.get(id);
    }

    public void addPrice(String id, BigDecimal price) {
        prices.put(id, price);
    }
}
