package dev.tingh.client;

import com.fasterxml.jackson.databind.JsonNode;
import dev.tingh.admin.handler.StatusHandler;
import dev.tingh.admin.model.Status;
import dev.tingh.data.handler.*;
import dev.tingh.data.model.*;
import dev.tingh.data.subscription.*;
import dev.tingh.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static dev.tingh.TradingModule.BASE_DATA_DIRECTORY;

public class KrakenDataClient extends KrakenBaseClient {

    private static final Logger logger = LoggerFactory.getLogger(KrakenDataClient.class);

    public static void main(String[] args) throws InterruptedException {
        // Example usage
        URI serverUri = URI.create("wss://ws.kraken.com/v2");
        KrakenDataClient client = new KrakenDataClient(serverUri,
                new BookDataHandler(BASE_DATA_DIRECTORY),
                new InstrumentDataHandler(BASE_DATA_DIRECTORY),
                new OhlcDataHandler(BASE_DATA_DIRECTORY),
                new StatusHandler(),
                new TickerDataHandler(BASE_DATA_DIRECTORY),
                new TradeDataHandler(BASE_DATA_DIRECTORY));

        // Connect to the server and subscribe to channels as needed
        client.connectBlocking(10, TimeUnit.SECONDS);

        OhlcSubscriptionBuilder ohlcSubscription = new OhlcSubscriptionBuilder("BTC/USD");
        ohlcSubscription.interval(1); // 1-second interval
        client.subscribeToOhlc(ohlcSubscription);
    }

    private final BookDataHandler bookDataHandler;
    private final InstrumentDataHandler instrumentDataHandler;
    private final OhlcDataHandler ohlcDataHandler;
    private final StatusHandler statusHandler;
    private final TickerDataHandler tickerDataHandler;
    private final TradeDataHandler tradeDataHandler;

    public KrakenDataClient(URI serverUri,
                            BookDataHandler bookDataHandler,
                            InstrumentDataHandler instrumentDataHandler,
                            OhlcDataHandler ohlcDataHandler,
                            StatusHandler statusHandler,
                            TickerDataHandler tickerDataHandler,
                            TradeDataHandler tradeDataHandler) {
        super(serverUri);
        this.bookDataHandler = bookDataHandler;
        this.instrumentDataHandler = instrumentDataHandler;
        this.ohlcDataHandler = ohlcDataHandler;
        this.statusHandler = statusHandler;
        this.tickerDataHandler = tickerDataHandler;
        this.tradeDataHandler = tradeDataHandler;

        logger.info("KrakenDataClient initialized with server URI: {}", serverUri);
    }

    public KrakenDataClient(URI serverUri, String baseDirectory) {
        super(serverUri);
        this.bookDataHandler = new BookDataHandler(baseDirectory);
        this.instrumentDataHandler = new InstrumentDataHandler(baseDirectory);
        this.ohlcDataHandler = new OhlcDataHandler(baseDirectory);
        this.statusHandler = new StatusHandler();
        this.tickerDataHandler = new TickerDataHandler(baseDirectory);
        this.tradeDataHandler = new TradeDataHandler(baseDirectory);
    }

    // Add message handler method
    @Override
    public void onMessage(String message) {
        try {
            JsonNode jsonNode = JsonUtils.getObjectMapper().readTree(message);
            if (!jsonNode.has("channel")) {
                logger.debug("Message does not contain type field: {}", message);
                return;
            }

            String channel = jsonNode.get("channel").asText();

            switch (channel) {
                case "book":
                    logger.info("Received message: {}", message);
                    bookDataHandler.handleBookData(JsonUtils.fromJson(message, BookData.class));
                    break;
                case "ticker":
                    logger.info("Received message: {}", message);
                    tickerDataHandler.handleTickerData(JsonUtils.fromJson(message, TickerData.class));
                    break;
                case "ohlc":
                    logger.info("Received message: {}", message);
                    ohlcDataHandler.handleOhlcData(JsonUtils.fromJson(message, OhlcData.class));
                    break;
                case "trade":
                    logger.info("Received message: {}", message);
                    tradeDataHandler.handleTradeData(JsonUtils.fromJson(message, TradeData.class));
                    break;
                case "instrument":
                    logger.info("Received message: {}", message);
                    instrumentDataHandler.handleInstrumentData(JsonUtils.fromJson(message, InstrumentData.class));
                    break;
                case "status":
                    logger.info("Received message: {}", message);
                    statusHandler.handleStatusData(JsonUtils.fromJson(message, Status.class));
                    break;
                case "heartbeat":
                    logger.debug("Received message: {}", message);
                    break;
                default:
                    logger.warn("Unknown message type: {}", channel);
                    break;
            }
        } catch (Exception e) {
            logger.error("Error processing message: {}", e.getMessage(), e);
        }
    }

    public void subscribeToTicker(TickerSubscriptionBuilder subscription) {
        send(JsonUtils.toJson(new HashMap<>(subscription.build())));
    }

    public void subscribeToBook(BookSubscriptionBuilder subscription) {
        send(JsonUtils.toJson(new HashMap<>(subscription.build())));
    }

    public void subscribeToOhlc(OhlcSubscriptionBuilder subscription) {
        send(JsonUtils.toJson(new HashMap<>(subscription.build())));
    }

    public void subscribeToTrade(TradeSubscriptionBuilder subscription) {
        send(JsonUtils.toJson(new HashMap<>(subscription.build())));
    }

    public void subscribeToInstrument(InstrumentSubscriptionBuilder subscription) {
        send(JsonUtils.toJson(new HashMap<>(subscription.build())));
    }
}
