package com.tridoo.wallettracker.wallet;

import android.util.Log;
import com.tridoo.wallettracker.common.TransactionDto;
import com.tridoo.wallettracker.wallet.LocalFileAccess;
import com.tridoo.wallettracker.wallet.WalletEntry;
import com.tridoo.wallettracker.wallet.WalletRepo;
import com.tridoo.wallettracker.wallet.WalletRepoImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WalletRepoImplTest {

    @Mock
    private LocalFileAccess access;

    @Captor
    private ArgumentCaptor<String> captor;

    private WalletRepo repo;

    private static MockedStatic<Log> log;
    @BeforeAll
    public static void init() {
        log = mockStatic(Log.class);
    }

    @AfterAll
    public static void close() {
        log.close();
    }

    @BeforeEach
    public void initEach(){
        repo = new WalletRepoImpl(null);
        repo.setFileAccess(access);
    }

    @Test
    public void getEmptyWallet() {
        when(access.readSavedData(any())).thenReturn(Collections.emptyList());

        List<WalletEntry> result = repo.getWallet();
        assertEquals(0, result.size());
    }

    @Test
    public void getWallet() {
        when(access.readSavedData(any())).thenReturn(Arrays.asList("[{\"avgPrice\":69287.7,\"currencyId\":\"bitcoin\",\"quantity\":1.1}]"));

        List<WalletEntry> result = repo.getWallet();
        assertEquals(1, result.size());
        assertEquals("bitcoin", result.get(0).getCurrencyId());
    }

    @Test
    public void testUpdateWalletEntryNewCurrency() {
        when(access.readSavedData(any())).thenReturn(Arrays.asList("[{\"avgPrice\":69287.7,\"currencyId\":\"bitcoin\",\"quantity\":1.1}]"));

        repo.updateWalletEntry("BTC", BigDecimal.ONE, BigDecimal.TEN);
        verify(access).writeData(captor.capture(), anyString(), anyInt());
        assertTrue(captor.getValue().contains("BTC"));
    }

    @Test
    public void testUpdateWalletEntry() {
        when(access.readSavedData(any())).thenReturn(Arrays.asList("[{\"avgPrice\":69287.7,\"currencyId\":\"bitcoin\",\"quantity\":1.1}]"));

        repo.updateWalletEntry("bitcoin", BigDecimal.ONE, BigDecimal.TEN);
        verify(access).writeData(captor.capture(), anyString(), anyInt());
        assertTrue(captor.getValue().contains("\"quantity\":2.1"));
    }

    @Test
    public void testMissingTransactions() {
        repo.getTransactions("test");
        verify(access).readSavedData("test.dat");
    }

    @Test
    public void testTransactions() {
        when(access.readSavedData(any())).thenReturn(Arrays.asList("{\"quantity\":0.1,\"time\":1730914908410,\"value\":7456}"));

        List<TransactionDto> result = repo.getTransactions("test");
        assertEquals(1, result.size());
        assertEquals(7456, result.get(0).getValue().longValue());
    }

    @Test
    public void testTransactionParseException() {
        when(access.readSavedData(any())).thenReturn(Arrays.asList("{7456}"));

        List<TransactionDto> result = repo.getTransactions("test");
        assertEquals(0, result.size());
    }

    @Test
    public void testAddTransaction() {
        repo.addTransaction("BTC", BigDecimal.ONE, BigDecimal.TEN);

        verify(access).writeData(anyString(), anyString(), anyInt());
    }

    @Test
    public void testSellAll() {
        when(access.readSavedData(any())).thenReturn(Arrays.asList("[{\"avgPrice\":69287.7,\"currencyId\":\"bitcoin\",\"quantity\":1.1}]"));

        repo.sellAll("bitcoin");

        verify(access).writeData(anyString(), anyString(), anyInt());
        verify(access).deleteFile(anyString());
    }
}