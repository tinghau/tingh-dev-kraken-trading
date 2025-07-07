package dev.tingh.admin.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class  Status {
    private String channel;
    private String type;
    private List<StatusItem> data;

    public String getChannel() {
        return channel;
    }

    public String getType() {
        return type;
    }

    public List<StatusItem> getData() {
        return data;
    }

    public static class StatusItem {
        @JsonProperty("connection_id")
        private String connectionId;
        @JsonProperty("api_version")
        private String apiVersion;
        private String version;
        private String system;

        public String getConnectionId() {
            return connectionId;
        }

        public String getApiVersion() {
            return apiVersion;
        }

        public String getVersion() {
            return version;
        }

        public String getSystem() {
            return system;
        }
    }
}