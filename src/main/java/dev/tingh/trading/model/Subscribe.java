package dev.tingh.trading.model;

public class Subscribe {
    private String type;
    private String symbol;

    // Default constructor for Jackson
    public Subscribe() {
        this.type = "subscribe";
    }

    // Constructor with symbol
    public Subscribe(String symbol) {
        this.type = "subscribe";
        this.symbol = symbol;
    }

    // Getters and setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return "Subscribe{" +
                "type='" + type + '\'' +
                ", symbol='" + symbol + '\'' +
                '}';
    }
}