
package com.library.model;

public class Dvd extends LibraryItem {

    private static final int MAX_LOAN_DAYS = 3;
    private static final double DAILY_LATE_FEE = 1.00;
    // DVDs are expensive per day, so the library caps the total fee to stay fair.
    private static final double MAX_LATE_FEE = 15.00;

    private final int durationMinutes;

    public Dvd(String id, String title, int durationMinutes) {
        super(id, title);
        if (durationMinutes <= 0) {
            throw new IllegalArgumentException("Duration must be positive");
        }
        this.durationMinutes = durationMinutes;
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
        return "DVD";
    }

    @Override
    public String getDetails() {
        return durationMinutes + " min";
    }

    /** Reuses the parent's calculation, then applies the DVD cap. */
    @Override
    public double calculateLateFee(long daysLate) {
        return Math.min(super.calculateLateFee(daysLate), MAX_LATE_FEE);
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }
}
