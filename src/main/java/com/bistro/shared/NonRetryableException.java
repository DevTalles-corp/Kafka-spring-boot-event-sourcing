package com.bistro.shared;

public class NonRetryableException extends RuntimeException {
    public NonRetryableException(String message, Throwable e) {
        super(message, e);
    }
}
