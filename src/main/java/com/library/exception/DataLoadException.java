package com.library.exception;

/** Thrown when a data file cannot be read at all. Keeps the original IOException as its cause. */
public class DataLoadException extends Exception {

    public DataLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
