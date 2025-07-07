package dev.tingh.trading;

import dev.tingh.client.KrakenOrderClient;

public interface OrderDispatcher extends KrakenOrderClient.OrderEventListener {

    String sendOrder(OrderBuilder orderBuilder);

    String cancelOrder(CancelOrderBuilder cancelOrderBuilder);

    void setOrderExecutor(OrderExecutor orderExecutor, CancelOrderExecutor cancelOrderExecutor);

    // Function interfaces
    interface OrderExecutor {
        void execute(OrderBuilder orderBuilder);
    }

    interface CancelOrderExecutor {
        void execute(CancelOrderBuilder cancelOrderBuilder);
    }
}

