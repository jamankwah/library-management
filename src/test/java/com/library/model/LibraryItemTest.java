package com.library.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LibraryItemTest {

    //  Normal cases

    @Test
    void bookLateFeeIsTwentyCentsPerDay() {
        // Arrange
        LibraryItem book = new Book("B001", "Clean Code", "Robert C. Martin");

        // Act
        double fee = book.calculateLateFee(5);

        // Assert
        assertEquals(1.00, fee, 0.001);
    }

    @Test
    void magazineLateFeeIsTenCentsPerDay() {
        LibraryItem magazine = new Magazine("M001", "Wired", 31);

        assertEquals(0.50, magazine.calculateLateFee(5), 0.001);
    }

    @Test
    void dvdLateFeeIsOneEuroPerDay() {
        LibraryItem dvd = new Dvd("D001", "Inception", 148);

        assertEquals(5.00, dvd.calculateLateFee(5), 0.001);
    }

    @Test
    void eachItemTypeHasItsOwnLoanPeriod() {
        // Polymorphism: same method call, different result depending on the real type
        List<LibraryItem> items = List.of(
                new Book("B001", "Clean Code", "Robert C. Martin"),
                new Magazine("M001", "Wired", 31),
                new Dvd("D001", "Inception", 148));

        assertEquals(21, items.get(0).getMaxLoanDays());
        assertEquals(7, items.get(1).getMaxLoanDays());
        assertEquals(3, items.get(2).getMaxLoanDays());
    }

    @Test
    void borrowedItemIsNoLongerAvailable() {
        LibraryItem book = new Book("B001", "Clean Code", "Robert C. Martin");

        book.markAsBorrowed();

        assertFalse(book.isAvailable());
    }

    @Test
    void returnedItemIsAvailableAgain() {
        LibraryItem book = new Book("B001", "Clean Code", "Robert C. Martin");
        book.markAsBorrowed();

        book.markAsReturned();

        assertTrue(book.isAvailable());
    }

    //  Edge cases

    @Test
    void noLateDaysMeansNoFee() {
        LibraryItem book = new Book("B001", "Clean Code", "Robert C. Martin");

        assertEquals(0.00, book.calculateLateFee(0), 0.001);
    }

    @Test
    void dvdFeeIsCappedAtFifteenEuros() {
        LibraryItem dvd = new Dvd("D001", "Inception", 148);

        assertEquals(15.00, dvd.calculateLateFee(30), 0.001);
    }

    @Test
    void dvdFeeReachesTheCapExactlyAtFifteenDays() {
        LibraryItem dvd = new Dvd("D001", "Inception", 148);

        assertEquals(15.00, dvd.calculateLateFee(15), 0.001);
    }

    @Test
    void itemsWithSameIdAreEqual() {
        LibraryItem first = new Book("B001", "Clean Code", "Robert C. Martin");
        LibraryItem second = new Book("B001", "Another title", "Someone else");

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    //  Error cases

    @Test
    void negativeLateDaysAreRefused() {
        LibraryItem book = new Book("B001", "Clean Code", "Robert C. Martin");

        assertThrows(IllegalArgumentException.class, () -> book.calculateLateFee(-1));
    }

    @Test
    void itemWithEmptyTitleCannotBeCreated() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Book("B001", "", "Robert C. Martin"));

        assertEquals("Item title cannot be empty", exception.getMessage());
    }

    @Test
    void magazineWithIssueNumberZeroCannotBeCreated() {
        assertThrows(IllegalArgumentException.class, () -> new Magazine("M001", "Wired", 0));
    }

    @Test
    void borrowingAnAlreadyBorrowedItemIsRefused() {
        LibraryItem book = new Book("B001", "Clean Code", "Robert C. Martin");
        book.markAsBorrowed();

        assertThrows(IllegalStateException.class, book::markAsBorrowed);
    }
}
