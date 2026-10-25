package com.mrs.exception;

public class SeatUnavailableException extends RuntimeException {
    public SeatUnavailableException(String message) { super(message); }
}
