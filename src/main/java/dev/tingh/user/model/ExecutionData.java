package dev.tingh.user.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ExecutionData {
    private String channel;
    private String type;
    private List<ExecutionItem> data;

    public String getChannel() {
        return channel;
    }

    public String getType() {
        return type;
    }

    public List<ExecutionItem> getData() {
        return data;
    }

    public static class ExecutionItem {
        @JsonProperty("order_id")
        private String orderId; // "order_id"
        @JsonProperty("order_userref")
        private Integer orderUserref; // "order_userref"
        @JsonProperty("exec_id")
        private String execId; // "exec_id"
        @JsonProperty("exec_type")
        private String execType; // "exec_type"
        @JsonProperty("trade_id")
        private String tradeId; // "trade_id"
        private String symbol; // "symbol"
        private String side; // "side"
        private String lastQty;
        @JsonProperty("last_price")
        private String lastPrice; // "last_price"
        @JsonProperty("liquidity_ind")
        private String liquidityInd; // "liquidity_ind"
        private String cost;
        @JsonProperty("order_qty")
        private String orderQty; // "order_qty"
        @JsonProperty("cum_cost")
        private String cumCost; // "cum_cost"
        @JsonProperty("cum_qty")
        private String cumQty; // "cum_qty"
        @JsonProperty("time_in_force")
        private String timeInForce; // "time_in_force"
        @JsonProperty("order_type")
        private String orderType; // "order_type"
        @JsonProperty("limit_price_type")
        private String limitPriceType; // "limit_price_type"
        @JsonProperty("limit_price")
        private String limitPrice; // "limit_price"
        @JsonProperty("stop_price")
        private String stopPrice; // "stop_price"
        @JsonProperty("avg_price")
        private String averagePrice; // "avg_price"
        @JsonProperty("order_status")
        private String orderStatus; // "order_status"
        @JsonProperty("fee_usd_equiv")
        private String feeUsdEquiv; // "fee_usd_equiv"
        @JsonProperty("fee_ccy_pref")
        private String feeCcyPref; // "fee_ccy_pref"
        private String timestamp; // "timestamp"
        private List<Fee> fees;

        public String getOrderId() { return orderId; }
        public Integer getOrderUserref() { return orderUserref; }
        public String getSymbol() { return symbol; }
        public String getOrderQty() { return orderQty; }
        public String getCumCost() { return cumCost; }
        public String getTimeInForce() { return timeInForce; }
        public String getExecType() { return execType; }
        public String getSide() { return side; }
        public String getOrderType() { return orderType; }
        public String getLimitPriceType() { return limitPriceType; }
        public String getLimitPrice() { return limitPrice; }
        public String getStopPrice() { return stopPrice; }
        public String getOrderStatus() { return orderStatus; }
        public String getFeeUsdEquiv() { return feeUsdEquiv; }
        public String getFeeCcyPref() { return feeCcyPref; }
        public String getTimestamp() { return timestamp; }
        public List<Fee> getFees() { return fees; }
    }

    public static class Fee {
        private String asset;
        private String qty;

        public String getAsset() {
            return asset;
        }

        public String getQty() {
            return qty;
        }
    }
}