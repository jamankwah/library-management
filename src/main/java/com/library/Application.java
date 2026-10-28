package com.library;

import com.library.model.Book;
import com.library.model.LibraryItem;

public class Application {

    public static void main(String[] args) {
        LibraryItem item = new Book("B001", "Clean Code", "Robert C. Martin");

        System.out.println(item);
        System.out.println("Loan period: " + item.getMaxLoanDays() + " days");
        System.out.println("Fee if 5 days late: " + item.calculateLateFee(5) + " EUR");

        item.markAsBorrowed();
        System.out.println(item);
    }
}
