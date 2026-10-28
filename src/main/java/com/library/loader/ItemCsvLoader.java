package com.library.loader;

import com.library.exception.InvalidDataException;
import com.library.model.LibraryItem;

/** Format: type,id,title,extra  (extra = author for a book, issue number for a magazine, minutes for a DVD) */
public class ItemCsvLoader extends CsvLoader<LibraryItem> {

    @Override
    protected LibraryItem parseLine(String[] fields) throws InvalidDataException {
        checkFieldCount(fields, 4);
        return LibraryItemFactory.create(fields[0], fields[1].trim(), fields[2].trim(), fields[3].trim());
    }
}