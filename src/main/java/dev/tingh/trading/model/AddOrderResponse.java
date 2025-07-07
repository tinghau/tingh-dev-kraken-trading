package dev.tingh.trading.model;


import com.fasterxml.jackson.annotation.JsonProperty;

public class AddOrderResponse {
    private String method;

    @JsonProperty("req_id")
    private long reqId;

    private OrderResult result;
    private boolean success;

    @JsonProperty("time_in")
    private String timeIn;

    @JsonProperty("time_out")
    private String timeOut;

    // Optional error field
    private String error;

    public static class OrderResult {
        @JsonProperty("order_id")
        private String orderId;

        @JsonProperty("order_userref")
        private long orderUserref;

        public String getOrderId() {
            return orderId;
        }

        public long getOrderUserref() {
            return orderUserref;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public void setOrderUserref(long orderUserref) {
            this.orderUserref = orderUserref;
        }
    }

    public String getMethod() {
        return method;
    }

    public long getReqId() {
        return reqId;
    }

    public OrderResult getResult() {
        return result;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getTimeIn() {
        return timeIn;
    }

    public String getTimeOut() {
        return timeOut;
    }

    public String getError() {
        return error;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setReqId(long reqId) {
        this.reqId = reqId;
    }

    public void setResult(OrderResult result) {
        this.result = result;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setTimeIn(String timeIn) {
        this.timeIn = timeIn;
    }

    public void setTimeOut(String timeOut) {
        this.timeOut = timeOut;
    }

    public void setError(String error) {
        this.error = error;
    }
}