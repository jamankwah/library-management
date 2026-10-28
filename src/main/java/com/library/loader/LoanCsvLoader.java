package com.library.loader;

import com.library.exception.InvalidDataException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/** Format: memberId,itemId,borrowDate (yyyy-MM-dd) */
public class LoanCsvLoader extends CsvLoader<LoanRecord> {

    @Override
    protected LoanRecord parseLine(String[] fields) throws InvalidDataException {
        checkFieldCount(fields, 3);
        try {
            LocalDate borrowDate = LocalDate.parse(fields[2].trim());
            return new LoanRecord(fields[0].trim(), fields[1].trim(), borrowDate);
        } catch (DateTimeParseException e) {
            throw new InvalidDataException("invalid date '" + fields[2] + "', expected yyyy-MM-dd");
        }
    }
}