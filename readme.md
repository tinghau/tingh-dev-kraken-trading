# Kraken Trading

A Java application that connects to the Kraken WebSocket API v2. 

The application subscribes to real-time market data and supports order submission and cancellation to the venue.
In addition, it provides a WebSocket server for clients to receive market data and send trading signals, e.g. a Python client.
Rudimentary position management is also included, exiting positions given desired conditions.
  
## Features

- Connect to Kraken WebSocket API v2
- Subscribe to market data channels:
    - Ticker
    - Book (Order Book)
    - Trades
    - OHLC (Candles)
    - Instruments (Assets and Pairs)
- Subscribe to authenticated channels:
    - Orders
    - Balances
    - Executions
- Add, amend and cancel orders
- Write received data to CSV files
- Websocket server for clients:
   - forward market data to clients, and
   - accept trading signals from clients.
- Provides rudimentary position management, exiting positions given desired conditions.

## Setup

### Dependencies

Add the following dependencies to your project:

```gradle
dependencies {
    implementation("com.fasterxml.jackson.core:jackson-core:2.18.3")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.18.3")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.18.3")
    implementation("com.google.inject:guice:7.0.0")
    implementation("org.java-websocket:Java-WebSocket:1.5.2")
    implementation("ch.qos.logback:logback-classic:1.5.13")
}
```

### Configuration
Create a base directory for data storage:

```java
private static final String BASE_DIRECTORY;
```

Add the Kraken private and public API keys to AuthTokens:
```java
private static final String PUBLIC_KEY;
private static final String PRIVATE_KEY;
```

### WebSocket Client 
The WebSocket client should connect to ws://localhost:8080.
A subscription message looks like this:
```json
{
  "type": "subscribe",
  "symbol": "BTC/USD"
}
```
A successful subscription will return ohlc and ticker data for the specified symbol.

To submit a trading signal, send a message like this:
```json
{
  "type": "signal",
  "action": "entry",
  "symbol": "BTC/USD",
  "timestamp": "2025-07-06T20:57:56",
  "price": 108746.5,
  "targetPrice": 112008.895,
  "targetReturn": 3.0,
  "holdTimeHours": 72,
  "orderQty": "1.0"
}
```

### Run the Application

```commandLine
./gradlew build run
```

### Pending
- Currently submitted orders go to the LoggingOrderDispatcher, which only logs the orders.
- This should be replaced with a real order dispatcher DefaultOrderDispatcher that sends orders to the Kraken venue - requires testing.
- Move to a scaled long representation for prices and quantities instead of BigDecimal.

### Further
Blog post: https://tingh.dev/2025/06/03/kraken-crypto-trading-app.html