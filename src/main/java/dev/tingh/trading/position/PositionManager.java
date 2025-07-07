package dev.tingh.trading.position;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PositionManager {
    /**
     * Create and store a new position
     */
    void openPosition(String symbol, String orderRef, BigDecimal quantity, BigDecimal entryPrice, BigDecimal exitPrice, LocalDateTime exitTime);

    /**
     * Close an existing position
     */
    void closePosition(String positionId, String exitPrice);

    /**
     * Get a position by ID
     */
    Optional<Position> getPosition(String positionId);

    void addListener(PositionListener listener);

    /**
     * Get all open positions
     */
    List<Position> getOpenPositions();

    /**
     * Get all positions for a symbol
     */
    List<Position> getPositionsForSymbol(String symbol);

    /**
     * Check if position limit is reached for a symbol
     */
    boolean isPositionLimitReached(String symbol);

    /**
     * Save positions to persistent storage
     */
    void savePositions();

    /**
     * Load positions from persistent storage
     */
    void loadPositions();
}