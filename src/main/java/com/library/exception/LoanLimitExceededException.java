package com.library.exception;

/** Thrown when a member already has the maximum number of loans allowed by their membership. */
public class LoanLimitExceededException extends LibraryException {

    public LoanLimitExceededException(String message) {
        super(message);
    }
}
