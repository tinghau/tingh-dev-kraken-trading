package dev.tingh;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import dev.tingh.client.KrakenClient;
import dev.tingh.server.TradingWebSocketServer;
import dev.tingh.trading.OrderDispatcher;
import dev.tingh.trading.position.PositionManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TradingApplicationManager {
    private static final Logger logger = LoggerFactory.getLogger(TradingApplicationManager.class);

    public static void main(String[] args) {
        configureLogging();

        // Create Guice injector with our module
        Injector injector = Guice.createInjector(
                new TradingModule()
        );

        // Get the properly configured TradingApplicationManager from Guice
        TradingApplicationManager manager = injector.getInstance(TradingApplicationManager.class);

        // Start the application
        manager.start();

        logger.info("Trading application started successfully");
    }

    private static void configureLogging() {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "info");
        System.setProperty("org.slf4j.simpleLogger.log.dev.tingh", "debug");
        System.setProperty("org.slf4j.simpleLogger.log.dev.tingh.client", "debug");
    }

    private final KrakenClient krakenClient;
    private final PositionManager positionManager;
    private final TradingWebSocketServer webSocketServer;

    @Inject
    public TradingApplicationManager(
            KrakenClient krakenClient,
            TradingWebSocketServer webSocketServer,
            PositionManager positionManager,
            OrderDispatcher orderDispatcher
            ) {
        this.positionManager = positionManager;
        this.webSocketServer = webSocketServer;
        this.krakenClient = krakenClient;

        krakenClient.configureOrderDispatcher(orderDispatcher);
    }

//    public TradingApplicationManager(String storageDir, int maxPositionsPerSymbol)
//            throws URISyntaxException {
//        // Initialize position manager with storage location
//        this.positionManager = new FilePositionManager(storageDir, maxPositionsPerSymbol);
//
//        // Initialize KrakenClient
//        KrakenClient krakenClient = new KrakenClient();
//
//        // Create OrderManager that will be configured by KrakenClient
//        OrderManager orderManager = new OrderManager();
//
//        // Have KrakenClient configure the OrderManager (internal implementation hidden)
//        krakenClient.configureOrderManager(orderManager);
//        krakenClient.start();
//
//        // Create SignalHandler with orderManager and positionManager
//        DefaultSignalHandler signalHandler = new DefaultSignalHandler(orderManager, positionManager);
//
//        // Initialize TradingWebSocketServer with SignalHandler
//        this.webSocketServer = new TradingWebSocketServer(signalHandler);
//
//    }

    public void start() {
        logger.info("Starting Trading Application");
        krakenClient.start();
        webSocketServer.start();

        // You might want to add shutdown hooks here
        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
    }

    public void stop() {
        logger.info("Stopping Trading Application");
        try {
            webSocketServer.stop();
            // Save positions before shutdown
            positionManager.savePositions();
        } catch (Exception e) {
            logger.error("Error stopping application", e);
        }
    }
}