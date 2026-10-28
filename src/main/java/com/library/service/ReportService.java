package com.library.service;

import com.library.model.LibraryItem;
import com.library.model.Loan;
import com.library.model.Member;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Read-only statistics. Kept apart from LibraryService because reports change
 * for different reasons than borrowing rules (Single Responsibility).
 */
public class ReportService {

    private final LibraryService libraryService;

    public ReportService(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    /** Percentage of items currently on the shelves. */
    public double getAvailabilityRate() {
        List<LibraryItem> items = libraryService.getAllItems();
        if (items.isEmpty()) {
            return 0;
        }
        long available = items.stream().filter(LibraryItem::isAvailable).count();
        return available * 100.0 / items.size();
    }

    /** Fees already charged to members and not paid yet. */
    public double getTotalUnpaidFees() {
        return libraryService.getAllMembers().stream()
                .mapToDouble(Member::getOutstandingFees)
                .sum();
    }

    /** Fees that would be charged if every overdue item was returned today. */
    public double getPendingLateFees(LocalDate today) {
        return libraryService.getOverdueLoans(today).stream()
                .mapToDouble(loan -> loan.calculateLateFee(today))
                .sum();
    }

    /** A TreeSet removes duplicates (a member may have several overdue items) and sorts the names. */
    public Set<String> getNamesOfMembersWithOverdueItems(LocalDate today) {
        Set<String> names = new TreeSet<>();
        for (Loan loan : libraryService.getOverdueLoans(today)) {
            names.add(loan.getMember().getName());
        }
        return names;
    }
}