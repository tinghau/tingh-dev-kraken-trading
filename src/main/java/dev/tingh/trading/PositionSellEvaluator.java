package dev.tingh.trading;

import dev.tingh.data.model.TickerData;
import dev.tingh.trading.position.Position;
import dev.tingh.trading.position.PositionListener;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;

public class PositionSellEvaluator implements PositionListener, Consumer<TickerData> {

    private final NavigableSet<Position> positionsByExitTime = new TreeSet<>(Comparator.comparing(Position::getExitTime));
    private final NavigableSet<Position> positionsByTargetPrice = new TreeSet<>(Comparator.comparing(Position::getExitPrice));

    private final DefaultOrderDispatcher orderDispatcher;

    public PositionSellEvaluator(DefaultOrderDispatcher orderDispatcher) {
        this.orderDispatcher = orderDispatcher;
    }

    @Override
    public synchronized void onPositionSnapshot(List<Position> positions) {
        positionsByExitTime.clear();
        positionsByTargetPrice.clear();
        positionsByExitTime.addAll(positions);
        positionsByTargetPrice.addAll(positions);
    }

    @Override
    public synchronized void onPositionUpdate(Position position) {
        // Remove old instance if present (assumes Position has proper equals/hashCode)
        positionsByExitTime.remove(position);
        positionsByTargetPrice.remove(position);
        // Add updated position
        positionsByExitTime.add(position);
        positionsByTargetPrice.add(position);
    }

    // Accessors for sorted views
    public List<Position> getPositionsByExitTime() {
        return new ArrayList<>(positionsByExitTime);
    }

    public List<Position> getPositionsByTargetPrice() {
        return new ArrayList<>(positionsByTargetPrice);
    }

    @Override
    public void accept(TickerData tickerData) {
        if (tickerData == null) {
            return; // Ignore null data
        }

        for (Position position : positionsByExitTime) {
            tickerData.getData().forEach(data -> {
                if (hasReachedTargetTime(data, position) ||
                        hasReachedTargetPrice(data, position)) {
                    OrderBuilder orderBuilder = new OrderBuilder("limit",
                            position.getSymbol(),
                            "sell",
                            position.getQuantity())
                            .withLimitPrice(String.valueOf(position.getExitPrice()));
                    orderDispatcher.sendOrder(orderBuilder);
                    position.close();
                    onPositionUpdate(position); // Notify listeners of the update
                }
            });
        }
    }

    private boolean hasReachedTargetPrice(TickerData.TickerSymbolData tickerData, Position position) {
        return tickerData.getLast().compareTo(position.getExitPrice()) >= 0;
    }

    private static boolean hasReachedTargetTime(TickerData.TickerSymbolData tickerData, Position position) {
        return LocalDateTime.now().isAfter(position.getExitTime());
    }
}
