package com.library.exception;

/** Thrown when a line of a data file is malformed. */
public class InvalidDataException extends Exception {

    public InvalidDataException(String message) {
        super(message);
    }
}
