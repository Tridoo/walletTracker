package com.tridoo.wallettracker.wallet;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
public class PricesHolderTest {

    @Test
    public void addPriceTest() {
        PricesHolder holder = PricesHolder.getInstance();
        holder.addPrice("btc", BigDecimal.TEN);

        assertEquals(BigDecimal.TEN, holder.getPrice("btc"));
        assertNull(holder.getPrice("pln"));
    }


}
