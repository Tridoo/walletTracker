package com.tridoo.wallettracker.wallet;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class WalletDtoTest {

    @Test
    public void noPriceTest(){
        WalletDto dto = new WalletDto("BTC", BigDecimal.ONE, BigDecimal.ONE, Optional.empty());
        assertEquals("0",dto.getChange());
    }
    @Test
    public void lowPriceTest(){
        WalletDto dto = new WalletDto("BTC", BigDecimal.ONE, BigDecimal.ONE, Optional.of(new BigDecimal("0.9")));
        assertEquals("0.900000",dto.getFormattedPrice());
    }

    @Test
    public void mediumPriceTest(){
        WalletDto dto = new WalletDto("BTC", BigDecimal.ONE, BigDecimal.ONE, Optional.of(BigDecimal.ONE));
        assertEquals("1.000",dto.getFormattedPrice());
    }

    @Test
    public void highPriceTest(){
        WalletDto dto = new WalletDto("BTC", BigDecimal.ONE, BigDecimal.ONE, Optional.of(BigDecimal.valueOf(100)));
        assertEquals("100.0",dto.getFormattedPrice());
    }

    @Test
    public void xxlPriceTest(){
        WalletDto dto = new WalletDto("BTC", BigDecimal.ONE, BigDecimal.ONE, Optional.of(BigDecimal.valueOf(1000)));
        assertEquals("1000",dto.getFormattedPrice());
    }
}
