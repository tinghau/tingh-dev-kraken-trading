package dev.tingh.trading;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingOrderDispatcher implements OrderDispatcher {
    private static final Logger logger = LoggerFactory.getLogger(LoggingOrderDispatcher.class);

    @Override
    public String sendOrder(OrderBuilder orderBuilder) {
        logger.info("Sending order: {}", orderBuilder);
        return orderBuilder.toString();
    }

    @Override
    public String cancelOrder(CancelOrderBuilder cancelOrderBuilder) {
        logger.info("Cancelling order: {}", cancelOrderBuilder);
        return cancelOrderBuilder.getOrderId();
    }

    @Override
    public void onOrderSuccess(String orderID, String userref) {
        logger.info("Order success: orderID={}, userref={}", orderID, userref);
    }

    @Override
    public void onOrderRejected(String reason, String userref) {
        logger.warn("Order rejected: reason={}, userref={}", reason, userref);
    }

    @Override
    public void setOrderExecutor(OrderExecutor orderExecutor, CancelOrderExecutor cancelOrderExecutor) {
        logger.debug("Order executors set: orderExecutor={}, cancelOrderExecutor={}", orderExecutor, cancelOrderExecutor);
    }
}
