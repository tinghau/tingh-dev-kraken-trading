package dev.tingh.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.tingh.admin.subscription.PingSubscriptionBuilder;
import dev.tingh.admin.model.Pong;
import dev.tingh.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


public class KrakenAdminClient extends KrakenBaseClient {

    private static final Logger logger = LoggerFactory.getLogger(KrakenAdminClient.class);

    public KrakenAdminClient(URI serverUri, String apiKey, String apiSecret) {
        super(serverUri);
    }

    public CompletableFuture<Pong> ping(PingSubscriptionBuilder pingSubscription) {
        CompletableFuture<Pong> pongFuture = new CompletableFuture<>();
        Map<String, Object> pingMap = pingSubscription.build();
        String reqid = (String) pingMap.getOrDefault("reqid", "");

        addMessageHandler(message -> {
            Pong pong = JsonUtils.fromJson(message, Pong.class);
            if ("pong".equals(pong.getMethod()) &&
                    (reqid.isEmpty() || reqid.equals(pong.getReqid()))) {
                logger.info("Received pong response: " + message);
                pongFuture.complete(pong);
                return true; // Remove this handler after processing
            }
            return false; // Keep this handler for future messages
        });

        send(JsonUtils.toJson(pingMap));
        return pongFuture;
    }

    /**
     * Add a message handler that processes incoming messages
     * @param handler function that returns true if handler should be removed after processing
     */
    private void addMessageHandler(MessageHandler handler) {
        // This method would be implemented in KrakenBaseClient
        // For now, we'll assume it handles adding message processing callbacks
    }

    @FunctionalInterface
    interface MessageHandler {
        boolean handleMessage(String message) throws JsonProcessingException;
    }
}

