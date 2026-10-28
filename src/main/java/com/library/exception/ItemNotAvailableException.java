package com.library.exception;

/** Thrown when someone tries to borrow an item that is already on loan. */
public class ItemNotAvailableException extends LibraryException {

    public ItemNotAvailableException(String message) {
        super(message);
    }
}
