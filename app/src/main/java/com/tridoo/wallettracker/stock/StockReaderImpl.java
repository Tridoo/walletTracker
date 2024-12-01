package com.tridoo.wallettracker.stock;

import android.util.Log;
import com.google.gson.*;
import com.tridoo.wallettracker.common.TimeRange;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import androidx.core.util.Pair;

public class StockReaderImpl implements StockReader {

    private static final String EMPTY_STRING = "";

    private static final Set<String> STABLE_COINS = Set.of("tether", "usd-coin");

    @Override
    public List<Pair<String, BigDecimal>> getTopCryptocurrencies() {
        try {
            JsonElement elem = JsonParser.parseString(getResponseFromUrl(CoingeckoUrlBuilder.getTopCryptocurrenciesUrl()));
            return parseResponse(elem.getAsJsonArray());
        } catch (MalformedURLException e) {
            Log.e(StockReader.class.getName(), "Błąd przetwarzania URL " + e.getMessage());
        }
        return Collections.emptyList();
    }

    @Override
    public Optional<BigDecimal> getCryptocurrencyTrack(String currencyId, TimeRange timeRange) {

        try {
            String response = getResponseFromUrl(CoingeckoUrlBuilder.createUrl(currencyId, timeRange));
            JsonArray prices = (JsonArray) new Gson().fromJson(response, JsonObject.class).get("prices");

            if (prices.isEmpty()) {
                Log.w(StockReader.class.getName(), "Pusta odpowiedz");
                return Optional.empty();
            }

            String val = prices.get(prices.size() - 1).getAsJsonArray().get(1).getAsString();
            Log.d(StockReader.class.getName(), currencyId + " " + val);
            return Optional.of(new BigDecimal(val));

        } catch (MalformedURLException e) {
            Log.e(StockReader.class.getName(), "Błąd przetwarzania URL " + e.getMessage());
        }
        return Optional.empty();
    }

    //public 4 tests
    public String getResponseFromUrl(URL url) {
        String inputLine;
        StringBuilder response = new StringBuilder();

        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() != 200) {
                Log.e(StockReader.class.getName(), "Błąd w pobieraniu danych: " + conn.getResponseCode());
                conn.disconnect();
                return EMPTY_STRING;
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            in.close();
            conn.disconnect();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return response.toString();
    }


    private List<Pair<String, BigDecimal>> parseResponse(JsonArray cryptocurrencies) {
        return IntStream
                .range(0, cryptocurrencies.size())
                .mapToObj(i -> getJObject(cryptocurrencies, i))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private Optional<Pair<String, BigDecimal>> getJObject(JsonArray cryptocurrencies, int i) {
        try {
            JsonObject obj = cryptocurrencies.get(i).getAsJsonObject();
            String cryptoId = obj.get("id").getAsString();

            if (STABLE_COINS.contains(cryptoId)) {
                Log.d(StockReader.class.getName(), "stable ");
                return Optional.empty();
            }

            return Optional.of(new Pair<>(cryptoId, new BigDecimal(obj.get("current_price").getAsString())));
        } catch (Exception e) {
            Log.e(StockReader.class.getName(), "JSONException", e);
            return Optional.empty();
        }
    }

}
