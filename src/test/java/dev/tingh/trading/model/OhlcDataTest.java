package dev.tingh.trading.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.tingh.data.model.OhlcData;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OhlcDataTest {

    @Test
    void testDeserializationOfOhlcSnapshot() throws Exception {
        String json = "{\"channel\":\"ohlc\",\"type\":\"snapshot\",\"timestamp\":\"2025-06-17T21:04:23.949829997Z\",\"data\":[{\"symbol\":\"BTC/USD\",\"open\":104448.1,\"high\":104448.1,\"low\":104365.8,\"close\":104365.9,\"trades\":49,\"volume\":2.31676300,\"vwap\":104378.5,\"interval_begin\":\"2025-06-17T20:55:00.000000000Z\",\"interval\":1,\"timestamp\":\"2025-06-17T20:56:00.000000Z\"}]}";
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        OhlcData ohlcData = mapper.readValue(json, OhlcData.class);

        assertEquals("ohlc", ohlcData.getChannel());
        assertEquals("snapshot", ohlcData.getType());
        assertEquals(LocalDateTime.of(2025, 6, 17, 21, 4, 23, 949_829_997), ohlcData.getTimestamp());
        assertNotNull(ohlcData.getData());
        assertEquals(1, ohlcData.getData().size());
        assertEquals("BTC/USD", ohlcData.getData().getFirst().getSymbol());
        assertEquals(LocalDateTime.of(2025, 6, 17, 20, 56, 0, 0), ohlcData.getData().getFirst().getTimestamp());
    }
}
