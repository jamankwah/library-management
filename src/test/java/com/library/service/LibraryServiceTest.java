package com.library.service;

import com.library.exception.EntityNotFoundException;
import com.library.exception.ItemNotAvailableException;
import com.library.exception.LibraryException;
import com.library.exception.LoanLimitExceededException;
import com.library.exception.UnpaidFeesException;
import com.library.model.Book;
import com.library.model.Dvd;
import com.library.model.LibraryItem;
import com.library.model.Loan;
import com.library.model.Magazine;
import com.library.model.Member;
import com.library.model.MembershipType;
import com.library.repository.InMemoryRepository;
import com.library.repository.Repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LibraryServiceTest {

    private static final LocalDate DAY_1 = LocalDate.of(2026, 10, 1);

    private LibraryService service;

    @BeforeEach
    void setUp() {
        // A small, fresh library for every test: no files, so the tests stay fast and independent
        Repository<LibraryItem> items = new InMemoryRepository<>(LibraryItem::getId);
        Repository<Member> members = new InMemoryRepository<>(Member::getId);
        Repository<Loan> loans = new InMemoryRepository<>(Loan::getId);

        items.save(new Book("B001", "Clean Code", "Robert C. Martin"));
        items.save(new Book("B002", "Effective Java", "Joshua Bloch"));
        items.save(new Magazine("M001", "Wired", 31));
        items.save(new Dvd("D001", "Inception", 148));

        members.save(new Member("U001", "Alice Martin", MembershipType.STANDARD));
        members.save(new Member("U002", "Bob Dupont", MembershipType.PREMIUM));
        members.save(new Member("U003", "Chloe Bernard", MembershipType.STANDARD, 5.00));

        service = new LibraryService(items, members, loans);
    }

    // ===== Normal cases =====

    @Test
    void borrowingMarksTheItemAsBorrowed() throws LibraryException {
        Loan loan = service.borrowItem("U001", "B001", DAY_1);

        assertEquals("B001", loan.getItem().getId());
        assertFalse(service.findItem("B001").isAvailable());
    }

    @Test
    void borrowedItemDisappearsFromAvailableItems() throws LibraryException {
        service.borrowItem("U001", "B001", DAY_1);

        assertEquals(3, service.getAvailableItems().size());
    }

    @Test
    void returningOnTimeCostsNothing() throws LibraryException {
        service.borrowItem("U001", "B001", DAY_1);

        double fee = service.returnItem("B001", DAY_1.plusDays(10));

        assertEquals(0.00, fee, 0.001);
        assertTrue(service.findItem("B001").isAvailable());
    }

    @Test
    void lateReturnChargesTheMember() throws LibraryException {
        // DVD: due after 3 days, returned after 10 days -> 7 days x 1 EUR
        service.borrowItem("U001", "D001", DAY_1);

        double fee = service.returnItem("D001", DAY_1.plusDays(10));

        assertEquals(7.00, fee, 0.001);
        assertEquals(7.00, service.findMember("U001").getOutstandingFees(), 0.001);
    }

    @Test
    void memberCanBorrowAgainAfterPayingFees() throws LibraryException {
        service.payFees("U003", 5.00);

        Loan loan = service.borrowItem("U003", "B001", DAY_1);

        assertEquals("Chloe Bernard", loan.getMember().getName());
    }

    // ===== Edge cases =====

    @Test
    void standardMemberCanBorrowExactlyThreeItems() throws LibraryException {
        service.borrowItem("U001", "B001", DAY_1);
        service.borrowItem("U001", "B002", DAY_1);
        service.borrowItem("U001", "M001", DAY_1);

        assertEquals(3, service.getActiveLoans("U001").size());
    }

    @Test
    void returnedItemCanBeBorrowedByAnotherMember() throws LibraryException {
        service.borrowItem("U001", "B001", DAY_1);
        service.returnItem("B001", DAY_1.plusDays(5));

        Loan loan = service.borrowItem("U002", "B001", DAY_1.plusDays(5));

        assertEquals("Bob Dupont", loan.getMember().getName());
    }

    @Test
    void onlyLateLoansAreOverdue() throws LibraryException {
        service.borrowItem("U001", "B001", DAY_1);   // book: due 22 October
        service.borrowItem("U002", "D001", DAY_1);   // DVD: due 4 October

        assertEquals(1, service.getOverdueLoans(LocalDate.of(2026, 10, 10)).size());
    }

    // ===== Error cases =====

    @Test
    void borrowingAnUnavailableItemIsRefused() throws LibraryException {
        service.borrowItem("U001", "B001", DAY_1);

        ItemNotAvailableException exception = assertThrows(ItemNotAvailableException.class,
                () -> service.borrowItem("U002", "B001", DAY_1));

        assertEquals("\"Clean Code\" is already borrowed", exception.getMessage());
    }

    @Test
    void fourthLoanOfStandardMemberIsRefused() throws LibraryException {
        service.borrowItem("U001", "B001", DAY_1);
        service.borrowItem("U001", "B002", DAY_1);
        service.borrowItem("U001", "M001", DAY_1);

        assertThrows(LoanLimitExceededException.class, () -> service.borrowItem("U001", "D001", DAY_1));
    }

    @Test
    void memberWithUnpaidFeesCannotBorrow() {
        assertThrows(UnpaidFeesException.class, () -> service.borrowItem("U003", "B001", DAY_1));
    }

    @Test
    void unknownMemberIsRefused() {
        assertThrows(EntityNotFoundException.class, () -> service.borrowItem("U999", "B001", DAY_1));
    }

    @Test
    void unknownItemIsRefused() {
        assertThrows(EntityNotFoundException.class, () -> service.borrowItem("U001", "X999", DAY_1));
    }

    @Test
    void returningAnItemThatIsNotBorrowedIsRefused() {
        assertThrows(EntityNotFoundException.class, () -> service.returnItem("B001", DAY_1));
    }

    @Test
    void refusedLoanChangesNothing() {
        assertThrows(UnpaidFeesException.class, () -> service.borrowItem("U003", "B001", DAY_1));

        assertTrue(service.getActiveLoans().isEmpty());
        assertEquals(4, service.getAvailableItems().size());
    }
}
