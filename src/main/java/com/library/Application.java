package com.library;

import com.library.exception.DataLoadException;
import com.library.model.LibraryItem;
import com.library.model.Loan;
import com.library.model.Member;
import com.library.repository.InMemoryRepository;
import com.library.repository.Repository;
import com.library.service.DataInitializer;
import com.library.service.LibraryService;
import com.library.service.ReportService;
import com.library.ui.ConsoleMenu;

import java.nio.file.Path;
import java.util.Scanner;

/**
 * Entry point. Its only job is to create the objects and connect them together;
 * all the real work is done by the classes it wires.
 */
public class Application {

    private static final String DEFAULT_DATA_FOLDER = "src/main/resources";

    public static void main(String[] args) {
        Path dataFolder = Path.of(args.length > 0 ? args[0] : DEFAULT_DATA_FOLDER);

        Repository<LibraryItem> itemRepository = new InMemoryRepository<>(LibraryItem::getId);
        Repository<Member> memberRepository = new InMemoryRepository<>(Member::getId);
        Repository<Loan> loanRepository = new InMemoryRepository<>(Loan::getId);

        LibraryService libraryService = new LibraryService(itemRepository, memberRepository, loanRepository);
        ReportService reportService = new ReportService(libraryService);
        DataInitializer initializer = new DataInitializer(itemRepository, memberRepository, libraryService);

        try {
            initializer.loadAll(dataFolder);
        } catch (DataLoadException e) {
            System.err.println("Cannot start the library: " + e.getMessage());
            System.err.println("Cause: " + e.getCause());
            return;
        }

        System.out.println("Data loaded: " + itemRepository.findAll().size() + " items, "
                + memberRepository.findAll().size() + " members, "
                + libraryService.getActiveLoans().size() + " active loans.");
        initializer.getWarnings().forEach(warning -> System.out.println("WARNING " + warning));

        try (Scanner scanner = new Scanner(System.in)) {
            new ConsoleMenu(libraryService, reportService, scanner).run();
        }
    }
}