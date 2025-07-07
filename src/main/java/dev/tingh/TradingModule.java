package dev.tingh;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import dev.tingh.admin.handler.StatusHandler;
import dev.tingh.client.*;
import dev.tingh.data.handler.*;
import dev.tingh.server.TradingWebSocketServer;
import dev.tingh.trading.*;
import dev.tingh.trading.position.FilePositionManager;
import dev.tingh.trading.position.PositionManager;

import java.net.URI;
import java.net.URISyntaxException;

public class TradingModule extends AbstractModule {

    public static final String STORAGE_DIRECTORY = "data/positions";
    public static final int MAX_POSITIONS_PER_SYMBOL = 1;
    public static final String BASE_DATA_DIRECTORY = "data";

    @Override
    protected void configure() {
        // Bind interfaces to implementations
        bind(PositionManager.class).toInstance(new FilePositionManager(STORAGE_DIRECTORY, MAX_POSITIONS_PER_SYMBOL));
        bind(OrderDispatcher.class).to(LoggingOrderDispatcher.class).in(Singleton.class);
        bind(SignalHandler.class).to(DefaultSignalHandler.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    public BookDataHandler provideBookDataHandler() {
        return new BookDataHandler(BASE_DATA_DIRECTORY);
    }

    @Provides
    @Singleton
    public InstrumentDataHandler provideInstrumentDataHandler() {
        return new InstrumentDataHandler(BASE_DATA_DIRECTORY);
    }

    @Provides
    @Singleton
    public OhlcDataHandler provideOhlcDataHandler() {
        return new OhlcDataHandler(BASE_DATA_DIRECTORY);
    }

    @Provides
    @Singleton
    public StatusHandler provideStatusHandler() {
        return new StatusHandler();
    }

    @Provides
    @Singleton
    public TickerDataHandler provideTickerDataHandler() {
        return new TickerDataHandler(BASE_DATA_DIRECTORY);
    }

    @Provides
    @Singleton
    public TradeDataHandler provideTradeDataHandler() {
        return new TradeDataHandler(BASE_DATA_DIRECTORY);
    }

    @Provides
    @Singleton
    public KrakenDataClient provideKrakenDataClient(
            BookDataHandler bookDataHandler,
            InstrumentDataHandler instrumentDataHandler,
            OhlcDataHandler ohlcDataHandler,
            StatusHandler statusHandler,
            TickerDataHandler tickerDataHandler,
            TradeDataHandler tradeDataHandler) throws URISyntaxException {

        URI serverUri = new URI("wss://ws.kraken.com/v2");
        return new KrakenDataClient(
                serverUri,
                bookDataHandler,
                instrumentDataHandler,
                ohlcDataHandler,
                statusHandler,
                tickerDataHandler,
                tradeDataHandler
        );
    }

    @Provides
    @Singleton
    public KrakenDataAuthClient provideKrakenDataAuthClient() throws URISyntaxException {
        URI serverUri = new URI("wss://ws-auth.kraken.com/v2");
        return new KrakenDataAuthClient(serverUri, BASE_DATA_DIRECTORY);
    }

    @Provides
    @Singleton
    public KrakenOrderClient provideKrakenOrderClient() throws URISyntaxException {
        URI serverUri = new URI("wss://ws-auth.kraken.com/v2");
        return new KrakenOrderClient(serverUri);
    }

    @Provides
    @Singleton
    public KrakenUserClient provideKrakenUserClient() throws URISyntaxException {
        URI serverUri = new URI("wss://ws-auth.kraken.com/v2");
        return new KrakenUserClient(serverUri, BASE_DATA_DIRECTORY);
    }

    @Provides
    @Singleton
    public KrakenClient provideKrakenClient(
            KrakenDataClient dataClient,
            KrakenDataAuthClient dataAuthClient,
            KrakenOrderClient orderClient,
            KrakenUserClient userClient,
            OrderDispatcher orderDispatcher) {

        KrakenClient krakenClient = new KrakenClient(
                dataClient,
                dataAuthClient,
                orderClient,
                userClient
        );

        krakenClient.configureOrderDispatcher(orderDispatcher);
        return krakenClient;
    }

    @Provides
    @Singleton
    public TradingWebSocketServer provideTradingWebSocketServer(
            SignalHandler signalHandler,
            OhlcDataHandler ohlcDataHandler,
            TickerDataHandler tickerDataHandler) {

        TradingWebSocketServer server = new TradingWebSocketServer(signalHandler);

        // Register the server as a consumer to OhlcDataHandler
        ohlcDataHandler.addOhlcDataListener(server::accept);
        tickerDataHandler.addTickerDataListener(server::accept);

        return server;
    }
}