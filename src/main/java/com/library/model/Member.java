package com.library.model;

import com.library.exception.InvalidAmountException;

import java.util.Objects;

public class Member {

    private final String id;
    private final String name;
    private final MembershipType membershipType;
    private double outstandingFees;

    public Member(String id, String name, MembershipType membershipType, double outstandingFees) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Member id cannot be empty");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Member name cannot be empty");
        }
        if (membershipType == null) {
            throw new IllegalArgumentException("Membership type is required");
        }
        if (outstandingFees < 0) {
            throw new IllegalArgumentException("Outstanding fees cannot be negative");
        }
        this.id = id;
        this.name = name;
        this.membershipType = membershipType;
        this.outstandingFees = outstandingFees;
    }

    public Member(String id, String name, MembershipType membershipType) {
        this(id, name, membershipType, 0);
    }

    public void addFee(double amount) {
        if (amount <= 0) {
            throw new InvalidAmountException("Fee must be positive: " + amount);
        }
        outstandingFees += amount;
    }

    public void payFees(double amount) {
        if (amount <= 0) {
            throw new InvalidAmountException("Payment must be positive: " + amount);
        }
        if (amount > outstandingFees) {
            throw new InvalidAmountException(String.format(
                    "Cannot pay %.2f EUR: %s only owes %.2f EUR", amount, name, outstandingFees));
        }
        outstandingFees -= amount;
    }

    public boolean hasUnpaidFees() {
        return outstandingFees > 0;
    }

    public int getMaxLoans() {
        return membershipType.getMaxLoans();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public MembershipType getMembershipType() {
        return membershipType;
    }

    public double getOutstandingFees() {
        return outstandingFees;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Member other)) {
            return false;
        }
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s) - fees due: %.2f EUR", id, name, membershipType, outstandingFees);
    }
}