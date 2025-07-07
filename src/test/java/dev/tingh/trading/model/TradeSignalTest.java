package dev.tingh.trading.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.tingh.util.JsonUtils;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TradeSignalTest {

    @Test
    void testDeserializeTradeSignal() throws Exception {
        String json = "{\"type\": \"signal\", \"action\": \"entry\", \"symbol\": \"BTC/USD\", \"timestamp\": \"2025-06-11T20:41:00\", \"price\": 108888.4, \"target_price\": 112155.052, \"target_return\": 3.0, \"hold_time_hours\": 72, \"order_qty\": 1.0}";

        ObjectMapper mapper = JsonUtils.getObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        TradeSignal signal = mapper.readValue(json, TradeSignal.class);

        assertEquals("signal", signal.getType());
        assertEquals("entry", signal.getAction());
        assertEquals("BTC/USD", signal.getSymbol());
        assertEquals(LocalDateTime.of(2025, 6, 11, 20, 41, 0), signal.getTimestamp());
        assertEquals(new BigDecimal("108888.4"), signal.getPrice());
        assertEquals(new BigDecimal("112155.052"), signal.getTargetPrice());
        assertEquals(new BigDecimal("3.0"), signal.getTargetReturn());
        assertEquals(72, signal.getHoldTimeHours());
        assertEquals(new BigDecimal("1.0"), signal.getOrderQty());
    }
}
