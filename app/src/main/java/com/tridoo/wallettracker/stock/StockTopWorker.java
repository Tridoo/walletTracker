package com.tridoo.wallettracker.stock;

import androidx.core.util.Pair;

import java.math.BigDecimal;
import java.util.Deque;

public class StockTopWorker implements Runnable{

    private final StockReader reader;
    private Deque<Pair<String, BigDecimal>> result;

    public StockTopWorker(StockReader reader, Deque<Pair<String, BigDecimal>> result) {
        this.reader = reader;
        this.result = result;
    }

    @Override
    public void run() {
        result.addAll(reader.getTopCryptocurrencies());
    }
}
