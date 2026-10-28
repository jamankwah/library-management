package com.library.ui;

import com.library.exception.InvalidAmountException;
import com.library.exception.LibraryException;
import com.library.model.Loan;
import com.library.model.Member;
import com.library.service.LibraryService;
import com.library.service.ReportService;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * User interaction only: reads input, calls the services, displays results.
 * This is the application boundary where business errors are turned into messages for the user.
 */
public class ConsoleMenu {

    private final LibraryService libraryService;
    private final ReportService reportService;
    private final Scanner scanner;

    public ConsoleMenu(LibraryService libraryService, ReportService reportService, Scanner scanner) {
        this.libraryService = libraryService;
        this.reportService = reportService;
        this.scanner = scanner;
    }

    public void run() {
        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> printList(libraryService.getAllItems());
                    case "2" -> printList(libraryService.getAvailableItems());
                    case "3" -> printList(libraryService.getAllMembers());
                    case "4" -> borrow();
                    case "5" -> returnItem();
                    case "6" -> showMemberLoans();
                    case "7" -> payFees();
                    case "8" -> showReport();
                    case "0" -> running = false;
                    default -> System.out.println("Unknown choice, please type a number from the menu.");
                }
            } catch (LibraryException e) {
                System.out.println("Operation refused: " + e.getMessage());
            } catch (InvalidAmountException e) {
                System.out.println("Invalid amount: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid input: " + e.getMessage());
            }
        }
        System.out.println("Goodbye!");
    }

    private void printMenu() {
        System.out.println();
        System.out.println("===== LIBRARY =====");
        System.out.println("1. List all items");
        System.out.println("2. List available items");
        System.out.println("3. List members");
        System.out.println("4. Borrow an item");
        System.out.println("5. Return an item");
        System.out.println("6. Show a member's loans");
        System.out.println("7. Pay late fees");
        System.out.println("8. Library report");
        System.out.println("0. Quit");
        System.out.print("Your choice: ");
    }

    private void borrow() throws LibraryException {
        String memberId = ask("Member id: ");
        String itemId = ask("Item id: ");
        Loan loan = libraryService.borrowItem(memberId, itemId, LocalDate.now());
        System.out.println("Loan created -> " + loan);
    }

    private void returnItem() throws LibraryException {
        String itemId = ask("Item id: ");
        LocalDate returnDate = askDate("Return date (yyyy-MM-dd, Enter for today): ");
        double fee = libraryService.returnItem(itemId, returnDate);
        if (fee > 0) {
            System.out.printf("Item returned late. Fee charged: %.2f EUR%n", fee);
        } else {
            System.out.println("Item returned on time. No fee.");
        }
    }

    private void showMemberLoans() throws LibraryException {
        Member member = libraryService.findMember(ask("Member id: "));
        System.out.println(member);
        List<Loan> loans = libraryService.getActiveLoans(member.getId());
        if (loans.isEmpty()) {
            System.out.println("No active loans.");
        }
        LocalDate today = LocalDate.now();
        for (Loan loan : loans) {
            String warning = loan.isOverdue(today) ? "  <-- OVERDUE by " + loan.getDaysLate(today) + " day(s)" : "";
            System.out.println("  " + loan + warning);
        }
    }

    private void payFees() throws LibraryException {
        String memberId = ask("Member id: ");
        double amount = askAmount("Amount (EUR): ");
        libraryService.payFees(memberId, amount);
        System.out.println("Payment accepted -> " + libraryService.findMember(memberId));
    }

    private void showReport() {
        LocalDate today = LocalDate.now();
        System.out.printf("Items available: %.1f%%%n", reportService.getAvailabilityRate());
        System.out.printf("Unpaid fees: %.2f EUR%n", reportService.getTotalUnpaidFees());
        System.out.printf("Pending late fees on overdue items: %.2f EUR%n", reportService.getPendingLateFees(today));
        System.out.println("Overdue loans:");
        printList(libraryService.getOverdueLoans(today));
        System.out.println("Members to contact: " + reportService.getNamesOfMembersWithOverdueItems(today));
    }

    private String ask(String question) {
        System.out.print(question);
        return scanner.nextLine().trim();
    }

    private double askAmount(String question) {
        String input = ask(question);
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("'" + input + "' is not a number (example: 4.50)");
        }
    }

    private LocalDate askDate(String question) {
        String input = ask(question);
        if (input.isEmpty()) {
            return LocalDate.now();
        }
        try {
            return LocalDate.parse(input);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("'" + input + "' is not a valid date (expected yyyy-MM-dd)");
        }
    }

    // Generic so it works for items, members and loans: each element uses its own toString().
    private <T> void printList(List<T> elements) {
        if (elements.isEmpty()) {
            System.out.println("  (nothing to display)");
        }
        for (T element : elements) {
            System.out.println("  " + element);
        }
    }
}