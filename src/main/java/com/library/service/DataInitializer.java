package com.library.service;

import com.library.exception.DataLoadException;
import com.library.exception.LibraryException;
import com.library.loader.CsvLoader;
import com.library.loader.ItemCsvLoader;
import com.library.loader.LoanCsvLoader;
import com.library.loader.LoanRecord;
import com.library.loader.MemberCsvLoader;
import com.library.model.LibraryItem;
import com.library.model.Member;
import com.library.repository.Repository;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Fills the repositories from the CSV files when the application starts. */
public class DataInitializer {

    private final Repository<LibraryItem> itemRepository;
    private final Repository<Member> memberRepository;
    private final LibraryService libraryService;
    private final List<String> warnings = new ArrayList<>();

    public DataInitializer(Repository<LibraryItem> itemRepository,
                           Repository<Member> memberRepository,
                           LibraryService libraryService) {
        this.itemRepository = itemRepository;
        this.memberRepository = memberRepository;
        this.libraryService = libraryService;
    }

    public void loadAll(Path dataFolder) throws DataLoadException {
        ItemCsvLoader itemLoader = new ItemCsvLoader();
        itemLoader.load(dataFolder.resolve("items.csv")).forEach(itemRepository::save);
        collectWarnings(itemLoader);

        MemberCsvLoader memberLoader = new MemberCsvLoader();
        memberLoader.load(dataFolder.resolve("members.csv")).forEach(memberRepository::save);
        collectWarnings(memberLoader);

        LoanCsvLoader loanLoader = new LoanCsvLoader();
        List<LoanRecord> records = loanLoader.load(dataFolder.resolve("loans.csv"));
        collectWarnings(loanLoader);
        for (LoanRecord loanRecord : records) {
            try {
                libraryService.borrowItem(loanRecord.getMemberId(), loanRecord.getItemId(), loanRecord.getBorrowDate());
            } catch (LibraryException e) {
                // One invalid loan in the file should not prevent the library from opening.
                warnings.add("loans.csv: loan of " + loanRecord.getItemId() + " refused: " + e.getMessage());
            }
        }
    }

    public List<String> getWarnings() {
        return List.copyOf(warnings);
    }

    private void collectWarnings(CsvLoader<?> loader) {
        warnings.addAll(loader.getWarnings());
    }
}