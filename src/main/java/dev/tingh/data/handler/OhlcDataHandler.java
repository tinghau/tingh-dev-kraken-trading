package dev.tingh.data.handler;

import dev.tingh.data.model.OhlcData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

public class OhlcDataHandler {
    private static final Logger logger = LoggerFactory.getLogger(OhlcDataHandler.class);

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    private final DateTimeFormatter timestampFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private final List<Consumer<OhlcData>> ohlcDataListeners = new CopyOnWriteArrayList<>();
    private final Lock fileLock = new ReentrantLock();

    private final String baseDirectory;

    private LocalDate currentFileDate;

    public OhlcDataHandler(String baseDirectory) {
        this.baseDirectory = baseDirectory;
        this.currentFileDate = LocalDate.now();

        // Create directory if it doesn't exist
        try {
            Files.createDirectories(Paths.get(baseDirectory));
        } catch (IOException e) {
            throw new RuntimeException("Failed to create directory for OHLC data", e);
        }
    }

    // Method to add an OHLC data listener
    public void addOhlcDataListener(Consumer<OhlcData> listener) {
        ohlcDataListeners.add(listener);
    }

    // Method to remove an OHLC data listener
    public boolean removeOhlcDataListener(Consumer<OhlcData> listener) {
        return ohlcDataListeners.remove(listener);
    }

    private void notifyOhlcDataListeners(OhlcData ohlcData) {
        for (Consumer<OhlcData> listener : ohlcDataListeners) {
            try {
                listener.accept(ohlcData);
            } catch (Exception e) {
                logger.error("Error in OHLC data listener: {}", e.getMessage(), e);
            }
        }
    }

    // New method that accepts OhlcData object
    public void handleOhlcData(OhlcData ohlcData) {
        try {
            if (ohlcData == null || ohlcData.getData() == null) {
                return;
            }

            // Process each symbol's data
            for (OhlcData.OhlcSymbolData symbolData : ohlcData.getData()) {
                writeOhlcDataToCsv(symbolData, ohlcData.getType());
            }

            notifyOhlcDataListeners(ohlcData);
        } catch (Exception e) {
            logger.error("Error processing OHLC data: {}", e.getMessage(), e);
        }
    }

    private void writeOhlcDataToCsv(OhlcData.OhlcSymbolData symbolData, String updateType) {
        LocalDate today = LocalDate.now();

        try {
            fileLock.lock();

            // Check if we need to roll to a new file (new day)
            if (!today.equals(currentFileDate)) {
                currentFileDate = today;
            }

            // Create the file path with date-based naming, include interval in filename
            int interval = symbolData.getInterval();
            String fileName = symbolData.getSymbol().replace("/", "_") + "_ohlc_" + interval + "_" + currentFileDate.format(dateFormatter) + ".csv";
            Path filePath = Paths.get(baseDirectory, fileName);

            // Create headers if file doesn't exist
            boolean fileExists = Files.exists(filePath);
            if (!fileExists) {
                String headers = "local_timestamp,symbol,update_type,open,high,low,close,trades,volume,vwap,interval_begin,interval,timestamp\n";
                Files.writeString(filePath, headers, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            }

            // Format the data rows
            String dataRows = getString(symbolData, updateType);
            // Append the data to the file
            Files.writeString(filePath, dataRows, StandardOpenOption.APPEND);
        } catch (IOException e) {
            logger.error("Error writing OHLC data to CSV: {}", e.getMessage(), e);
        } finally {
            fileLock.unlock();
        }
    }

    private String getString(OhlcData.OhlcSymbolData symbolData, String updateType) {
        String localTimestamp = LocalDateTime.now().format(timestampFormatter);

        return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
                localTimestamp,
                symbolData.getSymbol(),
                updateType,
                symbolData.getOpen(),
                symbolData.getHigh(),
                symbolData.getLow(),
                symbolData.getClose(),
                symbolData.getTrades(),
                symbolData.getVolume(),
                symbolData.getVwap(),
                symbolData.getIntervalBegin(),
                symbolData.getInterval(),
                symbolData.getTimestamp()
        );
    }
}
