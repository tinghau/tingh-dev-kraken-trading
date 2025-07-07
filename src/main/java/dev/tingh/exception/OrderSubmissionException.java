package dev.tingh.exception;

public class OrderSubmissionException extends RuntimeException {
    public OrderSubmissionException(String message) {
        super(message);
    }

    public OrderSubmissionException(String message, Throwable cause) {
        super(message, cause);
    }
}