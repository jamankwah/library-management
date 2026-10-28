package com.library.exception;

/** Thrown when a member with unpaid late fees tries to borrow. */
public class UnpaidFeesException extends LibraryException {

    public UnpaidFeesException(String message) {
        super(message);
    }
}