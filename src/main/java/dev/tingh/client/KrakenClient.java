package dev.tingh.client;

import com.google.inject.Guice;
import com.google.inject.Injector;
import dev.tingh.AuthTokens;
import dev.tingh.TradingModule;
import dev.tingh.data.subscription.*;
import dev.tingh.exception.ConnectionException;
import dev.tingh.trading.CancelOrderBuilder;
import dev.tingh.trading.OrderBuilder;
import dev.tingh.trading.OrderDispatcher;
import dev.tingh.user.subscription.BalanceSubscriptionBuilder;
import dev.tingh.user.subscription.ExecutionSubscriptionBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class KrakenClient {
    private static final Logger logger = LoggerFactory.getLogger(KrakenClient.class);

    public static void main(String[] args)  {
        // Create Guice injector with our module
        Injector injector = Guice.createInjector(new TradingModule());

        // Get the properly configured KrakenClient from Guice
        KrakenClient client = injector.getInstance(KrakenClient.class);

        // Start the client
        client.start();
    }

    private final KrakenDataClient dataClient;
    private final KrakenDataAuthClient dataAuthClient;
    private final KrakenOrderClient orderClient;
    private final KrakenUserClient userClient;

    public KrakenClient(
            KrakenDataClient dataClient,
            KrakenDataAuthClient dataAuthClient,
            KrakenOrderClient orderClient,
            KrakenUserClient userClient) {
        this.dataClient = dataClient;
        this.dataAuthClient = dataAuthClient;
        this.orderClient = orderClient;
        this.userClient = userClient;
    }

    public void start() {
        connect(dataClient);
//        connect(dataAuthClient);
//        connect(orderClient);
//        connect(userClient);

            subscribeToTicker();
//            subscribeToBook();
//        subscribeToOrders();
            subscribeToOhlc();
//            subscribeToTrade();
//            subscribeToInstrument();
//        subscribeToBalance();
//        subscribeToExecution();
    }

    public void configureOrderDispatcher(OrderDispatcher dispatcher) {
        // Internal setup without exposing the client
        orderClient.setOrderEventListener(dispatcher);
        dispatcher.setOrderExecutor(this::sendOrder, this::cancelOrder);
    }

    private void connect(KrakenBaseClient client) throws ConnectionException {
        final int MAX_RETRIES = 3;
        final long INITIAL_BACKOFF_MS = 1000; // Start with 1 second delay

        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                logger.info("Connecting to {} (attempt {}/{})", client.getClass().getSimpleName(), attempt, MAX_RETRIES);
                boolean connected = client.connectBlocking(10, TimeUnit.SECONDS);

                if (connected) {
                    logger.info("Successfully connected to {}", client.getClass().getSimpleName());
                    return;
                } else {
                    logger.warn("Failed to connect to {} (attempt {}/{})",
                            client.getClass().getSimpleName(), attempt, MAX_RETRIES);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("Connection interrupted for {}", client.getClass().getSimpleName(), e);
            } catch (Exception e) {
                logger.error("Connection error for {} (attempt {}/{}): {}",
                        client.getClass().getSimpleName(), attempt, MAX_RETRIES, e.getMessage());
            }

            if (attempt < MAX_RETRIES) {
                long backoffTime = INITIAL_BACKOFF_MS * attempt;
                logger.info("Retrying connection in {} ms", backoffTime);
                try {
                    Thread.sleep(backoffTime);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.warn("Retry delay was interrupted");
                }
            }
        }

        logger.error("Failed to connect to {} after {} attempts",
                client.getClass().getSimpleName(), MAX_RETRIES);
        throw new ConnectionException("Failed to connect to " + client.getClass().getSimpleName() +
                " after " + MAX_RETRIES + " attempts");
    }

    public void sendOrder(OrderBuilder orderBuilder) {
        orderClient.sendOrder(orderBuilder.withToken(new AuthTokens().getToken()));
    }

    public void cancelOrder(CancelOrderBuilder orderBuilder) {
        orderClient.cancelOrder(orderBuilder.withToken(new AuthTokens().getToken()));
    }

    private void subscribeToInstrument() {
        InstrumentSubscriptionBuilder instrumentSubscription = new InstrumentSubscriptionBuilder();
        dataClient.subscribeToInstrument(instrumentSubscription);
    }

    private void subscribeToTrade() {
        TradeSubscriptionBuilder tradeSubscription = new TradeSubscriptionBuilder("BTC/USD");
        dataClient.subscribeToTrade(tradeSubscription);
    }

    private void subscribeToOhlc() {
        OhlcSubscriptionBuilder ohlcSubscription = new OhlcSubscriptionBuilder("BTC/USD");
        ohlcSubscription.interval(1); // 1-second interval
        dataClient.subscribeToOhlc(ohlcSubscription);
    }

    private void subscribeToOrder() {
        OrderSubscriptionBuilder orderSubscription = new OrderSubscriptionBuilder("BTC/USD");
        dataAuthClient.subscribeToOrder(orderSubscription);
    }

    private void subscribeToBook() {
        BookSubscriptionBuilder bookSubscription = new BookSubscriptionBuilder("BTC/USD");
        dataClient.subscribeToBook(bookSubscription);
    }

    private void subscribeToTicker() {
        TickerSubscriptionBuilder tickerSubscription = new TickerSubscriptionBuilder("BTC/USD");
        dataClient.subscribeToTicker(tickerSubscription);
    }

    private void subscribeToBalances() {
        BalanceSubscriptionBuilder balanceSubscription = new BalanceSubscriptionBuilder();
        userClient.subscribeToBalances(balanceSubscription);
    }

    private void subscribeToExecution() {
        ExecutionSubscriptionBuilder executionSubscriptionBuilder = new ExecutionSubscriptionBuilder();
        userClient.subscribeToExecutions(executionSubscriptionBuilder);
    }
}