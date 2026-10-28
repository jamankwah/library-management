package com.library;

import com.library.exception.DataLoadException;
import com.library.exception.LibraryException;
import com.library.loader.ItemCsvLoader;
import com.library.loader.MemberCsvLoader;
import com.library.model.LibraryItem;
import com.library.model.Loan;
import com.library.model.Member;
import com.library.repository.InMemoryRepository;
import com.library.repository.Repository;
import com.library.service.LibraryService;

import java.nio.file.Path;
import java.time.LocalDate;

public class Application {

    public static void main(String[] args) throws DataLoadException {
        Repository<LibraryItem> itemRepository = new InMemoryRepository<>(LibraryItem::getId);
        Repository<Member> memberRepository = new InMemoryRepository<>(Member::getId);
        Repository<Loan> loanRepository = new InMemoryRepository<>(Loan::getId);

        new ItemCsvLoader().load(Path.of("src/main/resources/items.csv")).forEach(itemRepository::save);
        new MemberCsvLoader().load(Path.of("src/main/resources/members.csv")).forEach(memberRepository::save);

        LibraryService service = new LibraryService(itemRepository, memberRepository, loanRepository);
        LocalDate day1 = LocalDate.of(2026, 10, 1);

        System.out.println("1. Alice borrows Clean Code:");
        tryBorrow(service, "U001", "B001", day1);

        System.out.println("2. Bob wants the same book:");
        tryBorrow(service, "U002", "B001", day1);

        System.out.println("3. Chloe (owes 4.50 EUR) wants a book:");
        tryBorrow(service, "U003", "B002", day1);

        System.out.println("4. Alice borrows 2 more items, then a 4th one:");
        tryBorrow(service, "U001", "M001", day1);
        tryBorrow(service, "U001", "D001", day1);
        tryBorrow(service, "U001", "B003", day1);

        System.out.println("5. Unknown member:");
        tryBorrow(service, "U999", "B004", day1);

        System.out.println("6. Alice returns the DVD 10 days later (due after 3 days):");
        try {
            double fee = service.returnItem("D001", day1.plusDays(10));
            System.out.printf("   Fee charged: %.2f EUR -> %s%n", fee, service.findMember("U001"));
        } catch (LibraryException e) {
            System.out.println("   Refused: " + e.getMessage());
        }

        System.out.println("7. Alice now owes money and tries to borrow again:");
        tryBorrow(service, "U001", "B004", day1.plusDays(10));

        System.out.println("8. Alice pays, then borrows:");
        try {
            service.payFees("U001", 7.00);
            System.out.println("   Paid -> " + service.findMember("U001"));
        } catch (LibraryException e) {
            System.out.println("   Refused: " + e.getMessage());
        }
        tryBorrow(service, "U001", "B004", day1.plusDays(10));
    }

    private static void tryBorrow(LibraryService service, String memberId, String itemId, LocalDate date) {
        try {
            Loan loan = service.borrowItem(memberId, itemId, date);
            System.out.println("   OK: " + loan);
        } catch (LibraryException e) {
            System.out.println("   Refused (" + e.getClass().getSimpleName() + "): " + e.getMessage());
        }
    }
}