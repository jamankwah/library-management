package com.library;

import com.library.exception.ItemNotAvailableException;
import com.library.exception.LibraryException;
import com.library.model.Book;
import com.library.model.LibraryItem;

public class Application {

    public static void main(String[] args) {
        LibraryItem book = new Book("B001", "Clean Code", "Robert C. Martin");

        try {
            checkAvailable(book);
            System.out.println("First borrow: OK");
            book.markAsBorrowed();

            checkAvailable(book);
            System.out.println("Second borrow: OK");   // never printed
        } catch (LibraryException e) {
            System.out.println("Operation refused: " + e.getMessage());
            System.out.println("Exception type: " + e.getClass().getSimpleName());
        }

        System.out.println("The program continues normally.");
    }

    // "throws" declares that this method may throw a checked exception (Class 4: throw vs throws)
    private static void checkAvailable(LibraryItem item) throws ItemNotAvailableException {
        if (!item.isAvailable()) {
            throw new ItemNotAvailableException("\"" + item.getTitle() + "\" is already borrowed");
        }
    }
}