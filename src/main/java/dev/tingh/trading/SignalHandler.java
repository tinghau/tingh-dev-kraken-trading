package dev.tingh.trading;

import dev.tingh.trading.model.TradeSignal;

public interface SignalHandler {
    void process(TradeSignal signal);
}
