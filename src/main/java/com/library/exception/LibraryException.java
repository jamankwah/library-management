package com.library.exception;

/**
 * Parent of all business errors (checked): the caller is expected to handle them,
 * typically by explaining to the user why the operation was refused.
 */
public class LibraryException extends Exception {

    public LibraryException(String message) {
        super(message);
    }
}
