package dev.tingh.trading.model;

// Create POJOs for the response structure
class CancelOrderResponse {
    private String method;
    private long req_id;
    private OrderResult result;
    private boolean success;
    private String time_in;
    private String time_out;
    private String error;

    public String getMethod() {
        return method;
    }

    public long getReqId() {
        return req_id;
    }

    public OrderResult getResult() {
        return result;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getTimeIn() {
        return time_in;
    }

    public String getTimeOut() {
        return time_out;
    }

    public String getError() {
        return error;
    }
}

class OrderResult {
    private String order_id;
    private long order_userref;

    public String getOrderId() {
        return order_id;
    }

    public long getOrderUserref() {
        return order_userref;
    }
}
