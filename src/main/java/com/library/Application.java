package com.library;

import com.library.model.Book;
import com.library.model.Dvd;
import com.library.model.Loan;
import com.library.model.Member;
import com.library.model.MembershipType;

import java.time.LocalDate;

public class Application {

    public static void main(String[] args) {
        Member alice = new Member("U001", "Alice Martin", MembershipType.STANDARD);
        Book book = new Book("B001", "Clean Code", "Robert C. Martin");
        Dvd dvd = new Dvd("D001", "Inception", 148);

        LocalDate borrowDate = LocalDate.of(2026, 10, 1);
        Loan bookLoan = new Loan("L001", book, alice, borrowDate);
        Loan dvdLoan = new Loan("L002", dvd, alice, borrowDate);

        System.out.println(bookLoan);
        System.out.println(dvdLoan);

        LocalDate today = LocalDate.of(2026, 10, 25);
        System.out.println("On " + today + ":");
        System.out.println("  Book overdue? " + bookLoan.isOverdue(today)
                + " - days late: " + bookLoan.getDaysLate(today)
                + " - fee: " + bookLoan.calculateLateFee(today) + " EUR");
        System.out.println("  DVD overdue? " + dvdLoan.isOverdue(today)
                + " - days late: " + dvdLoan.getDaysLate(today)
                + " - fee: " + dvdLoan.calculateLateFee(today) + " EUR");

        System.out.println("Book returned yet? " + bookLoan.getReturnDate().isPresent());
        bookLoan.markAsReturned(today);
        System.out.println("Book returned on: " + bookLoan.getReturnDate().get());
    }
}