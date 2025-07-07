package dev.tingh.trading;

import dev.tingh.trading.model.TradeSignal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingSignalHandler implements SignalHandler {

    private final Logger logger = LoggerFactory.getLogger(LoggingSignalHandler.class);

    @Override
    public void process(TradeSignal signal) {
        String action = signal.getAction();
        String symbol = signal.getSymbol();

        System.out.println("Processing signal: " + action + " for " + symbol);
    }
}
