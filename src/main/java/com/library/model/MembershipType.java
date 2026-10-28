package com.library.model;

/** Each membership level allows a different number of simultaneous loans. */
public enum MembershipType {
    STANDARD(3),
    PREMIUM(6);

    private final int maxLoans;

    MembershipType(int maxLoans) {
        this.maxLoans = maxLoans;
    }

    public int getMaxLoans() {
        return maxLoans;
    }
}

