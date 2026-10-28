package com.library;

import com.library.model.Book;
import com.library.model.Dvd;
import com.library.model.LibraryItem;
import com.library.model.Magazine;
import com.library.model.Member;
import com.library.model.MembershipType;
import com.library.repository.InMemoryRepository;
import com.library.repository.Repository;

public class Application {

    public static void main(String[] args) {
        // Same generic class, two different types
        Repository<LibraryItem> itemRepository = new InMemoryRepository<>(LibraryItem::getId);
        Repository<Member> memberRepository = new InMemoryRepository<>(Member::getId);

        // Added in random order on purpose
        itemRepository.save(new Dvd("D001", "Inception", 148));
        itemRepository.save(new Book("B002", "Effective Java", "Joshua Bloch"));
        itemRepository.save(new Magazine("M001", "National Geographic", 245));
        itemRepository.save(new Book("B001", "Clean Code", "Robert C. Martin"));
        memberRepository.save(new Member("U001", "Alice Martin", MembershipType.STANDARD));

        System.out.println("All items (sorted by id thanks to TreeMap):");
        for (LibraryItem item : itemRepository.findAll()) {
            System.out.println("  " + item);
        }

        System.out.println("Search B002: "
                + itemRepository.findById("B002").map(LibraryItem::getTitle).orElse("not found"));
        System.out.println("Search B999: "
                + itemRepository.findById("B999").map(LibraryItem::getTitle).orElse("not found"));
        System.out.println("Search member U001: "
                + memberRepository.findById("U001").map(Member::getName).orElse("not found"));
    }
}