package com.library.loader;

import java.time.LocalDate;

/**
 * Raw loan data read from the file. It only holds ids: the service turns it into a real Loan,
 * so loans coming from the file go through exactly the same business rules as new loans.
 */
public class LoanRecord {

    private final String memberId;
    private final String itemId;
    private final LocalDate borrowDate;

    public LoanRecord(String memberId, String itemId, LocalDate borrowDate) {
        this.memberId = memberId;
        this.itemId = itemId;
        this.borrowDate = borrowDate;
    }

    public String getMemberId() {
        return memberId;
    }

    public String getItemId() {
        return itemId;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }
}