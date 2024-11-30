package com.tridoo.wallettracker.stock;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class StockDtoTest {

    @Test
    public void missingPricesTest() {
        StockDto dto = new StockDto("btc", BigDecimal.TEN, Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty());
        assertEquals("-", dto.getDayChange());
    }

    @Test
    public void roundingPricesTest() {
        StockDto dto = new StockDto("btc", BigDecimal.valueOf(12.3456), Optional.of(BigDecimal.valueOf(0.3456)),
                Optional.of(BigDecimal.valueOf(0.1456)),Optional.of(BigDecimal.valueOf(0.2456)), Optional.of(BigDecimal.valueOf(0.4456)));
        assertEquals(12, dto.getRoundedPrice());
        assertEquals("12.35", dto.getPrice());
        assertEquals("0.35", dto.getDayChange());
        assertEquals("0.15", dto.getWeekChange());
        assertEquals("0.25", dto.getMonthChange());
        assertEquals("0.45", dto.getYearChange());
    }
}
