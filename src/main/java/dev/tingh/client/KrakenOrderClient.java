package dev.tingh.client;

import com.fasterxml.jackson.databind.JsonNode;
import dev.tingh.exception.OrderSubmissionException;
import dev.tingh.trading.AmendOrderBuilder;
import dev.tingh.trading.CancelAfterBuilder;
import dev.tingh.trading.CancelOrderBuilder;
import dev.tingh.trading.OrderBuilder;
import dev.tingh.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class KrakenOrderClient extends KrakenBaseClient {
    private static final Logger logger = LoggerFactory.getLogger(KrakenOrderClient.class);

    private static final int ORDER_TIMEOUT_SECONDS = 10;

    // Callback interface for order events
    public interface OrderEventListener {
        void onOrderSuccess(String orderID, String userref);
        void onOrderRejected(String reason, String userref);
    }

    private OrderEventListener orderEventListener;

    public KrakenOrderClient(URI serverUri) {
        super(serverUri);
    }

    public void setOrderEventListener(OrderEventListener listener) {
        this.orderEventListener = listener;
    }

    @Override
    public void onMessage(String message) {
        logger.info("Order client received message: {}", message);

        try {
            JsonNode jsonNode = JsonUtils.getObjectMapper().readTree(message);

            if (jsonNode.has("method") && "add_order".equals(jsonNode.get("method").asText())) {
                handleAddOrderResponse(jsonNode);
            }

        } catch (Exception e) {
            logger.error("Error processing order message: {}", e.getMessage(), e);
        }
    }

    private void handleAddOrderResponse(JsonNode jsonNode) {
        boolean success = jsonNode.has("success") && jsonNode.get("success").asBoolean();

        if (success && jsonNode.has("result")) {
            JsonNode result = jsonNode.get("result");
            String orderId = result.get("order_id").asText();
            String userref = result.has("order_userref") ? result.get("order_userref").asText() : null;

            if (userref != null) {
                logger.info("Order successfully submitted: {}, userref: {}", orderId, userref);

                if (orderEventListener != null) {
                    orderEventListener.onOrderSuccess(orderId, userref);
                }
            }
        } else {
            String errorMsg = jsonNode.has("error") ? jsonNode.get("error").asText() : "Unknown error";
            String userref = null;

            // Try to extract userref from request
            if (jsonNode.has("req_id")) {
                userref = String.valueOf(jsonNode.get("req_id").asInt());
            }

            logger.error("Order rejected: {}, userref: {}", errorMsg, userref);

            if (orderEventListener != null && userref != null) {
                orderEventListener.onOrderRejected(errorMsg, userref);
            }
        }
    }

    private void handleCancelOrderResponse(JsonNode jsonNode) {
        boolean success = jsonNode.has("success") && jsonNode.get("success").asBoolean();
        String reqId = jsonNode.has("req_id") ? jsonNode.get("req_id").asText() : null;

        if (success && jsonNode.has("result")) {
            JsonNode result = jsonNode.get("result");
            String orderId = result.get("order_id").asText();

            logger.info("Cancel order request successful for order ID: {}, req_id: {}", orderId, reqId);
        } else {
            String errorMsg = jsonNode.has("error") ? jsonNode.get("error").asText() : "Unknown error";
            logger.error("Cancel order request failed: {}, req_id: {}", errorMsg, reqId);
        }
    }

    public void sendOrder(OrderBuilder order) throws OrderSubmissionException {
        // Ensure we have a userref for tracking the order
        Map<String, Object> orderDetails = order.build();
        String userref = (String) orderDetails.get("userref");

        if (userref == null) {
            userref = "order-" + System.currentTimeMillis();
            order = order.withOrderUserref(userref);
        }

        // Send the order
        Map<String, Object> orderMap = new HashMap<>();
        orderMap.put("event", "addOrder");
        orderMap.putAll(order.build());
        send(JsonUtils.toJson(orderMap));
    }

    public void amendOrder(AmendOrderBuilder amendOrder) {
        Map<String, Object> amendOrderMap = new HashMap<>();
        amendOrderMap.put("event", "amendOrder");
        amendOrderMap.putAll(amendOrder.build());
        send(JsonUtils.toJson(amendOrderMap));
    }

    public void cancelOrder(CancelOrderBuilder order) {
        Map<String, Object> orderMap = new HashMap<>();
        orderMap.put("event", "cancelOrder");
        orderMap.putAll(order.build());
        send(JsonUtils.toJson(orderMap));
    }

    public void cancelAfter(CancelAfterBuilder cancelAfter) {
        Map<String, Object> cancelAfterMap = new HashMap<>();
        cancelAfterMap.put("event", "cancelAllOrdersAfter");
        cancelAfterMap.putAll(cancelAfter.build());
        send(JsonUtils.toJson(cancelAfterMap));
    }
}