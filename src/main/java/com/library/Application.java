package com.library;

import com.library.exception.DataLoadException;
import com.library.loader.LoanCsvLoader;
import com.library.loader.LoanRecord;
import com.library.loader.MemberCsvLoader;
import com.library.model.Member;

import java.nio.file.Path;
import java.util.List;

public class Application {

    public static void main(String[] args) {
        MemberCsvLoader memberLoader = new MemberCsvLoader();
        LoanCsvLoader loanLoader = new LoanCsvLoader();

        try {
            List<Member> members = memberLoader.load(Path.of("src/main/resources/members.csv"));
            System.out.println(members.size() + " members loaded:");
            for (Member member : members) {
                System.out.println("  " + member);
            }
            for (String warning : memberLoader.getWarnings()) {
                System.out.println("WARNING " + warning);
            }

            List<LoanRecord> loans = loanLoader.load(Path.of("src/main/resources/loans.csv"));
            System.out.println(loans.size() + " loan records loaded:");
            for (LoanRecord loan : loans) {
                System.out.println("  " + loan.getMemberId() + " borrowed " + loan.getItemId()
                        + " on " + loan.getBorrowDate());
            }
            for (String warning : loanLoader.getWarnings()) {
                System.out.println("WARNING " + warning);
            }
        } catch (DataLoadException e) {
            System.out.println("Cannot load data: " + e.getMessage());
        }
    }
}