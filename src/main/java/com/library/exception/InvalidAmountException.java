package com.library.exception;

/** Unchecked: an invalid amount is a wrong argument, it should be fixed by the caller. */
public class InvalidAmountException extends RuntimeException {

    public InvalidAmountException(String message) {
        super(message);
    }
}

