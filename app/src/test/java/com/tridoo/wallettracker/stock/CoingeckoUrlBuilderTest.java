package com.tridoo.wallettracker.stock;


import com.tridoo.wallettracker.common.TimeRange;
import com.tridoo.wallettracker.stock.CoingeckoUrlBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;

@ExtendWith(MockitoExtension.class)
public class CoingeckoUrlBuilderTest {

    @Test
    public void testGetTopCryptocurrenciesUrl() {
        URL response;
        try {
            response = CoingeckoUrlBuilder.getTopCryptocurrenciesUrl();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertEquals("api.coingecko.com",response.getHost());
        Assertions.assertEquals("/api/v3/coins/markets",response.getPath());
    }

    @Test
    public void testCreateUrl() {
        URL response;
        try {
            response = CoingeckoUrlBuilder.createUrl("BTC", TimeRange.DAY);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        long yesterday = LocalDateTime.now().minusDays(1).atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();

        Assertions.assertEquals("api.coingecko.com",response.getHost());
        Assertions.assertEquals("/api/v3/coins/BTC/market_chart/range",response.getPath());
        Assertions.assertTrue(response.getQuery().contains("to="+yesterday));

    }
}