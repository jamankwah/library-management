package com.library;

import com.library.exception.DataLoadException;
import com.library.loader.ItemCsvLoader;
import com.library.model.LibraryItem;

import java.nio.file.Path;
import java.util.List;

public class Application {

    public static void main(String[] args) {
        ItemCsvLoader loader = new ItemCsvLoader();

        try {
            List<LibraryItem> items = loader.load(Path.of("src/main/resources/items.csv"));
            System.out.println(items.size() + " items loaded:");
            for (LibraryItem item : items) {
                System.out.println("  " + item);
            }
            for (String warning : loader.getWarnings()) {
                System.out.println("WARNING " + warning);
            }
        } catch (DataLoadException e) {
            System.out.println("Cannot load items: " + e.getMessage());
        }

        // Second test: a file that does not exist
        try {
            loader.load(Path.of("src/main/resources/missing.csv"));
        } catch (DataLoadException e) {
            System.out.println("Cannot load items: " + e.getMessage());
            System.out.println("Original cause: " + e.getCause().getClass().getSimpleName());
        }
    }
}