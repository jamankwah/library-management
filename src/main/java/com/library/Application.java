package com.library;

import com.library.model.Book;
import com.library.model.Dvd;
import com.library.model.LibraryItem;
import com.library.model.Magazine;

import java.util.ArrayList;
import java.util.List;

public class Application {

    public static void main(String[] args) {
        List<LibraryItem> items = new ArrayList<>();
        items.add(new Book("B001", "Clean Code", "Robert C. Martin"));
        items.add(new Magazine("M001", "National Geographic", 245));
        items.add(new Dvd("D001", "Inception", 148));

        for (LibraryItem item : items) {
            System.out.println(item);
            System.out.println("   Loan period: " + item.getMaxLoanDays() + " days");
            System.out.println("   Fee for 5 days late: " + item.calculateLateFee(5) + " EUR");
            System.out.println("   Fee for 30 days late: " + item.calculateLateFee(30) + " EUR");
        }
    }
}