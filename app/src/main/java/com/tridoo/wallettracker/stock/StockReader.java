package com.tridoo.wallettracker.stock;

import androidx.core.util.Pair;
import com.tridoo.wallettracker.common.TimeRange;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface StockReader {
    List<Pair<String, BigDecimal>> getTopCryptocurrencies();

    Optional<BigDecimal> getCryptocurrencyTrack(String currencyId, TimeRange timeRange);
}
