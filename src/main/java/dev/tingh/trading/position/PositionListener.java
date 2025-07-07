package dev.tingh.trading.position;

import java.util.List;

public interface PositionListener {
    void onPositionSnapshot(List<Position> position);

    void onPositionUpdate(Position position);
}
