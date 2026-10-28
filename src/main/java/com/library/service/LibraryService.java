package com.library.service;

import com.library.exception.EntityNotFoundException;
import com.library.exception.ItemNotAvailableException;
import com.library.exception.LibraryException;
import com.library.exception.LoanLimitExceededException;
import com.library.exception.UnpaidFeesException;
import com.library.model.LibraryItem;
import com.library.model.Loan;
import com.library.model.Member;
import com.library.repository.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Business rules of the library: borrowing, returning and paying fees.
 * Repositories are injected through the constructor (Dependency Inversion).
 */
public class LibraryService {

    private final Repository<LibraryItem> itemRepository;
    private final Repository<Member> memberRepository;
    private final Repository<Loan> loanRepository;
    private int nextLoanNumber = 1;

    public LibraryService(Repository<LibraryItem> itemRepository,
                          Repository<Member> memberRepository,
                          Repository<Loan> loanRepository) {
        this.itemRepository = itemRepository;
        this.memberRepository = memberRepository;
        this.loanRepository = loanRepository;
    }

    public Loan borrowItem(String memberId, String itemId, LocalDate borrowDate) throws LibraryException {
        Member member = findMember(memberId);
        LibraryItem item = findItem(itemId);

        // All rules are checked before anything is modified, so a refused loan changes nothing.
        if (member.hasUnpaidFees()) {
            throw new UnpaidFeesException(String.format(
                    "%s must pay %.2f EUR of late fees before borrowing", member.getName(), member.getOutstandingFees()));
        }
        int activeLoans = getActiveLoans(memberId).size();
        if (activeLoans >= member.getMaxLoans()) {
            throw new LoanLimitExceededException(member.getName() + " already has " + activeLoans
                    + " loans, the maximum for " + member.getMembershipType() + " members");
        }
        if (!item.isAvailable()) {
            throw new ItemNotAvailableException("\"" + item.getTitle() + "\" is already borrowed");
        }

        item.markAsBorrowed();
        Loan loan = new Loan(String.format("L%03d", nextLoanNumber++), item, member, borrowDate);
        loanRepository.save(loan);
        return loan;
    }

    /** Returns the item and charges the late fee to the member. Returns the fee charged (0 if on time). */
    public double returnItem(String itemId, LocalDate returnDate) throws LibraryException {
        Loan loan = findActiveLoanForItem(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item " + itemId + " is not currently borrowed"));

        loan.markAsReturned(returnDate);
        loan.getItem().markAsReturned();

        double fee = loan.calculateLateFee(returnDate);
        if (fee > 0) {
            loan.getMember().addFee(fee);
        }
        return fee;
    }

    public void payFees(String memberId, double amount) throws EntityNotFoundException {
        findMember(memberId).payFees(amount);
    }

    public Member findMember(String memberId) throws EntityNotFoundException {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("No member with id " + memberId));
    }

    public LibraryItem findItem(String itemId) throws EntityNotFoundException {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("No item with id " + itemId));
    }

    public List<LibraryItem> getAllItems() {
        return itemRepository.findAll();
    }

    public List<LibraryItem> getAvailableItems() {
        return itemRepository.findAll().stream()
                .filter(LibraryItem::isAvailable)
                .toList();
    }

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    public List<Loan> getActiveLoans() {
        return loanRepository.findAll().stream()
                .filter(Loan::isActive)
                .toList();
    }

    public List<Loan> getActiveLoans(String memberId) {
        return getActiveLoans().stream()
                .filter(loan -> loan.getMember().getId().equals(memberId))
                .toList();
    }

    public List<Loan> getOverdueLoans(LocalDate today) {
        return getActiveLoans().stream()
                .filter(loan -> loan.isOverdue(today))
                .toList();
    }

    private Optional<Loan> findActiveLoanForItem(String itemId) {
        return getActiveLoans().stream()
                .filter(loan -> loan.getItem().getId().equals(itemId))
                .findFirst();
    }
}