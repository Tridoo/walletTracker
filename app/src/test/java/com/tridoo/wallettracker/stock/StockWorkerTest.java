package com.tridoo.wallettracker.stock;

import android.util.Log;
import androidx.core.util.Pair;
import com.tridoo.wallettracker.common.TimeRange;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StockWorkerTest {
    @Mock
    StockController controler;
    @Mock
    StockReader reader;

    private static MockedStatic<Log> log;

    @BeforeAll
    public static void init() {
        log = mockStatic(Log.class);
    }

    @AfterAll
    public static void close() {
        log.close();
    }

    @Test
    public void emptyQueueTest() {
        Queue<StockDto> result = new ArrayDeque<>();

        StockWorker worker = new StockWorker(reader, controler, result);
        worker.run();

        assertTrue(result.isEmpty());
    }

    @Test
    public void queueTest() {
        Queue<StockDto> result = new ArrayDeque<>();
        Deque<Pair<String, BigDecimal>> cryptoQueue = new ArrayDeque<>();
        cryptoQueue.add(Pair.create("btc", BigDecimal.valueOf(1000.0)));

        doAnswer(in -> cryptoQueue.poll())
                .when(controler).getNextCurrency();

        when(reader.getCryptocurrencyTrack("btc", TimeRange.DAY)).thenReturn(Optional.of(BigDecimal.valueOf(990.0)));
        when(reader.getCryptocurrencyTrack("btc", TimeRange.WEEK)).thenReturn(Optional.of(BigDecimal.valueOf(1004.0)));

        StockWorker worker = new StockWorker(reader, controler, result);
        worker.run();

        assertEquals(1, result.size());
        assertEquals("1.01",result.peek().getDayChange());
        assertEquals("-0.40",result.peek().getWeekChange());
    }
}
