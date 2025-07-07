package dev.tingh.client;

import dev.tingh.AuthTokens;
import dev.tingh.user.subscription.BalanceSubscriptionBuilder;
import dev.tingh.user.subscription.ExecutionSubscriptionBuilder;
import dev.tingh.user.handler.BalanceDataHandler;
import dev.tingh.user.handler.ExecutionDataHandler;
import dev.tingh.user.model.BalanceData;
import dev.tingh.user.model.ExecutionData;
import dev.tingh.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.HashMap;

public class KrakenUserClient extends KrakenBaseClient {

    private static final Logger logger = LoggerFactory.getLogger(KrakenUserClient.class);

    private final AuthTokens authTokens = new AuthTokens();

    private final BalanceDataHandler balanceDataHandler;
    private final ExecutionDataHandler executionDataHandler;

    public KrakenUserClient(URI serverUri, String baseDirectory) {
        super(serverUri);

        this.balanceDataHandler = new BalanceDataHandler(baseDirectory);
        this.executionDataHandler = new ExecutionDataHandler(baseDirectory);
    }

    @Override
    public void onMessage(String message) {
        logger.info("Received message: {}", message);

        if (message.contains("\"type\":") && (message.contains("\"balances\""))) {
            balanceDataHandler.handleBalancesData(JsonUtils.fromJson(message, BalanceData.class));
        } else if (message.contains("\"type\":") && message.contains("\"executions\"")) {
            executionDataHandler.handleExecutionData(JsonUtils.fromJson(message, ExecutionData.class));
        }
    }

    public void subscribeToExecutions(ExecutionSubscriptionBuilder subscription) {
        subscription.withToken(authTokens.getToken());
        send(JsonUtils.toJson(new HashMap<>(subscription.build())));
    }

    public void subscribeToBalances(BalanceSubscriptionBuilder subscription) {
        subscription.withToken(authTokens.getToken());
        send(JsonUtils.toJson(new HashMap<>(subscription.build())));
    }
}
