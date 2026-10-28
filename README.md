# Library Management System

A Java console application that manages a small library: members borrow and return
books, magazines and DVDs, with loan limits, due dates and automatic late fees.

Final project – Object-Oriented Programming in Java – JUNIA M1

---

## 1. Purpose of the application

The application helps a librarian manage daily operations. It enforces the library's
business rules automatically:

| Rule | Detail |
|---|---|
| Loan period depends on the item type | Book: 21 days · Magazine: 7 days · DVD: 3 days |
| Late fee depends on the item type | Book: 0.20 €/day · Magazine: 0.10 €/day · DVD: 1.00 €/day, capped at 15 € |
| Loan limit depends on the membership | STANDARD: 3 items at once · PREMIUM: 6 items at once |
| Unpaid fees block borrowing | A member who owes money must pay before borrowing again |
| One item, one borrower | An item that is already borrowed cannot be borrowed again |

---

## 2. How to run the application

**Requirements:** Java 17 or higher, Maven.

**From IntelliJ IDEA:** open `src/main/java/com/library/Application.java` and click the green ▶ next to `main`.

**From the command line** (in the project folder):

```bash
mvn compile
java -cp target/classes com.library.Application
```

The application reads its data from `src/main/resources` by default.
Another folder can be given as an argument:

```bash
java -cp target/classes com.library.Application path/to/data
```

---

## 3. Main features

When it starts, the application loads its data from CSV files, then shows a menu:

```
1. List all items           5. Return an item
2. List available items     6. Show a member's loans
3. List members             7. Pay late fees
4. Borrow an item           8. Library report
0. Quit
```

- **Borrow** an item (all business rules are checked)
- **Return** an item: the late fee is calculated and charged to the member automatically
- **Pay** late fees
- **See a member's loans**, with overdue loans highlighted
- **Library report**: availability rate, unpaid fees, pending late fees, overdue loans
  and the members to contact

Every refused operation displays a clear message, and the application never crashes.

---

## 4. Main classes

| Package | Class | Responsibility |
|---|---|---|
| `model` | `LibraryItem` (abstract) | Common state and behavior of every item (id, title, availability, late fee calculation) |
| `model` | `Book`, `Magazine`, `Dvd` | Concrete item types with their own loan period and fee rule |
| `model` | `Member`, `MembershipType` (enum) | A library member, their loan limit and their unpaid fees |
| `model` | `Loan` | Links a member to an item, computes the due date and the late fee |
| `repository` | `Repository<T>` (interface) | Storage contract: save, find by id, find all |
| `repository` | `InMemoryRepository<T>` | Generic in-memory implementation (sorted by id) |
| `loader` | `CsvLoader<T>` (abstract) | Reads a CSV file and skips invalid lines with a warning |
| `loader` | `ItemCsvLoader`, `MemberCsvLoader`, `LoanCsvLoader` | Turn one CSV line into an object |
| `loader` | `LibraryItemFactory` | Creates the right `LibraryItem` subclass from a type label |
| `service` | `LibraryService` | Business rules: borrow, return, pay fees, searches |
| `service` | `ReportService` | Read-only statistics |
| `service` | `DataInitializer` | Loads all the CSV files at startup |
| `ui` | `ConsoleMenu` | User interaction: