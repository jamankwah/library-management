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

import java.nio.file.Path;
import java.time.LocalDate;

public class Application {

    public static void main(String[] args) {
        Repository<LibraryItem> itemRepository = new InMemoryRepository<>(LibraryItem::getId);
        Repository<Member> memberRepository = new InMemoryRepository<>(Member::getId);
        Repository<Loan> loanRepository = new InMemoryRepository<>(Loan::getId);

        LibraryService libraryService = new LibraryService(itemRepository, memberRepository, loanRepository);
        ReportService reportService = new ReportService(libraryService);
        DataInitializer initializer = new DataInitializer(itemRepository, memberRepository, libraryService);

        try {
            initializer.loadAll(Path.of("src/main/resources"));
        } catch (DataLoadException e) {
            System.out.println("Cannot start the library: " + e.getMessage());
            return;
        }

        System.out.println("Data loaded: " + itemRepository.findAll().size() + " items, "
                + memberRepository.findAll().size() + " members, "
                + libraryService.getActiveLoans().size() + " active loans.");
        for (String warning : initializer.getWarnings()) {
            System.out.println("WARNING " + warning);
        }

        LocalDate today = LocalDate.of(2026, 10, 28);
        System.out.println();
        System.out.println("Report on " + today + ":");
        System.out.printf("  Items available: %.1f%%%n", reportService.getAvailabilityRate());
        System.out.printf("  Unpaid fees: %.2f EUR%n", reportService.getTotalUnpaidFees());
        System.out.printf("  Pending late fees: %.2f EUR%n", reportService.getPendingLateFees(today));
        System.out.println("  Overdue loans:");
        for (Loan loan : libraryService.getOverdueLoans(today)) {
            System.out.println("    " + loan + " (" + loan.getDaysLate(today) + " days late)");
        }
        System.out.println("  Members to contact: " + reportService.getNamesOfMembersWithOverdueItems(today));
    }
}