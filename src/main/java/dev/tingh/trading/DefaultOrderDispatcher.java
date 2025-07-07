package dev.tingh.trading;

import dev.tingh.client.KrakenOrderClient;
import dev.tingh.exception.OrderSubmissionException;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultOrderDispatcher implements KrakenOrderClient.OrderEventListener, OrderDispatcher {

    private final Map<String, CompletableFuture<String>> pendingOrders = new ConcurrentHashMap<>();

    private OrderExecutor orderExecutor;
    private CancelOrderExecutor cancelOrderExecutor;

    public void setOrderExecutor(OrderExecutor orderExecutor, CancelOrderExecutor cancelOrderExecutor) {
        this.orderExecutor = orderExecutor;
        this.cancelOrderExecutor = cancelOrderExecutor;
    }

    public String cancelOrder(CancelOrderBuilder cancelOrderBuilder) throws OrderSubmissionException {
        // Get the order ID being canceled for tracking
        Map<String, Object> cancelDetails = cancelOrderBuilder.build();
        String orderId = (String) cancelDetails.get("order_id");

        if (orderId == null) {
            throw new OrderSubmissionException("Cannot cancel order: order_id is required");
        }

        // Use order ID as tracking reference for the cancel operation
        String cancelRef = "cancel-" + orderId;

        // Create a future to track this cancellation
        CompletableFuture<String> cancelFuture = new CompletableFuture<>();
        pendingOrders.put(cancelRef, cancelFuture);

        cancelOrderExecutor.execute(cancelOrderBuilder);
        return cancelRef;
    }

    // Add these methods to handle cancel order callbacks
    public void onCancelOrderSuccess(String orderId) {
        String cancelRef = "cancel-" + orderId;
        CompletableFuture<String> future = pendingOrders.get(cancelRef);
        if (future != null) {
            future.complete(orderId);
        }
    }

    public void onCancelOrderRejected(String orderId, String reason) {
        String cancelRef = "cancel-" + orderId;
        CompletableFuture<String> future = pendingOrders.get(cancelRef);
        if (future != null) {
            future.completeExceptionally(new OrderSubmissionException("Cancel order rejected: " + reason));
        }
    }

    public String sendOrder(OrderBuilder order) throws OrderSubmissionException {
        // Ensure we have a userref for tracking
        Map<String, Object> orderDetails = order.build();
        String userref = (String) orderDetails.get("userref");

        if (userref == null) {
            userref = "order-" + System.currentTimeMillis();
            order = order.withOrderUserref(userref);
        }

        // Create a future to track this order
        CompletableFuture<String> orderFuture = new CompletableFuture<>();
        pendingOrders.put(userref, orderFuture);
        orderExecutor.execute(order);
        return userref;
    }

    @Override
    public void onOrderSuccess(String orderId, String userref) {
        CompletableFuture<String> future = pendingOrders.get(userref);
        if (future != null) {
            future.complete(orderId);
        }
    }

    @Override
    public void onOrderRejected(String reason, String userref) {
        CompletableFuture<String> future = pendingOrders.get(userref);
        if (future != null) {
            future.completeExceptionally(new OrderSubmissionException("Order rejected: " + reason));
        }
    }
}