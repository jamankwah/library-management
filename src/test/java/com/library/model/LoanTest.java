package com.library.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoanTest {

    private static final LocalDate BORROW_DATE = LocalDate.of(2026, 10, 1);

    private Loan bookLoan;

    @BeforeEach
    void setUp() {
        Member member = new Member("U001", "Alice Martin", MembershipType.STANDARD);
        LibraryItem book = new Book("B001", "Clean Code", "Robert C. Martin");
        bookLoan = new Loan("L001", book, member, BORROW_DATE);
    }

    // ===== Normal cases =====

    @Test
    void bookIsDueTwentyOneDaysAfterBorrowing() {
        assertEquals(LocalDate.of(2026, 10, 22), bookLoan.getDueDate());
    }

    @Test
    void dvdIsDueThreeDaysAfterBorrowing() {
        Member member = new Member("U001", "Alice Martin", MembershipType.STANDARD);
        Loan dvdLoan = new Loan("L002", new Dvd("D001", "Inception", 148), member, BORROW_DATE);

        assertEquals(LocalDate.of(2026, 10, 4), dvdLoan.getDueDate());
    }

    @Test
    void lateReturnIsCharged() {
        // Due on 22 October, returned on 27 October: 5 days late x 0.20 EUR
        LocalDate returnDate = LocalDate.of(2026, 10, 27);

        assertEquals(5, bookLoan.getDaysLate(returnDate));
        assertEquals(1.00, bookLoan.calculateLateFee(returnDate), 0.001);
    }

    @Test
    void returnedLoanIsNoLongerActive() {
        bookLoan.markAsReturned(LocalDate.of(2026, 10, 10));

        assertFalse(bookLoan.isActive());
        assertTrue(bookLoan.getReturnDate().isPresent());
    }

    // ===== Edge cases =====

    @Test
    void returningOnTheDueDateIsNotLate() {
        LocalDate dueDate = LocalDate.of(2026, 10, 22);

        assertFalse(bookLoan.isOverdue(dueDate));
        assertEquals(0, bookLoan.getDaysLate(dueDate));
    }

    @Test
    void oneDayAfterTheDueDateIsOverdue() {
        assertTrue(bookLoan.isOverdue(LocalDate.of(2026, 10, 23)));
    }

    @Test
    void earlyReturnIsNeverNegativeLate() {
        assertEquals(0, bookLoan.getDaysLate(LocalDate.of(2026, 10, 5)));
    }

    @Test
    void newLoanHasNoReturnDate() {
        assertTrue(bookLoan.getReturnDate().isEmpty());
    }

    // ===== Error cases =====

    @Test
    void returnDateBeforeBorrowDateIsRefused() {
        assertThrows(IllegalArgumentException.class,
                () -> bookLoan.markAsReturned(LocalDate.of(2026, 9, 30)));
    }

    @Test
    void returningTwiceIsRefused() {
        bookLoan.markAsReturned(LocalDate.of(2026, 10, 10));

        assertThrows(IllegalStateException.class,
                () -> bookLoan.markAsReturned(LocalDate.of(2026, 10, 11)));
    }
}
