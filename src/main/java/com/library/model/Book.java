package com.library.model;

public class Book extends LibraryItem {

    private static final int MAX_LOAN_DAYS = 21;
    private static final double DAILY_LATE_FEE = 0.20;

    private final String author;

    public Book(String id, String title, String author) {
        super(id, title);
        if (author == null || author.isBlank()) {
            throw new IllegalArgumentException("Book author cannot be empty");
        }
        this.author = author;
    }

    @Override
    public int getMaxLoanDays() {
        return MAX_LOAN_DAYS;
    }

    @Override
    public double getDailyLateFee() {
        return DAILY_LATE_FEE;
    }

    @Override
    public String getType() {
        return "Book";
    }

    @Override
    public String getDetails() {
        return "by " + author;
    }

    public String getAuthor() {
        return author;
    }
}

