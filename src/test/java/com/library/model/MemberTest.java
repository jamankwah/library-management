package com.library.model;

import com.library.exception.InvalidAmountException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MemberTest {

    private Member member;

    @BeforeEach
    void setUp() {
        // Every test starts with a fresh member who owes 10 EUR
        member = new Member("U001", "Alice Martin", MembershipType.STANDARD, 10.00);
    }

    // ===== Normal cases =====

    @Test
    void payingPartOfTheFeesReducesWhatIsOwed() {
        member.payFees(4.00);

        assertEquals(6.00, member.getOutstandingFees(), 0.001);
    }

    @Test
    void addingAFeeIncreasesWhatIsOwed() {
        member.addFee(2.50);

        assertEquals(12.50, member.getOutstandingFees(), 0.001);
    }

    @Test
    void standardMemberCanBorrowThreeItems() {
        assertEquals(3, member.getMaxLoans());
    }

    @Test
    void premiumMemberCanBorrowSixItems() {
        Member premium = new Member("U002", "Bob Dupont", MembershipType.PREMIUM);

        assertEquals(6, premium.getMaxLoans());
    }

    //  Edge cases

    @Test
    void payingExactlyEverythingClearsTheFees() {
        member.payFees(10.00);

        assertEquals(0.00, member.getOutstandingFees(), 0.001);
        assertFalse(member.hasUnpaidFees());
    }

    @Test
    void newMemberHasNoFees() {
        Member newMember = new Member("U002", "Bob Dupont", MembershipType.PREMIUM);

        assertFalse(newMember.hasUnpaidFees());
    }

    @Test
    void memberWhoOwesMoneyHasUnpaidFees() {
        assertTrue(member.hasUnpaidFees());
    }

    // ===== Error cases =====

    @Test
    void payingMoreThanOwedIsRefused() {
        InvalidAmountException exception = assertThrows(InvalidAmountException.class,
                () -> member.payFees(50.00));

        assertTrue(exception.getMessage().contains("Alice Martin"));
    }

    @Test
    void payingZeroIsRefused() {
        assertThrows(InvalidAmountException.class, () -> member.payFees(0));
    }

    @Test
    void payingNegativeAmountIsRefused() {
        assertThrows(InvalidAmountException.class, () -> member.payFees(-5));
    }

    @Test
    void refusedPaymentDoesNotChangeTheFees() {
        assertThrows(InvalidAmountException.class, () -> member.payFees(50.00));

        assertEquals(10.00, member.getOutstandingFees(), 0.001);
    }

    @Test
    void memberWithNegativeFeesCannotBeCreated() {
        assertThrows(IllegalArgumentException.class,
                () -> new Member("U003", "Chloe Bernard", MembershipType.STANDARD, -1));
    }
}