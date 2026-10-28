package com.library.loader;

import com.library.exception.DataLoadException;
import com.library.exception.InvalidDataException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads a CSV file line by line. Subclasses only decide how to turn one line into an object.
 * A bad line is skipped and reported instead of stopping the whole application.
 */
public abstract class CsvLoader<T> implements DataLoader<T> {

    private final List<String> warnings = new ArrayList<>();

    @Override
    public List<T> load(Path path) throws DataLoadException {
        List<String> lines;
        try {
            lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new DataLoadException("Unable to read file " + path, e);
        }

        warnings.clear();
        List<T> results = new ArrayList<>();
        // Index 0 is the header line, data starts at index 1.
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.isBlank()) {
                continue;
            }
            int lineNumber = i + 1;
            try {
                results.add(parseLine(line.split(",")));
            } catch (InvalidDataException e) {
                warnings.add(path.getFileName() + " line " + lineNumber + " skipped: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                // Thrown by model constructors when a value breaks a business rule.
                warnings.add(path.getFileName() + " line " + lineNumber + " skipped: " + e.getMessage());
            }
        }
        return results;
    }

    public List<String> getWarnings() {
        return List.copyOf(warnings);
    }

    protected abstract T parseLine(String[] fields) throws InvalidDataException;

    protected void checkFieldCount(String[] fields, int expected) throws InvalidDataException {
        if (fields.length != expected) {
            throw new InvalidDataException("expected " + expected + " fields but found " + fields.length);
        }
    }

    protected int parseInt(String value, String fieldName) throws InvalidDataException {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new InvalidDataException(fieldName + " is not a whole number: '" + value + "'");
        }
    }

    protected double parseDouble(String value, String fieldName) throws InvalidDataException {
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            throw new InvalidDataException(fieldName + " is not a number: '" + value + "'");
        }
    }
}