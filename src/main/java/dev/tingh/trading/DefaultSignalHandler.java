package dev.tingh.trading;

import com.google.inject.Inject;
import dev.tingh.trading.model.TradeSignal;
import dev.tingh.trading.position.PositionManager;
import dev.tingh.util.Orders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class DefaultSignalHandler implements SignalHandler {
    private static final Logger logger = LoggerFactory.getLogger(DefaultSignalHandler.class);

    public static String generateOrderRef() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        String randomSuffix = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return "ORDER-" + timestamp + "-" + randomSuffix;
    }

    private final OrderDispatcher orderDispatcher;
    private final PositionManager positionManager;

    @Inject
    public DefaultSignalHandler(OrderDispatcher orderDispatcher, PositionManager positionManager) {
        this.orderDispatcher = orderDispatcher;
        this.positionManager = positionManager;
    }

    @Override
    public void process(TradeSignal signal) {
        String action = signal.getAction();
        String symbol = signal.getSymbol();

        if (action == null || symbol == null) {
            logger.warn("Invalid signal received: missing action or symbol");
            return;
        }

        logger.info("Processing signal: {} for {}", action, symbol);

        switch (action) {
            case "entry":
                executeEntrySignal(signal);
                break;
            case "exit":
                executeExitSignal(signal);
                break;
            default:
                logger.warn("Unknown signal action: {}", action);
        }
    }

    private void executeEntrySignal(TradeSignal signal) {
        logger.info("Executing ENTRY signal: {}", signal);
        if (!positionManager.isPositionLimitReached(signal.getSymbol())) {
            positionManager.openPosition(signal.getSymbol(),
                    Orders.generateOrderRef(signal.getSymbol()),
                    signal.getOrderQty(),
                    signal.getPrice(),
                    signal.getTargetPrice(),
                    LocalDateTime.now().plusHours(signal.getHoldTimeHours()));

            OrderBuilder orderBuilder = new OrderBuilder("limit",
                    signal.getSymbol(),
                    "buy",
                    signal.getOrderQty())
                    .withLimitPrice(String.valueOf(signal.getTargetPrice()));
            logger.info("Sending order: {}", orderBuilder);

            orderDispatcher.sendOrder(orderBuilder);
        }
    }

    private void executeExitSignal(TradeSignal signal) {
        // Implement sell logic here
        logger.warn("EXIT signal execution is not supported: {}", signal);
    }
}