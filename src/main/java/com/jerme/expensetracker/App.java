package com.jerme.expensetracker;

import com.jerme.expensetracker.filecreator.ExpenseFileCreator;
import com.jerme.expensetracker.models.Expense;
import com.jerme.expensetracker.repository.ExpenseRepository;
import com.jerme.expensetracker.repository.GetType;
import com.jerme.expensetracker.services.ExpenseRepositoryService;
import com.jerme.expensetracker.utils.ExpenseAuthenticator;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class App {

    private static final ExpenseRepository EXPENSE_REPOSITORY = new ExpenseRepository();
    private static final ExpenseRepositoryService ERP = new ExpenseRepositoryService();

    private static final Scanner IN = new Scanner(System.in);

    private boolean mainConsoleIsRunning = true;
    private boolean updateConsoleIsRunning = false;

    public void mainConsole() {
        System.out.println("Expense Tracker :)");

        while (mainConsoleIsRunning) {
            showMenu();
            handleChoice(IN.nextLine());
        }
    }

    public void showMenu() {
        System.out.println("\n1. Add an expense");
        System.out.println("2. Update an expense");
        System.out.println("3. Delete an expense");
        System.out.println("4. View all expenses");
        System.out.println("5. View expenses within specific timeframe");
        System.out.println("6. Create a file for the list of all expenses");
        System.out.println("7. Create a file for the list of expenses within a specific timeframe");
        System.out.println("8. View all of your spending summary");
        System.out.println("9. View all of your spending summary in a specific timeframe");
        System.out.println("10. Exit");
        System.out.print("Enter choice here: ");
    }

    public void handleChoice(String choice) {
        int c;

        if (choice.matches("\\d+") || choice.matches("-\\d+")) c = Integer.parseInt(choice);
        else {
            System.out.println("\"" + choice + "\" not a number!");
            return;
        }

        switch (c) {
            case 1 -> insert();
            case 2 -> updateConsole();
            case 3 -> delete();
            case 4 -> printAllExpenses();
            case 5 -> printAllExpenseByDate();
            case 6 -> createFileForAllExpenses();
            case 7 -> createFileForSpecificTime();
            case 8 -> viewSummaryForAll();
            case 9 -> viewSummaryForSpecificTime();
            case 10 -> exit();
            default -> System.out.println("Input " + c + " cannot be resolved!");
        }
    }

    public void insert() {
        String name;
        String desc;
        String amount;

        System.out.println("\nEnter \"break\" to exit.\n");

        do {
            System.out.print("Enter expense name here: ");
            name = IN.nextLine();

            if (wantBreak(name)) return;
            else if (!ExpenseAuthenticator.isProperName(name)) ERP.printNamingError(name);

        } while (!ExpenseAuthenticator.isProperName(name));

        do {
            System.out.print("Enter expense description here: ");
            desc = IN.nextLine();

            if (wantBreak(desc)) return;
            else if (!ExpenseAuthenticator.isProperDesc(desc)) ERP.printDescError(desc);

        } while (!ExpenseAuthenticator.isProperDesc(desc));

        do {
            System.out.print("Enter expense amount here: ");
            amount = IN.nextLine();

            if (wantBreak(amount)) return;
            else if (!ExpenseAuthenticator.isProperDouble(amount)) ERP.printDoubleError(desc);

        } while (!ExpenseAuthenticator.isProperDouble(amount));

        var expense = new Expense(
          name, desc, LocalDate.now(), Double.parseDouble(amount)
        );

        var newAdded = EXPENSE_REPOSITORY.insert(expense).orElse(null);

        System.out.println(newAdded != null ? "Expense has been added to the database! Expense ID: " + newAdded.id() : "An unexpected error occurred.");

    }

    public void delete() {
        String expenseId;

        System.out.println("\nEnter \"break\" to exit.\n");

        do {
            System.out.print("Enter expense ID to delete from your list here: ");
            expenseId = IN.nextLine();

            if (wantBreak(expenseId)) return;
            else if (!ExpenseAuthenticator.isProperInteger(expenseId)) ERP.printIntegerError(expenseId, "Expense ID");
            else if (!EXPENSE_REPOSITORY.existsById(Long.parseLong(expenseId))) System.out.println("Expense ID does not exists in your list.");

        } while (!ExpenseAuthenticator.isProperInteger(expenseId) || !EXPENSE_REPOSITORY.existsById(Long.parseLong(expenseId)));

        String ans;

        do {
            System.out.print("Are you sure you want to delete this? (Y/N): ");
            ans = IN.nextLine();

            if (wantBreak(ans)) return;
            else if (!ans.equalsIgnoreCase("Y") && !ans.equalsIgnoreCase("N")) System.out.println("Decision must be between Y/N.");

        } while (!ans.equalsIgnoreCase("Y") && !ans.equalsIgnoreCase("N"));

        if (ans.equalsIgnoreCase("Y")) {
            System.out.println(EXPENSE_REPOSITORY.delete(Integer.parseInt(expenseId)) ? "Successfully removed from your list." : "An unexpected error occurred.");
        } else {
            System.out.println("Deletion process has been cancelled.");
        }
    }

    public void updateConsole() {
        updateConsoleIsRunning = true;

        while (updateConsoleIsRunning) {
            updateMenu();
            handleUpdateChoice(IN.nextLine());
        }
    }

    public void updateMenu() {
        System.out.println("\nUpdate options: ");
        System.out.println("1. Expense name");
        System.out.println("2. Expense description");
        System.out.println("3. Expense date");
        System.out.println("4. Expense amount");
        System.out.println("5. Cancel update");
        System.out.print("Enter choice here: ");
    }

    public void handleUpdateChoice(String choice) {
        int c;

        if (choice.matches("\\d+") || choice.matches("-\\d+")) c = Integer.parseInt(choice);
        else {
            System.out.println("\"" + choice + "\" not a number!");
            return;
        }

        switch (c) {
            case 1 -> updateName();
            case 2 -> updateDesc();
            case 3 -> updateDate();
            case 4 -> updateAmount();
            case 5 -> updateConsoleIsRunning = false;
            default -> System.out.println("Input " + c + " cannot be resolved.");
        }

    }

    public void updateName() {
        String id;
        String newName;

        System.out.println("\nEnter \"break\" to exit.\n");

        do {
            System.out.print("Enter expense ID to update here: ");
            id = IN.nextLine();

            if (wantBreak(id)) return;
            else if (!ExpenseAuthenticator.isProperInteger(id)) ERP.printIntegerError(id, "Expense ID");
            else if (!EXPENSE_REPOSITORY.existsById(Long.parseLong(id))) System.out.println("Expense ID does not exists.");

        } while (!ExpenseAuthenticator.isProperInteger(id) || !EXPENSE_REPOSITORY.existsById(Long.parseLong(id)));

        var expenseItGot = EXPENSE_REPOSITORY.getExpenseById(Long.parseLong(id)).orElse(null);

        System.out.println(expenseItGot != null ? "\nExpense has been found! Update its name now!\n" : "An unexpected error occurred.");

        if (expenseItGot == null) return;

        do {
            System.out.print("Enter new name for your expense here: ");
            newName = IN.nextLine();

            if (wantBreak(newName)) return;
            else if (!ExpenseAuthenticator.isProperName(newName)) ERP.printNamingError(newName);

        } while (!ExpenseAuthenticator.isProperName(newName));

        boolean updated = EXPENSE_REPOSITORY.update(new Expense(
                Long.parseLong(id), newName, expenseItGot.desc(), expenseItGot.date(), expenseItGot.amount()
        ));

        System.out.println(updated ? "Name has been updated successfully!" : "An unexpected error occurred.");

    }

    public void updateDesc() {
        String id;
        String newDesc;

        System.out.println("\nEnter \"break\" to exit.\n");

        do {
            System.out.print("Enter expense ID to update here: ");
            id = IN.nextLine();

            if (wantBreak(id)) return;
            else if (!ExpenseAuthenticator.isProperInteger(id)) ERP.printIntegerError(id, "Expense ID");
            else if (!EXPENSE_REPOSITORY.existsById(Long.parseLong(id))) System.out.println("Expense ID does not exists.");

        } while (!ExpenseAuthenticator.isProperInteger(id) || !EXPENSE_REPOSITORY.existsById(Long.parseLong(id)));

        var expenseItGot = EXPENSE_REPOSITORY.getExpenseById(Long.parseLong(id)).orElse(null);

        System.out.println(expenseItGot != null ? "\nExpense has been found! Update its description now!\n" : "An unexpected error occurred.");

        if (expenseItGot == null) return;

        do {
            System.out.print("Enter new description for your expense here: ");
            newDesc = IN.nextLine();

            if (wantBreak(newDesc)) return;
            else if (!ExpenseAuthenticator.isProperDesc(newDesc)) ERP.printDescError(newDesc);

        } while (!ExpenseAuthenticator.isProperDesc(newDesc));

        boolean updated = EXPENSE_REPOSITORY.update(new Expense(
                Long.parseLong(id), expenseItGot.name(), newDesc, expenseItGot.date(), expenseItGot.amount()
        ));

        System.out.println(updated ? "Description has been updated successfully!" : "An unexpected error occurred.");
    }

    public void updateDate() {
        String id;
        String year;
        String month;
        String dayOfMonth;

        System.out.println("\nEnter \"break\" to exit.\n");

        do {
            System.out.print("Enter expense ID to update here: ");
            id = IN.nextLine();

            if (wantBreak(id)) return;
            else if (!ExpenseAuthenticator.isProperInteger(id)) ERP.printIntegerError(id, "Expense ID");
            else if (!EXPENSE_REPOSITORY.existsById(Long.parseLong(id))) System.out.println("Expense ID does not exists.");

        } while (!ExpenseAuthenticator.isProperInteger(id) || !EXPENSE_REPOSITORY.existsById(Long.parseLong(id)));

        var expenseItGot = EXPENSE_REPOSITORY.getExpenseById(Long.parseLong(id)).orElse(null);

        System.out.println(expenseItGot != null ? "\nExpense has been found! Update its date now!\n" : "An unexpected error occurred.");

        if (expenseItGot == null) return;

        do {
            System.out.print("Enter year here: ");
            year = IN.nextLine();

            if (wantBreak(year)) return;
            if (!ExpenseAuthenticator.isProperYear(year)) ERP.printYearError(year);

        } while (!ExpenseAuthenticator.isProperYear(year));

        do {
            System.out.print("Enter month here: ");
            month = IN.nextLine();

            if (wantBreak(month)) return;
            if (!ExpenseAuthenticator.isProperMonth(year, month)) ERP.printMonthError(year, month);

        } while (!ExpenseAuthenticator.isProperMonth(year, month));

        do {
            System.out.print("Enter day of the month here: ");
            dayOfMonth = IN.nextLine();

            if (wantBreak(dayOfMonth)) return;
            if (!ExpenseAuthenticator.isProperDayFromAMonth(year, month, dayOfMonth)) ERP.printDayError(year, month, dayOfMonth);

        } while (!ExpenseAuthenticator.isProperDayFromAMonth(year, month, dayOfMonth));

        if (month.length() == 1) month = "0" + month;
        if (dayOfMonth.length() == 1) dayOfMonth = "0" + dayOfMonth;

        var newExpense = new Expense(
                expenseItGot.id(), expenseItGot.name(), expenseItGot.desc(),
                LocalDate.parse(year + "-" + month + "-" + dayOfMonth), expenseItGot.amount()
        );

        boolean updated = EXPENSE_REPOSITORY.update(newExpense);

        System.out.println(updated ? "Expense date has been updated successfully!" : "An unexpected error occurred.");
    }

    public void updateAmount() {
        String id;
        String amount;

        System.out.println("\nEnter \"break\" to exit.\n");

        do {
            System.out.print("Enter expense ID to update here: ");
            id = IN.nextLine();

            if (wantBreak(id)) return;
            else if (!ExpenseAuthenticator.isProperInteger(id)) ERP.printIntegerError(id, "Expense ID");
            else if (!EXPENSE_REPOSITORY.existsById(Long.parseLong(id))) System.out.println("Expense ID does not exists.");

        } while (!ExpenseAuthenticator.isProperInteger(id) || !EXPENSE_REPOSITORY.existsById(Long.parseLong(id)));

        var expenseItGot = EXPENSE_REPOSITORY.getExpenseById(Long.parseLong(id)).orElse(null);

        System.out.println(expenseItGot != null ? "\nExpense has been found! Update its date now!\n" : "An unexpected error occurred.");

        if (expenseItGot == null) return;

        do {
            System.out.print("Enter new expense amount here: ");
            amount = IN.nextLine();

            if (wantBreak(amount)) return;
            else if (!ExpenseAuthenticator.isProperDouble(amount)) ERP.printDoubleError(amount);

        } while (!ExpenseAuthenticator.isProperDouble(amount));

        boolean updated = EXPENSE_REPOSITORY.update(new Expense(
                expenseItGot.id(), expenseItGot.name(), expenseItGot.desc(), expenseItGot.date(), Double.parseDouble(amount)
            )
        );

        System.out.println(updated ? "Expense amount has been updated successfully!" : "An unexpected error occurred.");
    }

    public void printAllExpenses() {
        print(EXPENSE_REPOSITORY.getExpenses());
    }

    public void printAllExpenseByDate() {
        String year;
        String month;
        String dayOfMonth;

        System.out.println("\nEnter \"break\" to exit.");
        System.out.println("Enter \"get_it\" to get the input already, and start filtering. \nFor example, you \"get_it\" from month, you'll get the filtered result that came from that month \nfrom indicated year.");

        do {
            System.out.print("Enter year here: ");
            year = IN.nextLine();

            if (wantBreak(year)) return;
            else if (wantGet(year)) System.out.println("\"get_it\" can only be applied to the sections lower than year.");
            else if (!ExpenseAuthenticator.isProperYear(year)) ERP.printYearError(year);

        } while (!ExpenseAuthenticator.isProperYear(year));

        do {
            System.out.print("Enter month here: ");
            month = IN.nextLine();

            if (wantBreak(month)) return;
            else if (wantGet(month)) {
                print(EXPENSE_REPOSITORY.getExpenseByDate(Integer.parseInt(year)));
                return;
            }
            else if (!ExpenseAuthenticator.isProperMonth(year, month)) ERP.printMonthError(year, month);

        } while (!ExpenseAuthenticator.isProperMonth(year, month));

        do {
            System.out.print("Enter day of the month here: ");
            dayOfMonth = IN.nextLine();

            if (wantBreak(dayOfMonth)) return;
            else if (wantGet(dayOfMonth)) {
                print(EXPENSE_REPOSITORY.getExpenseByDate(Integer.parseInt(year), Integer.parseInt(month)));
                return;
            } else if (!ExpenseAuthenticator.isProperDayFromAMonth(year, month, dayOfMonth)) ERP.printDayError(year, month, dayOfMonth);

        } while (!ExpenseAuthenticator.isProperDayFromAMonth(year, month, dayOfMonth));

        String date = year + "-" + (month.length() == 1 ? "0" + month : month) + "-" + (dayOfMonth.length() == 1 ? "0" + dayOfMonth : dayOfMonth);

        print(EXPENSE_REPOSITORY.getExpenseByDate(LocalDate.parse(date)));

    }

    public void createFileForAllExpenses() {
        boolean fileCreated = ExpenseFileCreator.createExpenseForAll(EXPENSE_REPOSITORY.getExpenses());

        System.out.println(fileCreated ? "File has been created successfully!" : "An unexpected error occurred.");
    }

    public void createFileForSpecificTime() {
        String year;
        String month;
        String dayOfMonth;

        System.out.println("\nEnter \"break\" to exit.");
        System.out.println("Enter \"create\" to get the file already, and start filtering. \nFor example, you \"create\" from month, you'll get the filtered file that came from that first input, which is the year.");

        do {
            System.out.print("Enter year here: ");
            year = IN.nextLine();

            if (wantBreak(year)) return;
            else if (wantCreate(year)) System.out.println("\"create\" can only be applied to the sections lower than year.");
            else if (!ExpenseAuthenticator.isProperYear(year)) ERP.printYearError(year);

        } while (!ExpenseAuthenticator.isProperYear(year));

        do {
            System.out.print("Enter month here: ");
            month = IN.nextLine();

            if (wantBreak(month)) return;
            else if (wantCreate(month)) {
                var fetched = EXPENSE_REPOSITORY.getExpenseByDate(Integer.parseInt(year));

                if (fetched.isEmpty()) {
                    System.out.println("You have no expenses during this time.");
                    return;
                }

                String fileName;

                System.out.println("Note: Don't add the file extension in the end of the file name. It is inferred by the system.");

                do {
                    System.out.print("Enter file name here: ");
                    fileName = IN.nextLine();

                    if (wantBreak(fileName)) return;
                    else if (fileName.isBlank() || ExpenseAuthenticator.hasSpecialChar(fileName)) System.out.println("Invalid file name format.");

                } while (fileName.isBlank() || ExpenseAuthenticator.hasSpecialChar(fileName));

                boolean created = ExpenseFileCreator.createFileForSpecificTimeframe(fetched, fileName);
                System.out.println(created ? "File has been created successfully!" : "An unexpected error occurred.");
                return;
            }
            else if (!ExpenseAuthenticator.isProperMonth(year, month)) ERP.printMonthError(year, month);

        } while (!ExpenseAuthenticator.isProperMonth(year, month));

        do {
            System.out.print("Enter day of the month here: ");
            dayOfMonth = IN.nextLine();

            if (wantBreak(dayOfMonth)) return;
            else if (wantCreate(dayOfMonth)) {
                var fetched = EXPENSE_REPOSITORY.getExpenseByDate(Integer.parseInt(year), Integer.parseInt(month));

                if (fetched.isEmpty()) {
                    System.out.println("You have no expenses during this time.");
                    return;
                }

                String fileName;

                System.out.println("Note: Don't add the file extension in the end of the file name. It is inferred by the system.");

                do {
                    System.out.print("Enter file name here: ");
                    fileName = IN.nextLine();

                    if (wantBreak(fileName)) return;
                    else if (fileName.isBlank() || ExpenseAuthenticator.hasSpecialChar(fileName)) System.out.println("Invalid file name format.");

                } while (fileName.isBlank() || ExpenseAuthenticator.hasSpecialChar(fileName));

                boolean created = ExpenseFileCreator.createFileForSpecificTimeframe(fetched, fileName);

                System.out.println(created ? "File has been created successfully!" : "An unexpected error occurred.");
                return;
            } else if (!ExpenseAuthenticator.isProperDayFromAMonth(year, month, dayOfMonth)) ERP.printDayError(year, month, dayOfMonth);

        } while (!ExpenseAuthenticator.isProperDayFromAMonth(year, month, dayOfMonth));

        String fileName;

        System.out.println("Note: Don't add the file extension in the end of the file name. It is inferred by the system.");

        do {
            System.out.print("Enter file name here: ");
            fileName = IN.nextLine();

            if (wantBreak(fileName)) return;
            else if (fileName.isBlank() || ExpenseAuthenticator.hasSpecialChar(fileName)) System.out.println("Invalid file name format.");

        } while (fileName.isBlank() || ExpenseAuthenticator.hasSpecialChar(fileName));

        String date = year + "-" + (month.length() == 1 ? "0" + month : month) + "-" + (dayOfMonth.length() == 1 ? "0" + dayOfMonth : dayOfMonth);
        boolean fileCreated = ExpenseFileCreator.createFileForSpecificTimeframe(EXPENSE_REPOSITORY.getExpenseByDate(LocalDate.parse(date)), fileName);

        System.out.println(fileCreated ? "File has been created successfully!" : "An unexpected error occurred.");
    }

    public void viewSummaryForAll() {
        int lineCount = 35;

        System.out.println("\n" + "=".repeat(lineCount));
        System.out.println("|" + " ".repeat(13) + "SUMMARY" + " ".repeat(13) + "|");
        System.out.println("-".repeat(lineCount));
        System.out.printf( "|%-20s|%-12.2f|%n", "Sum of Spending", EXPENSE_REPOSITORY.getSummaryForAllExpenses(GetType.SUM));
        System.out.println("-".repeat(lineCount));
        System.out.printf( "|%-20s|%-12.2f|%n", "Average Spending", EXPENSE_REPOSITORY.getSummaryForAllExpenses(GetType.AVG));
        System.out.println("-".repeat(lineCount));
        System.out.printf( "|%-20s|%-12.2f|%n", "Minimum Spending", EXPENSE_REPOSITORY.getSummaryForAllExpenses(GetType.MIN));
        System.out.println("-".repeat(lineCount));
        System.out.printf( "|%-20s|%-12.2f|%n", "Maximum Spending", EXPENSE_REPOSITORY.getSummaryForAllExpenses(GetType.MAX));
        System.out.println("=".repeat(lineCount));
    }

    public void viewSummaryForSpecificTime() {
        String year;
        String month;
        String day;

        System.out.println("\nEnter \"break\" to exit.");
        System.out.println("\nEnter \"get_it\" to stop and get the summary immediately.");
        do {
            System.out.print("Enter year here: ");
            year = IN.nextLine();

            if (wantBreak(year)) return;
            else if (wantGet(year)) System.out.println("Can't get summary when there is no given filter information.");
            else if (!ExpenseAuthenticator.isProperYear(year)) ERP.printYearError(year);

        } while (!ExpenseAuthenticator.isProperYear(year));

        int intYear = Integer.parseInt(year);

        do {
            System.out.print("Enter month here: ");
            month = IN.nextLine();

            if (wantBreak(month)) return;
            else if (wantGet(month)) {
                double[] summary = {
                        EXPENSE_REPOSITORY.getSummaryForSpecificTimeExpense(intYear, GetType.SUM),
                        EXPENSE_REPOSITORY.getSummaryForSpecificTimeExpense(intYear, GetType.AVG),
                        EXPENSE_REPOSITORY.getSummaryForSpecificTimeExpense(intYear, GetType.MIN),
                        EXPENSE_REPOSITORY.getSummaryForSpecificTimeExpense(intYear, GetType.MAX)
                };

                if (Arrays.stream(summary).anyMatch(s -> s == -1)) {
                    System.out.println("No summary for this timeframe.");
                    return;
                }

                printSummary(EXPENSE_REPOSITORY.getExpenseByDate(intYear), summary);
                return;
            }
            else if (!ExpenseAuthenticator.isProperMonth(year, month)) ERP.printMonthError(year, month);

        } while (!ExpenseAuthenticator.isProperMonth(year, month));

        int intMonth = Integer.parseInt(month);

        do {
            System.out.print("Enter day here: ");
            day = IN.nextLine();

            if (wantBreak(day)) return;
            else if (wantGet(day)) {
                double[] summary = {
                        EXPENSE_REPOSITORY.getSummaryForSpecificTimeExpense(intYear, intMonth, GetType.SUM),
                        EXPENSE_REPOSITORY.getSummaryForSpecificTimeExpense(intYear, intMonth, GetType.AVG),
                        EXPENSE_REPOSITORY.getSummaryForSpecificTimeExpense(intYear, intMonth, GetType.MIN),
                        EXPENSE_REPOSITORY.getSummaryForSpecificTimeExpense(intYear, intMonth, GetType.MAX)
                };

                if (Arrays.stream(summary).anyMatch(s -> s == -1)) {
                    System.out.println("No summary for this timeframe.");
                    return;
                }

                printSummary(EXPENSE_REPOSITORY.getExpenseByDate(intYear, intMonth), summary);
                return;
            }
            else if (!ExpenseAuthenticator.isProperDayFromAMonth(year, month, day)) ERP.printDayError(year, month, day);

        } while (!ExpenseAuthenticator.isProperDayFromAMonth(year, month, day));

        if (month.length() == 1) month = "0" + month;
        if (day.length()   == 1) day   = "0" + month;

        var date = LocalDate.parse(year + "-" + month + "-" + day);

        double[] summary = {
                EXPENSE_REPOSITORY.getSummaryForSpecificTimeExpense(date, GetType.SUM),
                EXPENSE_REPOSITORY.getSummaryForSpecificTimeExpense(date, GetType.AVG),
                EXPENSE_REPOSITORY.getSummaryForSpecificTimeExpense(date, GetType.MIN),
                EXPENSE_REPOSITORY.getSummaryForSpecificTimeExpense(date, GetType.MAX)
        };

        if (Arrays.stream(summary).anyMatch(s -> s == -1)) {
            System.out.println("No summary for this timeframe.");
            return;
        }

        printSummary(EXPENSE_REPOSITORY.getExpenseByDate(date), summary);

    }

    private static boolean wantBreak(String breakState) {
        return breakState.equalsIgnoreCase("break");
    }

    private static boolean wantGet(String getState) {
        return getState.equalsIgnoreCase("get_it");
    }

    private static boolean wantCreate(String createState) {
        return createState.equalsIgnoreCase("create");
    }

    private static void print(List<Expense> expenses) {
        if (expenses.isEmpty()) {
            System.out.println("Expense list is empty.");
            return;
        }

        System.out.println("==========================================================================================================");

        System.out.printf("| %-5s | %-20s | %-40s | %-15s | %-10s |%n",
                "ID", "Name", "Description", "Date", "Amount");

        System.out.println("----------------------------------------------------------------------------------------------------------");

        for (var exp : expenses) {
            System.out.printf("| %-5d | %-20s | %-40s | %-15s | %-10.2f |%n",
                    exp.id(),
                    exp.name(),
                    exp.desc(),
                    exp.date(),
                    exp.amount());
        }

        System.out.println("==========================================================================================================");
    }

    private static void printSummary(List<Expense> expenses, double[] summary) {
        print(expenses);

        System.out.printf("| %-89s | %-11.2f|%n", "Sum of all expense", summary[0]);
        System.out.println("----------------------------------------------------------------------------------------------------------");
        System.out.printf("| %-89s | %-11.2f|%n", "Average Spending", summary[1]);
        System.out.println("----------------------------------------------------------------------------------------------------------");
        System.out.printf("| %-89s | %-11.2f|%n", "Minimum Spending", summary[2]);
        System.out.println("----------------------------------------------------------------------------------------------------------");
        System.out.printf("| %-89s | %-11.2f|%n", "Maximum Spending", summary[3]);
        System.out.println("==========================================================================================================");
    }

    public void exit() {
        mainConsoleIsRunning = false;
    }

    public static void main(String[] args) {
        new App().mainConsole();
    }
}