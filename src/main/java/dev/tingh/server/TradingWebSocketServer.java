package dev.tingh.server;

import com.fasterxml.jackson.databind.JsonNode;
import dev.tingh.data.model.OhlcData;
import dev.tingh.data.model.TickerData;
import dev.tingh.trading.LoggingSignalHandler;
import dev.tingh.trading.SignalHandler;
import dev.tingh.trading.model.Subscribe;
import dev.tingh.trading.model.TradeSignal;
import dev.tingh.util.JsonUtils;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class TradingWebSocketServer extends WebSocketServer {
    private static final Logger logger = LoggerFactory.getLogger(TradingWebSocketServer.class);

    public static void main(String[] args) {
        TradingWebSocketServer server = new TradingWebSocketServer(new LoggingSignalHandler());
        server.start();
        logger.info("WebSocket server started on port {}", server.getPort());
    }

    private final Map<WebSocket, Set<String>> clientSubscriptions = new ConcurrentHashMap<>();
    private final SignalHandler signalHandler;

    public TradingWebSocketServer(SignalHandler signalHandler) {
        super(new InetSocketAddress("localhost", 8080));
        this.signalHandler = signalHandler;
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake handshake) {
        logger.info("New connection established from: {}", webSocket.getRemoteSocketAddress());
        clientSubscriptions.put(webSocket, ConcurrentHashMap.newKeySet());
    }

    @Override
    public void onClose(WebSocket webSocket, int code, String reason, boolean remote) {
        logger.info("Connection closed: {} with code: {}, reason: {}, remote: {}",
                webSocket.getRemoteSocketAddress(), code, reason, remote);
        clientSubscriptions.remove(webSocket);
    }

    @Override
    public void onMessage(WebSocket webSocket, String message) {
        try {
            logger.info("Received message from {}: {}", webSocket.getRemoteSocketAddress(), message);
            JsonNode jsonNode = JsonUtils.getObjectMapper().readTree(message);
            String type = jsonNode.get("type").asText();

            if (type == null) {
                sendError(webSocket, "Missing 'type' field in message");
                return;
            }

            switch (type) {
                case "subscribe":
                    Subscribe subscribe = JsonUtils.fromJson(message, Subscribe.class);
                    handleSubscribe(webSocket, subscribe);
                    break;
                case "signal":
                    TradeSignal signal = JsonUtils.fromJson(message, TradeSignal.class);
                    handleSignal(signal);
                    break;
                default:
                    sendError(webSocket, "Unknown message type: " + type);
            }
        } catch (Exception e) {
            logger.error("Error processing message: {}", e.getMessage(), e);
            sendError(webSocket, "Failed to process message: " + e.getMessage());
        }
    }

    private void handleSubscribe(WebSocket webSocket, Subscribe subscribe) {
        String symbol = subscribe.getSymbol();
        if (symbol == null) {
            sendError(webSocket, "Missing 'symbol' field in subscribe message");
            return;
        }

        clientSubscriptions.get(webSocket).add(symbol);
        sendSuccess(webSocket, "Subscribed to " + symbol);
    }

    private void handleSignal(TradeSignal signal) {
        try {
            signalHandler.process(signal);
            logger.info("Processed trading signal: {}", signal);
        } catch (Exception e) {
            logger.error("Error processing trading signal: {}", e.getMessage(), e);
        }
    }

    private void sendError(WebSocket conn, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", message);
        conn.send(JsonUtils.toJson(response));
    }

    private void sendSuccess(WebSocket conn, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", message);
        conn.send(JsonUtils.toJson(response));
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        logger.error("Error in WebSocket server: {}", ex.getMessage(), ex);
        if (conn != null) {
            clientSubscriptions.remove(conn);
        }
    }

    @Override
    public void onStart() {
        String serverUrl = "ws://" + getAddress().getHostString() + ":" + getPort();
        logger.info("WebSocket server started at URL: {}", serverUrl);
    }

    public void accept(TickerData tickerData) {
        if (tickerData == null || tickerData.getData() == null) {
            return;
        }

        String tickerJson = JsonUtils.toJson(tickerData);

        for (Map.Entry<WebSocket, Set<String>> entry : clientSubscriptions.entrySet()) {
            WebSocket client = entry.getKey();
            Set<String> symbols = entry.getValue();

            for (dev.tingh.data.model.TickerData.TickerSymbolData symbolData : tickerData.getData()) {
                if (symbols.contains(symbolData.getSymbol())) {
                    client.send(tickerJson);
                    break; // Send only once per client per update
                }
            }
        }
    }

    public void accept(OhlcData ohlcData) {
        if (ohlcData == null || ohlcData.getData() == null) {
            return;
        }

        // Forward OHLC data to subscribed clients
        String ohlcJson = JsonUtils.toJson(ohlcData);

        for (Map.Entry<WebSocket, Set<String>> entry : clientSubscriptions.entrySet()) {
            WebSocket client = entry.getKey();
            Set<String> symbols = entry.getValue();

            // Check if this client is subscribed to any of the symbols in this update
            for (OhlcData.OhlcSymbolData symbolData : ohlcData.getData()) {
                if (symbols.contains(symbolData.getSymbol())) {
                    client.send(ohlcJson);
                    break;  // Send only once per client even if multiple symbols match
                }
            }
        }
    }
}