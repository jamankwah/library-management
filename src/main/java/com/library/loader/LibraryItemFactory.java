package com.library.loader;

import com.library.exception.InvalidDataException;
import com.library.model.Book;
import com.library.model.Dvd;
import com.library.model.LibraryItem;
import com.library.model.Magazine;

/**
 * Simple Factory: the only place that knows which concrete class matches a type label.
 * Adding a new item type means adding a subclass and one case here.
 */
public class LibraryItemFactory {

    private LibraryItemFactory() {
        // Utility class: no instances.
    }

    public static LibraryItem create(String type, String id, String title, String extra)
            throws InvalidDataException {
        return switch (type.trim().toUpperCase()) {
            case "BOOK" -> new Book(id, title, extra);
            case "MAGAZINE" -> new Magazine(id, title, parseNumber(extra));
            case "DVD" -> new Dvd(id, title, parseNumber(extra));
            default -> throw new InvalidDataException("unknown item type '" + type + "'");
        };
    }

    private static int parseNumber(String value) throws InvalidDataException {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new InvalidDataException("'" + value + "' is not a whole number");
        }
    }
}
