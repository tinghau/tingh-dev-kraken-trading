package dev.tingh.data.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

public class TickerData {
    private String channel;
    private String type;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'")
    private final LocalDateTime timestamp = LocalDateTime.now(ZoneId.of("UTC"));
    private List<TickerSymbolData> data;

    public String getChannel() {
        return channel;
    }

    public String getType() {
        return type;
    }

    public List<TickerSymbolData> getData() {
        return data;
    }

    public static class TickerSymbolData {
        private String symbol;
        private String bid;
        @JsonProperty("bid_qty")
        private String bidQty;
        private String ask;
        @JsonProperty("ask_qty")
        private String askQty;
        private BigDecimal last;
        private String volume;
        private String vwap;
        private String low;
        private String high;
        private String change;
        @JsonProperty("change_pct")
        private String changePct;

        public String getSymbol() { return symbol; }
        public String getBid() { return bid; }
        public String getBidQty() { return bidQty; }
        public String getAsk() { return ask; }
        public String getAskQty() { return askQty; }
        public BigDecimal getLast() { return last; }
        public String getVolume() { return volume; }
        public String getVwap() { return vwap; }
        public String getLow() { return low; }
        public String getHigh() { return high; }
        public String getChange() { return change; }
        public String getChangePct() { return changePct; }
    }
}