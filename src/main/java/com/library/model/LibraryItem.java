package com.library.model;

import java.util.Objects;


public abstract class LibraryItem {

    // PART 1: ATTRIBUTES (the state)
    private final String id;
    private final String title;
    private boolean available;

    //  PART 2: CONSTRUCTOR
    protected LibraryItem(String id, String title) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Item id cannot be empty");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Item title cannot be empty");
        }
        this.id = id;
        this.title = title;
        this.available = true;
    }

    //  PART 3: ABSTRACT METHODS

    /** How many days the item can be kept before it is late. */
    public abstract int getMaxLoanDays();

    /** Fee charged for each day of delay. */
    public abstract double getDailyLateFee();

    /** Short label of the item type, used for display. */
    public abstract String getType();

    /** Type-specific information (author, issue number, duration...). */
    public abstract String getDetails();

    //  PART 4: SHARED BEHAVIOR (written once, used by all)

    public double calculateLateFee(long daysLate) {
        if (daysLate < 0) {
            throw new IllegalArgumentException("Days late cannot be negative: " + daysLate);
        }
        return daysLate * getDailyLateFee();
    }

    public void markAsBorrowed() {
        // The service checks availability first, so reaching this means a programming error.
        if (!available) {
            throw new IllegalStateException("Item " + id + " is already borrowed");
        }
        available = false;
    }

    public void markAsReturned() {
        if (available) {
            throw new IllegalStateException("Item " + id + " is not borrowed");
        }
        available = true;
    }

    //  PART 5: GETTERS (read-only access)

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public boolean isAvailable() {
        return available;
    }

    //  PART 6: METHODS INHERITED FROM Object

    // Two items are the same item if they have the same id (the title is not unique).
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof LibraryItem other)) {
            return false;
        }
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        String status = available ? "available" : "borrowed";
        return "[" + id + "] " + getType() + " - " + title + " (" + getDetails() + ") - " + status;
    }
}