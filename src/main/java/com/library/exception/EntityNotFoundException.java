package com.library.exception;

/** Thrown when an item, member or loan cannot be found. */
public class EntityNotFoundException extends LibraryException {

    public EntityNotFoundException(String message) {
        super(message);
    }
}