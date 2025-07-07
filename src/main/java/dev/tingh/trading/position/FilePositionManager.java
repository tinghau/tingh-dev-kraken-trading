package dev.tingh.trading.position;

import com.fasterxml.jackson.core.type.TypeReference;
import dev.tingh.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static dev.tingh.trading.position.PositionStatus.OPEN;

public class FilePositionManager implements PositionManager {
    private static final Logger logger = LoggerFactory.getLogger(FilePositionManager.class);

    private final List<PositionListener> listeners = new ArrayList<>();
    private final Map<String, Position> positions = new ConcurrentHashMap<>();
    private final String storageFilePath;
    private final int maxPositionsPerSymbol;

    public FilePositionManager(String storageDirectory, int maxPositionsPerSymbol) {
        this.storageFilePath = storageDirectory + "/positions.json";
        this.maxPositionsPerSymbol = maxPositionsPerSymbol;
        loadPositions();
    }

    @Override
    public void addListener(PositionListener listener) {
        listeners.add(listener);
        listener.onPositionSnapshot(new ArrayList<>(positions.values()));
    }

    @Override
    public void openPosition(String symbol, String orderRef, BigDecimal quantity, BigDecimal entryPrice, BigDecimal exitPrice, LocalDateTime exitTime) {
        if (isPositionLimitReached(symbol)) {
            logger.warn("Position limit reached for symbol: {}", symbol);
            return;
        }

        Position position = new Position(symbol, orderRef, quantity,  entryPrice, exitPrice, exitTime);
        positions.put(position.getId(), position);
        for (PositionListener listener : listeners) {
            listener.onPositionUpdate(position);
        }
        savePositions();
    }

    @Override
    public void closePosition(String positionId, String exitPrice) {
        Position position = positions.get(positionId);
        if (position != null) {
            position.close();
            savePositions();
        }
    }

    @Override
    public Optional<Position> getPosition(String positionId) {
        return Optional.ofNullable(positions.get(positionId));
    }

    @Override
    public List<Position> getOpenPositions() {
        return positions.values().stream()
                .filter(p -> OPEN.equals(p.getStatus()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Position> getPositionsForSymbol(String symbol) {
        return positions.values().stream()
                .filter(p -> p.getSymbol().equals(symbol))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isPositionLimitReached(String symbol) {
        long openPositionsCount = positions.values().stream()
                .filter(p -> p.getSymbol().equals(symbol))
                .filter(p -> OPEN.equals(p.getStatus()))
                .count();

        return openPositionsCount >= maxPositionsPerSymbol;
    }

    @Override
    public void savePositions() {
        try {
            // Convert positions to list of maps for serialization
            List<Map<String, Object>> positionMaps = positions.values().stream()
                    .map(Position::toMap)
                    .collect(Collectors.toList());

            String json = JsonUtils.toJson(positionMaps);
            Path path = Path.of(storageFilePath);
            Files.createDirectories(path.getParent());
            Files.writeString(path, json);
            logger.info("Positions saved to: {}", storageFilePath);
        } catch (IOException e) {
            logger.error("Failed to save positions: {}", e.getMessage(), e);
        }
    }

    @Override
    public void loadPositions() {
        File file = new File(storageFilePath);
        if (!file.exists()) {
            logger.info("No position file found at: {}", storageFilePath);
            return;
        }

        try {
            String json = Files.readString(Path.of(storageFilePath));
            if (json.isEmpty()) {
                logger.info("Position file is empty: {}", storageFilePath);
                return;
            }
            TypeReference<List<Map<String, Object>>> typeReference = new TypeReference<>() {};
            List<Map<String, Object>> positionMaps = JsonUtils.fromJson(json, typeReference);

            for (Map<String, Object> map : positionMaps) {
                Position position = Position.fromMap(map);
                positions.put(position.getId(), position);
            }

            logger.info("Loaded {} positions", positions.size());
        } catch (IOException e) {
            logger.error("Failed to load positions: {}", e.getMessage(), e);
        }
    }
}