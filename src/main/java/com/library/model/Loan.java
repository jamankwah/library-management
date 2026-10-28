package com.library.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

/**
 * Links a member to an item for a period of time.
 * The due date is computed from the item type, so a Book and a DVD get different deadlines.
 */
public class Loan {

    private final String id;
    private final LibraryItem item;
    private final Member member;
    private final LocalDate borrowDate;
    private final LocalDate dueDate;
    private LocalDate returnDate;

    public Loan(String id, LibraryItem item, Member member, LocalDate borrowDate) {
        if (item == null || member == null || borrowDate == null) {
            throw new IllegalArgumentException("Item, member and borrow date are required");
        }
        this.id = id;
        this.item = item;
        this.member = member;
        this.borrowDate = borrowDate;
        this.dueDate = borrowDate.plusDays(item.getMaxLoanDays());
    }

    public void markAsReturned(LocalDate date) {
        if (returnDate != null) {
            throw new IllegalStateException("Loan " + id + " is already returned");
        }
        if (date.isBefore(borrowDate)) {
            throw new IllegalArgumentException("Return date cannot be before borrow date " + borrowDate);
        }
        this.returnDate = date;
    }

    public boolean isActive() {
        return returnDate == null;
    }

    public boolean isOverdue(LocalDate today) {
        return isActive() && today.isAfter(dueDate);
    }

    public long getDaysLate(LocalDate date) {
        return Math.max(0, ChronoUnit.DAYS.between(dueDate, date));
    }

    /** Delegates to the item: the fee rule depends on the actual item type (polymorphism). */
    public double calculateLateFee(LocalDate date) {
        return item.calculateLateFee(getDaysLate(date));
    }

    public String getId() {
        return id;
    }

    public LibraryItem getItem() {
        return item;
    }

    public Member getMember() {
        return member;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public Optional<LocalDate> getReturnDate() {
        return Optional.ofNullable(returnDate);
    }

    @Override
    public String toString() {
        return id + ": \"" + item.getTitle() + "\" borrowed by " + member.getName()
                + " on " + borrowDate + ", due " + dueDate;
    }
}
