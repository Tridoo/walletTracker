package com.tridoo.wallettracker.wallet;

import android.content.Context;
import android.util.Log;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;
import com.tridoo.wallettracker.common.TransactionDto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class WalletRepoImpl implements WalletRepo {

    private final static String WALLET_FILE_NAME = "wallet.dat";
    private LocalFileAccess fileAccess;

    private final Gson gson = new Gson();

    public WalletRepoImpl(Context ctx) {
        fileAccess = new LocalFileAccess(ctx);
    }

    @Override
    public List<WalletEntry> getWallet() {
        List<String> savedData = fileAccess.readSavedData(WALLET_FILE_NAME);
        if (savedData.isEmpty()) return new ArrayList<>();
        String data = savedData.get(0);
        try {
            JsonElement elem = JsonParser.parseString(data);
            return parseResponseToWallet(elem.getAsJsonArray());
        } catch (Exception e) {
            Log.e(WalletRepo.class.getName(), "JSONException", e);
            return new ArrayList<>();
        }
    }

    @Override
    public void updateWallet(List<WalletEntry> wallet) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String jsonArray = objectMapper.writeValueAsString(wallet);
            fileAccess.writeData(jsonArray, WALLET_FILE_NAME, Context.MODE_PRIVATE);
        } catch (JsonProcessingException e) {
            Log.e(WalletRepo.class.getName(), "JsonProcessingException", e);
        }
    }

    @Override
    public void updateWalletEntry(String currencyId, BigDecimal quantity, BigDecimal value) {
        List<WalletEntry> wallet = getWallet();
        boolean exists = false;

        for (WalletEntry e : wallet) {
            if (e.getCurrencyId().equals(currencyId)) {
                exists = true;
                BigDecimal newQuantity = e.getQuantity().add(quantity);
                Log.i(WalletRepo.class.getName(), "newQuantity: " + newQuantity);

                if (BigDecimal.ZERO.equals(newQuantity)) {
                    wallet.remove(e);
                    break;
                }
                BigDecimal newAvgPrice = e.getQuantity().multiply(e.getAvgPrice())
                        .add(value)
                        .divide(newQuantity, RoundingMode.HALF_UP);

                Log.i(WalletRepo.class.getName(), "newAvgPrice: " + newAvgPrice);

                e.setQuantity(newQuantity);
                e.setAvgPrice(newAvgPrice);
                break;
            }
        }
        if (!exists) {
            wallet.add(new WalletEntry(currencyId, quantity, value.divide(quantity, RoundingMode.HALF_UP)));
            Log.i(WalletRepo.class.getName(), "new WalletEntry " + currencyId);
        }

        updateWallet(wallet);
    }

    @Override
    public List<TransactionDto> getTransactions(String currencyId) {
        List<String> savedData = fileAccess.readSavedData(currencyId + ".dat");
        Collections.reverse((savedData));
        return parseTransactions(savedData);
    }

    @Override
    public void addTransaction(String currencyId, BigDecimal quantity, BigDecimal value) {
        TransactionDto dto = new TransactionDto(Instant.now().toEpochMilli(), quantity, value);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String json = objectMapper.writeValueAsString(dto);
            fileAccess.writeData(json, currencyId + ".dat", Context.MODE_APPEND);
        } catch (JsonProcessingException e) {
            Log.e(WalletRepo.class.getName(), "JsonProcessingException", e);
        }
    }

    @Override
    public void sellAll(String currencyId) {
        boolean updated = false;
        List<WalletEntry> wallet = getWallet();
        for (int i = 0; i < wallet.size(); i++) {
            if (wallet.get(i).getCurrencyId().equals(currencyId)) {
                wallet.remove(i);
                updated = true;
                break;
            }
        }
        if (!updated) Log.w(WalletRepo.class.getName(), "Brak wpisu " + currencyId);
        else updateWallet(wallet);

        fileAccess.deleteFile(currencyId + ".dat");
    }


    private List<WalletEntry> parseResponseToWallet(JsonArray cryptocurrencies) {
        return IntStream
                .range(0, cryptocurrencies.size())
                .mapToObj(i -> getWalletEntry(cryptocurrencies, i))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private List<TransactionDto> parseTransactions(List<String> transactions) {
        return transactions.stream()
                .map(this::convertToTransactionDto)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private Optional<WalletEntry> getWalletEntry(JsonArray cryptocurrencies, int i) {
        try {
            JsonObject obj = cryptocurrencies.get(i).getAsJsonObject();
            return Optional.of(new WalletEntry(obj.get("currencyId").getAsString(), new BigDecimal(obj.get("quantity").getAsString()), new BigDecimal(obj.get("avgPrice").getAsString())));
        } catch (Exception e) {
            Log.e(WalletRepo.class.getName(), "JSONException", e);
            return Optional.empty();
        }
    }

    private Optional<TransactionDto> convertToTransactionDto(String json) {
        try {
            return Optional.of(gson.fromJson(json, TransactionDto.class));
        } catch (Exception e) {
            Log.e(WalletRepo.class.getName(), "convertToTransactionDto", e);
            return Optional.empty();
        }
    }


    public void setFileAccess(LocalFileAccess fileAccess) {
        this.fileAccess = fileAccess;
    }
}
