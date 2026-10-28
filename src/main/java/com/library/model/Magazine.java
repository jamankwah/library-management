package com.library.model;

public class Magazine extends LibraryItem {

    private static final int MAX_LOAN_DAYS = 7;
    private static final double DAILY_LATE_FEE = 0.10;

    private final int issueNumber;

    public Magazine(String id, String title, int issueNumber) {
        super(id, title);
        if (issueNumber < 1) {
            throw new IllegalArgumentException("Issue number must be at least 1");
        }
        this.issueNumber = issueNumber;
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
        return "Magazine";
    }

    @Override
    public String getDetails() {
        return "issue #" + issueNumber;
    }

    public int getIssueNumber() {
        return issueNumber;
    }
}

