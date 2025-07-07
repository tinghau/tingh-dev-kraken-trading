package dev.tingh.trading.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TradeSignal {
    private String type;
    private String action;
    private String symbol;
    private LocalDateTime timestamp;
    private BigDecimal price;

    @JsonProperty("target_price")
    private BigDecimal targetPrice;

    @JsonProperty("target_return")
    private BigDecimal targetReturn;

    @JsonProperty("hold_time_hours")
    private int holdTimeHours;

    @JsonProperty("order_qty")
    private BigDecimal orderQty;

    // Default constructor for Jackson
    public TradeSignal() {
    }

    // Getters and setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getTargetPrice() {
        return targetPrice;
    }

    public void setTargetPrice(BigDecimal targetPrice) {
        this.targetPrice = targetPrice;
    }

    public BigDecimal getTargetReturn() {
        return targetReturn;
    }

    public void setTargetReturn(BigDecimal targetReturn) {
        this.targetReturn = targetReturn;
    }

    public int getHoldTimeHours() {
        return holdTimeHours;
    }

    public void setHoldTimeHours(int holdTimeHours) {
        this.holdTimeHours = holdTimeHours;
    }

    public BigDecimal getOrderQty() {
        return orderQty;
    }

    public void setOrderQty(BigDecimal orderQty) {
        this.orderQty = orderQty;
    }

    @Override
    public String toString() {
        return "TradeSignal{" +
                "type='" + type + '\'' +
                ", action='" + action + '\'' +
                ", symbol='" + symbol + '\'' +
                ", timestamp=" + timestamp +
                ", price=" + price +
                ", targetPrice=" + targetPrice +
                ", targetReturn=" + targetReturn +
                ", holdTimeHours=" + holdTimeHours +
                ", orderQty='" + orderQty + '\'' +
                '}';
    }
}