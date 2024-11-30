package com.tridoo.wallettracker.wallet;

import android.util.Log;
import com.tridoo.wallettracker.stock.StockReader;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WalletWorkerTest {

    private static MockedStatic<Log> log;
    @Mock
    private StockReader reader;

    @BeforeAll
    public static void init() {
        log = mockStatic(Log.class);
    }

    @AfterAll
    public static void close() {
        log.close();
    }

    @Test
    public void test() {
        Set<String> ids = new HashSet<>();
        ids.add("btc");

        Map<String, BigDecimal> result = new HashMap<>();

        when(reader.getCryptocurrencyTrack(anyString(), any())).thenReturn(Optional.of(BigDecimal.ONE));
        WalletWorker worker = new WalletWorker(reader, ids, result);
        worker.run();

        assertEquals(BigDecimal.ONE, result.get("btc"));
    }
}
