# Library Management System

A Java console application that manages a small library. Members borrow and return
books, magazines and DVDs. The application enforces the library's rules automatically:
loan periods, loan limits, late fees and unpaid fees.

Final project – Object-Oriented Programming in Java – JUNIA M1

---

## 1. Project explanation

The application helps a librarian manage daily operations. When it starts, it loads the
library's items, members and current loans from CSV files, then lets the user borrow,
return and pay through a console menu.

The library follows these business rules:

| Rule | Detail |
|---|---|
| Loan period depends on the item type | Book: 21 days · Magazine: 7 days · DVD: 3 days |
| Late fee depends on the item type | Book: 0.20 €/day · Magazine: 0.10 €/day · DVD: 1.00 €/day, capped at 15 € |
| Loan limit depends on the membership | STANDARD: 3 items at once · PREMIUM: 6 items at once |
| Unpaid fees block borrowing | A member who owes money must pay before borrowing again |
| One item, one borrower | An item that is already borrowed cannot be borrowed again |

---

## 2. Code structure

The code is organized in packages, each with a single responsibility.

```
src/
├── main/
│   ├── java/com/library/
│   │   ├── model/        the library's entities
│   │   ├── exception/    custom exceptions
│   │   ├── repository/   storage of the entities
│   │   ├── loader/       reading the CSV files
│   │   ├── service/      business rules and reports
│   │   ├── ui/           console menu
│   │   └── Application.java
│   └── resources/        items.csv, members.csv, loans.csv
└── test/
    └── java/com/library/ unit tests (JUnit 5)
```

| Package | Class | Responsibility |
|---|---|---|
| `model` | `LibraryItem` (abstract) | Common state and behavior of every item: id, title, availability, late fee calculation |
| `model` | `Book`, `Magazine`, `Dvd` | Concrete item types, each with its own loan period and fee rule |
| `model` | `Member`, `MembershipType` (enum) | A library member, their loan limit and their unpaid fees |
| `model` | `Loan` | Links a member to an item, computes the due date and the late fee |
| `exception` | `LibraryException` and its subclasses | Business errors (item not available, loan limit, unpaid fees, not found) |
| `repository` | `Repository<T>` (interface) | Storage contract: save, find by id, find all |
| `repository` | `InMemoryRepository<T>` | Generic in-memory implementation |
| `loader` | `CsvLoader<T>` (abstract) | Reads a CSV file line by line |
| `loader` | `ItemCsvLoader`, `MemberCsvLoader`, `LoanCsvLoader` | Turn one CSV line into an object |
| `loader` | `LibraryItemFactory` | Creates the right item class from its type |
| `service` | `LibraryService` | Business rules: borrow, return, pay fees |
| `service` | `ReportService` | Statistics for the librarian |
| `service` | `DataInitializer` | Loads all the CSV files at startup |
| `ui` | `ConsoleMenu` | User interaction: menu, input, messages |
| — | `Application` | Entry point: creates the objects and connects them |

---

## 3. Main features

After loading the data, the application shows a menu:

```
===== LIBRARY =====
1. List all items
2. List available items
3. List members
4. Borrow an item
5. Return an item
6. Show a member's loans
7. Pay late fees
8. Library report
0. Quit
```

| Feature | What it does |
|---|---|
| **List items / available items / members** | Shows the catalogue, the items on the shelves, and the members with their unpaid fees |
| **Borrow an item** | Creates a loan starting today. Refused if the item is already borrowed, the member reached their limit or owes fees |
| **Return an item** | Ends the loan. If the item is late, the fee is calculated according to the item type and charged to the member |
| **Show a member's loans** | Lists the member's current loans and highlights the overdue ones |
| **Pay late fees** | Reduces the member's unpaid fees. Refused if the amount is invalid or higher than what is owed |
| **Library report** | Availability rate, total unpaid fees, pending late fees, overdue loans and members to contact |

Every refused operation displays a clear message, and the menu comes back: the
application never crashes on a wrong input.

---

## 4. Technical choices

1. **An abstract class for library items.** `Book`, `Magazine` and `Dvd` are the same kind
   of thing and share state (id, title, availability) and code (`calculateLateFee`).
   `LibraryItem` is abstract because a generic item must not exist. Each subclass overrides
   its loan period and fee, so the rest of the code calls `item.calculateLateFee(...)`
   without knowing the type (polymorphism, no `instanceof`). `Dvd` reuses the parent
   calculation with `super` and adds its 15 € cap.

2. **An interface for storage.** `LibraryService` depends on the `Repository<T>` interface
   and receives its repositories through its constructor (dependency injection). The storage
   could change (for example to a database) without changing the business rules.

3. **One generic repository instead of three.** `InMemoryRepository<T>` stores items,
   members and loans. It receives a function giving each object's id (`LibraryItem::getId`).
   A `TreeMap` keeps listings sorted by id.

4. **Business rules checked before any change.** In `borrowItem`, every rule is verified
   before the item is marked as borrowed, so a refused loan leaves the application unchanged.

5. **Custom exceptions for business errors.** Business errors extend `LibraryException`,
   a checked exception, so the compiler forces the menu to handle them; one `catch`
   handles them all. An invalid amount throws `InvalidAmountException`, which is unchecked
   because it is a wrong argument.

6. **Dates passed as parameters.** Methods such as `Loan.isOverdue(LocalDate today)` receive
   the date instead of calling `LocalDate.now()` inside, so results are predictable and the
   unit tests give the same result every day. Only the console menu uses the real date.

7. **Separate classes for separate responsibilities.** Business rules (`LibraryService`),
   statistics (`ReportService`), file reading (loaders) and user interaction (`ConsoleMenu`)
   are in different classes. `Application` only creates and connects the objects.

8. **A Simple Factory for item creation.** `LibraryItemFactory` is the only place that
   knows which class matches `BOOK`, `MAGAZINE` or `DVD`. Adding a new item type means
   adding a subclass and one line in the factory.

---

## 5. Data used by the application

The application uses three **CSV files** stored in `src/main/resources`. They are loaded
automatically at startup. The first line of each file is a header.

### `items.csv` – the library's catalogue

| Column | Meaning | Example |
|---|---|---|
| `type` | `BOOK`, `MAGAZINE` or `DVD` | `BOOK` |
| `id` | Unique identifier | `B001` |
| `title` | Title of the item | `Clean Code` |
| `extra` | Author (book), issue number (magazine) or duration in minutes (DVD) | `Robert C. Martin` |

Content: 5 books, 3 magazines and 3 DVDs.

### `members.csv` – the library's members

| Column | Meaning | Example |
|---|---|---|
| `id` | Unique identifier | `U001` |
| `name` | Member's name | `Alice Martin` |
| `membershipType` | `STANDARD` (3 loans max) or `PREMIUM` (6 loans max) | `STANDARD` |
| `outstandingFees` | Late fees already owed, in euros | `0` |

Content: 2 STANDARD and 2 PREMIUM members. One member, Chloe Bernard, already owes 4.50 €.

### `loans.csv` – loans in progress when the library opens

| Column | Meaning | Example |
|---|---|---|
| `memberId` | Id of the member who borrowed | `U001` |
| `itemId` | Id of the borrowed item | `B001` |
| `borrowDate` | Date of the loan (`yyyy-MM-dd`) | `2026-10-01` |

Some of these loans are already overdue, so the report shows late fees from the start.

### How the data is loaded

1. Each file is read with `Files.readAllLines`, and the header line is skipped.
2. Each line is split on commas and converted by the matching loader.
   `LibraryItemFactory` chooses the right class (`Book`, `Magazine` or `Dvd`).
3. Items and members are saved in their repositories.
4. Each loan is created through `LibraryService.borrowItem`, so the business rules apply
   to the file data exactly as they do in the menu.

### Deliberately invalid data

The files contain a few invalid lines on purpose, to show the error handling. They are
skipped and reported as warnings at startup, and the application keeps running.

| File | Line | Problem | Result |
|---|---|---|---|
| `items.csv` | `POSTER,P001,Old Map,none` | Unknown item type | Skipped with a warning |
| `items.csv` | `MAGAZINE,M004,Le Monde Diplomatique,abc` | Issue number is not a number | Skipped with a warning |
| `members.csv` | `U005,Emma Leroy,GOLD,0` | Unknown membership type | Skipped with a warning |
| `loans.csv` | `U003,B003,2026-10-10` | Chloe owes fees | Loan refused |
| `loans.csv` | `U001,B001,2026-10-05` | Item already borrowed | Loan refused |

Startup output:

```
Data loaded: 11 items, 4 members, 5 active loans.
WARNING items.csv line 13 skipped: unknown item type 'POSTER'
WARNING items.csv line 14 skipped: 'abc' is not a whole number
WARNING members.csv line 6 skipped: unknown membership type 'GOLD'
WARNING loans.csv: loan of B003 refused: Chloe Bernard must pay 4.50 EUR of late fees before borrowing
WARNING loans.csv: loan of B001 refused: "Clean Code" is already borrowed
```