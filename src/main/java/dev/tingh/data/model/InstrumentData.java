package dev.tingh.data.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;

public class InstrumentData {
    private String type;
    private InstrumentSymbolData data;

    public static class InstrumentSymbolData {
        private List<AssetDetail> assets;
        private List<PairDetail> pairs;

        public List<AssetDetail> getAssets() { return assets; }

        public List<PairDetail> getPairs() {
            return pairs;
        }
    }

    public static class AssetDetail {
        private String id;
        private String status;
        private String precision;
        @JsonProperty("precision_display")
        private String precisionDisplay;
        private String borrowable;
        @JsonProperty("collateral_value")
        private String collateralValue;
        @JsonProperty("margin_rate")
        private String marginRate;

        public String getId() { return id; }
        public String getStatus() { return status; }
        public String getPrecision() { return precision; }
        public String getPrecisionDisplay() { return precisionDisplay; }
        public String getBorrowable() { return borrowable; }
        public String getCollateralValue() { return collateralValue; }
        public String getMarginRate() { return marginRate; }
    }

    public static class PairDetail {
        private String symbol;
        private String base;
        private String quote;
        private String status;
        @JsonProperty("qty_precision")
        private long qtyPrecision;
        @JsonProperty("qty_increment")
        private BigDecimal qtyIncrement;
        @JsonProperty("price_precision")
        private long pricePrecision;
        @JsonProperty("cost_precision")
        private long costPrecision;
        private boolean marginable;
        @JsonProperty("has_index")
        private boolean hasIndex;
        @JsonProperty("cost_min")
        private BigDecimal costMin;
        @JsonProperty("margin_initial")
        private BigDecimal marginInitial;
        @JsonProperty("position_limit_long")
        private long positionLimitLong;
        @JsonProperty("position_limit_short")
        private long positionLimitShort;
        @JsonProperty("tick_size")
        private BigDecimal tickSize;
        @JsonProperty("price_increment")
        private BigDecimal priceIncrement;
        @JsonProperty("qty_min")
        private BigDecimal qtyMin;

        public String getSymbol() { return symbol; }
        public String getBase() { return base; }
        public String getQuote() { return quote; }
        public String getStatus() { return status; }
        public long getQtyPrecision() { return qtyPrecision; }
        public BigDecimal getQtyIncrement() { return qtyIncrement; }
        public long getPricePrecision() { return pricePrecision; }
        public long getCostPrecision() { return costPrecision; }
        public boolean isMarginable() { return marginable; }
        public boolean hasIndex() { return hasIndex; }
        public BigDecimal getCostMin() { return costMin; }
        public BigDecimal getMarginInitial() { return marginInitial; }
        public long getPositionLimitLong() { return positionLimitLong; }
        public long getPositionLimitShort() { return positionLimitShort; }
        public BigDecimal getTickSize() { return tickSize; }
        public BigDecimal getPriceIncrement() { return priceIncrement; }
        public BigDecimal getQtyMin() { return qtyMin; }
    }

    public String getType() {
        return type;
    }

    public InstrumentSymbolData getData() {
        return data;
    }
}