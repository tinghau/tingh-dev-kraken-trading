package dev.tingh.client;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

public abstract class KrakenBaseClient extends WebSocketClient {

    private static final Logger logger = LoggerFactory.getLogger(KrakenBaseClient.class);

    public KrakenBaseClient(URI serverUri) {
        super(serverUri);
    }

    // Common connection/auth methods

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        logger.info("Connected to Kraken WebSocket API");
    }

    @Override
    public void onMessage(String message) {
        logger.info("Received message: {}", message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        logger.info("Connection closed: code={}, reason={}", code, reason);
    }

    @Override
    public void onError(Exception ex) {
        logger.error("Error occurred: ", ex);
    }
}