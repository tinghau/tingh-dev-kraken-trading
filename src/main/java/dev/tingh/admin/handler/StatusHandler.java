package dev.tingh.admin.handler;

import dev.tingh.admin.model.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatusHandler {
    private static final Logger logger = LoggerFactory.getLogger(dev.tingh.admin.handler.StatusHandler.class);

    public void handleStatusData(Status statusData) {
        try {
            if (statusData == null || statusData.getData() == null) {
                logger.warn("Received null or empty status data");
                return;
            }

            for (Status.StatusItem item : statusData.getData()) {
                logger.info("Status update: connectionId={}, version={}, system={}, apiVersion={}",
                        item.getConnectionId(),
                        item.getVersion(),
                        item.getSystem(),
                        item.getApiVersion());
            }
        } catch (Exception e) {
            logger.error("Error handling status data: {}", e.getMessage(), e);
        }
    }
}