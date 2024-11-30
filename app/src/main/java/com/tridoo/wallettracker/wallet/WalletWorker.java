package com.tridoo.wallettracker.wallet;


import android.util.Log;
import com.tridoo.wallettracker.stock.StockReader;
import com.tridoo.wallettracker.common.TimeRange;

import java.math.BigDecimal;
import java.util.*;

public class WalletWorker implements Runnable {

    private final StockReader reader;
    private final Set<String> cryptoIds;
    private Map<String, BigDecimal> result;

    public WalletWorker(StockReader reader, Set<String> cryptoIds, Map<String, BigDecimal> result) {
        this.reader = reader;
        this.cryptoIds = cryptoIds;
        this.result = result;
    }

    @Override
    public void run() {
        for (String id : cryptoIds) {
            Optional<BigDecimal> price = reader.getCryptocurrencyTrack(id, TimeRange.NOW);
            if (price.isPresent()) {
                Log.i(Thread.currentThread().getName(), id + " " + price.get());
                result.put(id, price.get());
            } else {
                Log.i(Thread.currentThread().getName(), id + " brak ceny");
            }
        }
    }
}
