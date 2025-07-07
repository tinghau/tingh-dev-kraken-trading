package dev.tingh.trading.position;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static dev.tingh.trading.position.PositionStatus.OPEN;

public class Position {
    private final String id;
    private final String symbol;
    private final String orderRef;
    private final BigDecimal entryPrice;
    private final LocalDateTime entryTime;
    private final LocalDateTime exitTime;

    private final BigDecimal exitPrice;
    private final BigDecimal quantity;

    private PositionStatus status;

    public Position(String symbol, String orderRef, BigDecimal quantity, BigDecimal entryPrice, BigDecimal exitPrice, LocalDateTime exitTime) {
        this(UUID.randomUUID().toString(), symbol, orderRef, quantity, entryPrice, exitPrice, exitTime, OPEN);
    }

    private Position(String symbol, String orderRef, BigDecimal quantity, BigDecimal entryPrice, BigDecimal exitPrice, LocalDateTime exitTime, PositionStatus status) {
        this(UUID.randomUUID().toString(), symbol, orderRef, quantity, entryPrice, exitPrice, exitTime, status);

    }

    private Position(String id, String symbol, String orderRef, BigDecimal quantity, BigDecimal entryPrice, BigDecimal exitPrice, LocalDateTime exitTime, PositionStatus status) {
        this.id = id;
        this.symbol = symbol;
        this.orderRef = orderRef;
        this.entryPrice = entryPrice;
        this.exitPrice = exitPrice;
        this.quantity = quantity;
        this.entryTime = LocalDateTime.now();
        this.exitTime = exitTime;
        this.status = status;
    }

    // Getters and required methods
    public String getId() { return id; }
    public String getSymbol() { return symbol; }
    public String getOrderRef() { return orderRef; }
    public BigDecimal getEntryPrice() { return entryPrice; }
    public BigDecimal getQuantity() { return quantity; }
    public PositionStatus getStatus() { return status; }
    public BigDecimal getExitPrice() { return exitPrice; }
    public LocalDateTime getExitTime() { return exitTime; }

    public void close() {
        this.status = PositionStatus.CLOSED;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("symbol", symbol);
        map.put("orderRef", orderRef);
        map.put("entryPrice", entryPrice.toString());
        map.put("quantity", quantity.toString());
        map.put("entryTime", entryTime.toString());
        map.put("status", status);
        map.put("exitPrice", exitPrice.toString());
        if (exitTime != null) map.put("exitTime", exitTime.toString());
        return map;
    }

    public static Position fromMap(Map<String, Object> map) {
        return new Position(map.get("symbol").toString(),
                map.get("orderRef").toString(),
                new BigDecimal((String) map.get("quantity")),
                new BigDecimal((String) map.get("entryPrice")),
                new BigDecimal((String) map.get("exitPrice")),
                map.get("exitTime") != null ? LocalDateTime.parse(map.get("exitTime").toString()) : null,
                PositionStatus.valueOf((String) map.get("status")));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Position position = (Position) o;
        return Objects.equals(id, position.id) &&
                Objects.equals(symbol, position.symbol) &&
                Objects.equals(orderRef, position.orderRef) &&
                Objects.equals(entryPrice, position.entryPrice) &&
                Objects.equals(quantity, position.quantity);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(id);
        result = 31 * result + Objects.hashCode(symbol);
        result = 31 * result + Objects.hashCode(orderRef);
        result = 31 * result + Objects.hashCode(entryPrice);
        result = 31 * result + Objects.hashCode(quantity);
        return result;
    }
}