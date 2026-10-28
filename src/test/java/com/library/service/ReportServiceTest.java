package com.library.service;

import com.library.exception.LibraryException;
import com.library.model.Book;
import com.library.model.Dvd;
import com.library.model.LibraryItem;
import com.library.model.Loan;
import com.library.model.Member;
import com.library.model.MembershipType;
import com.library.repository.InMemoryRepository;
import com.library.repository.Repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReportServiceTest {

    private static final LocalDate DAY_1 = LocalDate.of(2026, 10, 1);

    private Repository<LibraryItem> items;
    private Repository<Member> members;
    private LibraryService libraryService;
    private ReportService reportService;

    @BeforeEach
    void setUp() {
        items = new InMemoryRepository<>(LibraryItem::getId);
        members = new InMemoryRepository<>(Member::getId);
        Repository<Loan> loans = new InMemoryRepository<>(Loan::getId);
        members.save(new Member("U001", "Alice Martin", MembershipType.STANDARD));
        members.save(new Member("U002", "Bob Dupont", MembershipType.PREMIUM, 3.50));
        libraryService = new LibraryService(items, members, loans);
        reportService = new ReportService(libraryService);
    }

    @Test
    void availabilityRateCountsItemsOnTheShelves() throws LibraryException {
        items.save(new Book("B001", "Clean Code", "Robert C. Martin"));
        items.save(new Book("B002", "Effective Java", "Joshua Bloch"));
        libraryService.borrowItem("U001", "B001", DAY_1);

        assertEquals(50.0, reportService.getAvailabilityRate(), 0.001);
    }

    @Test
    void availabilityRateOfEmptyLibraryIsZero() {
        // Edge case: no items must not cause a division by zero
        assertEquals(0.0, reportService.getAvailabilityRate(), 0.001);
    }

    @Test
    void totalUnpaidFeesAddsAllMembers() {
        assertEquals(3.50, reportService.getTotalUnpaidFees(), 0.001);
    }

    @Test
    void pendingLateFeesUseEachItemRule() throws LibraryException {
        items.save(new Book("B001", "Clean Code", "Robert C. Martin"));
        items.save(new Dvd("D001", "Inception", 148));
        libraryService.borrowItem("U001", "B001", DAY_1);   // due 22 Oct
        libraryService.borrowItem("U001", "D001", DAY_1);   // due 4 Oct

        // On 27 October: book 5 days x 0.20 = 1.00, DVD 23 days capped at 15.00
        assertEquals(16.00, reportService.getPendingLateFees(LocalDate.of(2026, 10, 27)), 0.001);
    }

    @Test
    void memberWithSeveralOverdueItemsIsListedOnce() throws LibraryException {
        items.save(new Book("B001", "Clean Code", "Robert C. Martin"));
        items.save(new Dvd("D001", "Inception", 148));
        libraryService.borrowItem("U001", "B001", DAY_1);
        libraryService.borrowItem("U001", "D001", DAY_1);

        Set<String> names = reportService.getNamesOfMembersWithOverdueItems(LocalDate.of(2026, 10, 27));

        assertEquals(Set.of("Alice Martin"), names);
    }
}
