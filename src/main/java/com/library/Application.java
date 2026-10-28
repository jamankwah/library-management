package com.library;

import com.library.exception.InvalidAmountException;
import com.library.model.Member;
import com.library.model.MembershipType;

public class Application {

    public static void main(String[] args) {
        Member alice = new Member("U001", "Alice Martin", MembershipType.STANDARD);
        Member bob = new Member("U002", "Bob Dupont", MembershipType.PREMIUM);

        System.out.println(alice + " -> can borrow " + alice.getMaxLoans() + " items");
        System.out.println(bob + " -> can borrow " + bob.getMaxLoans() + " items");

        alice.addFee(4.50);
        System.out.println("After a late return: " + alice);

        try {
            alice.payFees(10);
        } catch (InvalidAmountException e) {
            System.out.println("Payment refused: " + e.getMessage());
        }

        alice.payFees(4.50);
        System.out.println("After paying: " + alice);
    }
}