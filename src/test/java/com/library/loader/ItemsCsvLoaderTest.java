package com.library.loader;

import com.library.exception.DataLoadException;
import com.library.model.Book;
import com.library.model.Dvd;
import com.library.model.LibraryItem;
import com.library.model.Magazine;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemCsvLoaderTest {

    private ItemCsvLoader loader;
    private Path tempFile;

    @BeforeEach
    void setUp() throws IOException {
        loader = new ItemCsvLoader();
        tempFile = Files.createTempFile("items", ".csv");
    }

    @AfterEach
    void tearDown() throws IOException {
        // Clean up the temporary file created for the test
        Files.deleteIfExists(tempFile);
    }

    // ===== Normal case =====

    @Test
    void validLinesCreateTheRightItemTypes() throws IOException, DataLoadException {
        Files.write(tempFile, List.of(
                "type,id,title,extra",
                "BOOK,B001,Clean Code,Robert C. Martin",
                "MAGAZINE,M001,Wired,31",
                "DVD,D001,Inception,148"));

        List<LibraryItem> items = loader.load(tempFile);

        assertEquals(3, items.size());
        assertTrue(items.get(0) instanceof Book);
        assertTrue(items.get(1) instanceof Magazine);
        assertTrue(items.get(2) instanceof Dvd);
        assertTrue(loader.getWarnings().isEmpty());
    }

    // ===== Edge cases =====

    @Test
    void fileWithOnlyTheHeaderGivesNoItems() throws IOException, DataLoadException {
        Files.write(tempFile, List.of("type,id,title,extra"));

        assertTrue(loader.load(tempFile).isEmpty());
    }

    @Test
    void blankLinesAreIgnored() throws IOException, DataLoadException {
        Files.write(tempFile, List.of(
                "type,id,title,extra",
                "",
                "BOOK,B001,Clean Code,Robert C. Martin"));

        assertEquals(1, loader.load(tempFile).size());
        assertTrue(loader.getWarnings().isEmpty());
    }

    // ===== Error cases =====

    @Test
    void invalidLinesAreSkippedWithAWarning() throws IOException, DataLoadException {
        Files.write(tempFile, List.of(
                "type,id,title,extra",
                "BOOK,B001,Clean Code,Robert C. Martin",
                "POSTER,P001,Old Map,none",
                "DVD,D001,Inception,abc",
                "BOOK,B002"));

        List<LibraryItem> items = loader.load(tempFile);

        assertEquals(1, items.size());
        assertEquals(3, loader.getWarnings().size());
        assertTrue(loader.getWarnings().get(0).contains("unknown item type 'POSTER'"));
    }

    @Test
    void missingFileThrowsDataLoadException() {
        Path missingFile = Path.of("does-not-exist.csv");

        DataLoadException exception = assertThrows(DataLoadException.class, () -> loader.load(missingFile));

        assertTrue(exception.getCause() instanceof IOException);
    }
}