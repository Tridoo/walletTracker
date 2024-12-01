package com.tridoo.wallettracker.stock;

import android.util.Log;
import androidx.core.util.Pair;
import com.tridoo.wallettracker.common.TimeRange;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
public class StockReaderTest {

    private static MockedStatic<Log> log;

    StockReaderImpl readerSpy;

    @BeforeEach
    public void beforeEach(){
        readerSpy = Mockito.spy(new StockReaderImpl());
    }
    @BeforeAll
    public static void init() {
        log = mockStatic(Log.class);
    }

    @AfterAll
    public static void close() {
        log.close();
    }

    @Test
    public void getTopCryptocurrenciesTest() {
        Mockito.doReturn(topCryptoJsonArray()).when(readerSpy).getResponseFromUrl(any());

        List<Pair<String, BigDecimal>> result = readerSpy.getTopCryptocurrencies();
        assertEquals(2, result.size()); //filtered stableCoins
        assertEquals("bitcoin", result.get(0).first);
    }

    @Test
    public void getCryptocurrencyTrackTest() {
        Mockito.doReturn(trackCryptoJsonArray()).when(readerSpy).getResponseFromUrl(any());

        Optional<BigDecimal> result = readerSpy.getCryptocurrencyTrack("bitcoin", TimeRange.DAY);
        assertTrue(result.isPresent());
        assertEquals(new BigDecimal("94644.2"), result.get());
    }

    @Test
    public void getCryptocurrencyMissingTrackTest() {
        Mockito.doReturn(emptyTrackCryptoJsonArray()).when(readerSpy).getResponseFromUrl(any());

        Optional<BigDecimal> result = readerSpy.getCryptocurrencyTrack("bitcoin", TimeRange.DAY);
        assertTrue(result.isEmpty());
    }

    private String topCryptoJsonArray() {
        return "[{\"id\":\"bitcoin\",\"current_price\":95256},{\"id\":\"ethereum\",\"current_price\":256},{\"id\":\"tether\",\"current_price\":1}]";
    }

    private String trackCryptoJsonArray() {
        return "{\"prices\":[[1732720231528,94861.9],[1732720591481,94756.4],[1732720851788,94644.2]]}";
    }

    private String emptyTrackCryptoJsonArray() {
        return "{\"prices\":[]}";
    }
}
