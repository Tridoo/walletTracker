package com.tridoo.wallettracker.wallet;

import com.tridoo.wallettracker.common.TransactionDto;

import java.math.BigDecimal;
import java.util.List;

public interface WalletRepo {
    List<WalletEntry> getWallet();

    void updateWallet(List<WalletEntry> wallet);
    void updateWalletEntry(String currencyId, BigDecimal quantity, BigDecimal value);

    List<TransactionDto> getTransactions(String currencyId);

    void addTransaction(String currencyId, BigDecimal quantity, BigDecimal value);

    void sellAll(String currencyId);

    public void setFileAccess(LocalFileAccess fileAccess);
}
