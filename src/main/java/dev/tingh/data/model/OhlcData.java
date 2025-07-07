package dev.tingh.data.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OhlcData {
    private String channel;
    private String type;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS'Z'")
    private LocalDateTime timestamp;
    private List<OhlcSymbolData> data;

    public String getChannel() {
        return channel;
    }

    public String getType() {
        return type;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public List<OhlcSymbolData> getData() {
        return data;
    }

    public static class OhlcSymbolData {
        private String symbol;
        private BigDecimal open;
        private BigDecimal high;
        private BigDecimal low;
        private BigDecimal close;
        private BigDecimal trades;
        private BigDecimal volume;
        private BigDecimal vwap;
        @JsonProperty("interval_begin")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS'Z'")
        private LocalDateTime intervalBegin;
        private int interval;
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'")
        private LocalDateTime timestamp;

        public String getSymbol() { return symbol; }
        public BigDecimal getOpen() { return open; }
        public BigDecimal getHigh() { return high; }
        public BigDecimal getLow() { return low; }
        public BigDecimal getClose() { return close; }
        public BigDecimal getTrades() { return trades; }
        public BigDecimal getVolume() { return volume; }
        public BigDecimal getVwap() { return vwap; }
        public LocalDateTime getIntervalBegin() { return intervalBegin; }
        public int getInterval() { return interval; }
        public LocalDateTime getTimestamp() { return timestamp; }
    }
}