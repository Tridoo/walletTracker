package com.tridoo.wallettracker.stock;

import com.tridoo.wallettracker.BuildConfig;
import com.tridoo.wallettracker.common.TimeRange;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class CoingeckoUrlBuilder {

    private static final String TOP_URL = "https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&order=market_cap_desc&per_page=10&page=1&sparkline=false";

    private static final long TIME_OFFSET = 60 * 60;

    public static URL getTopCryptocurrenciesUrl() throws MalformedURLException {
        return new URL(CoingeckoUrlBuilder.TOP_URL);
    }

    public static URL createUrl(String currencyId, TimeRange timeRange) throws MalformedURLException {
        String urlString = "https://api.coingecko.com/api/v3/coins/"
                + currencyId
                + "/market_chart/range?vs_currency=usd&"
                + "from=" + getFromTime(timeRange)
                + "&to=" + getToTime(timeRange)
                + "&x_cg_demo_api_key="
                + BuildConfig.API_KEY;

        return new URL(urlString);
    }

    private static long getFromTime(TimeRange range) {
        return getTime(range, TIME_OFFSET);
    }

    private static long getToTime(TimeRange range) {
        return getTime(range, 0);
    }

    private static long getTime(TimeRange range, long offset) {
        switch (range) {
            case DAY:
                return LocalDateTime.now().minusDays(1).atZone(ZoneId.systemDefault()).toInstant().getEpochSecond() - offset;
            case WEEK:
                return LocalDateTime.now().minusDays(7).atZone(ZoneId.systemDefault()).toInstant().getEpochSecond() - offset;
            case MONTH:
                return LocalDateTime.now().minusMonths(1).atZone(ZoneId.systemDefault()).toInstant().getEpochSecond() - offset;
            case YEAR:
                return LocalDateTime.now().minusYears(1).atZone(ZoneId.systemDefault()).toInstant().getEpochSecond() - offset;
            case NOW:
            default:
                return LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().getEpochSecond() - offset;
        }
    }

}
