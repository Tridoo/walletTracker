package com.tridoo.wallettracker.stock;


import android.util.Log;
import androidx.core.util.Pair;
import com.tridoo.wallettracker.common.TimeRange;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import java.util.Queue;

public class StockWorker implements Runnable {

    private final StockReader reader;
    private final StockController controller;
    private Queue<StockDto> result;

    public StockWorker(StockReader reader, StockController controller, Queue<StockDto> result) {
        this.reader = reader;
        this.controller = controller;
        this.result = result;
    }

    @Override
    public void run() {
        Pair<String, BigDecimal> crypto = controller.getNextCurrency();

        while (crypto != null) {
            Optional<BigDecimal> priceDay = reader.getCryptocurrencyTrack(crypto.first, TimeRange.DAY);
            Optional<BigDecimal> priceWeek = reader.getCryptocurrencyTrack(crypto.first, TimeRange.WEEK);
//            Optional<BigDecimal> priceMonth = reader.getCryptocurrenciesTrack(crypto.first, TimeRange.MONTH);
//            Optional<BigDecimal> priceYear = reader.getCryptocurrenciesTrack(crypto.first, TimeRange.YEAR);
            Log.i(Thread.currentThread().getName(), crypto.first + " " + priceDay + " " + priceWeek);

            Optional<BigDecimal> changeDay = countChange(crypto.second, priceDay);
            Optional<BigDecimal> changeWeek = countChange(crypto.second, priceWeek);
//            OptionalDouble changeMonth = countChange(crypto.second, priceMonth);
//            OptionalDouble changeYear = countChange(crypto.second, priceYear);

            StockDto dto = new StockDto(crypto.first, crypto.second, changeDay, changeWeek, Optional.empty(), Optional.empty());
            result.add(dto);
            crypto = controller.getNextCurrency();
        }

    }

    private Optional<BigDecimal> countChange(BigDecimal currentPrice, Optional<BigDecimal> price) {
        if (!price.isPresent()) {
            return Optional.empty();
        }
        return Optional.of(
                currentPrice.subtract(price.get())
                        .multiply(BigDecimal.valueOf(100))
                        .divide(price.get(), 2, RoundingMode.HALF_UP)
        );
    }
}
