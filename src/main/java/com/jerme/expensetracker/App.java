package com.jerme.expensetracker;

import com.jerme.expensetracker.models.Expense;
import com.jerme.expensetracker.repository.ExpenseRepository;
import com.jerme.expensetracker.services.ExpenseRepositoryService;
import com.jerme.expensetracker.utils.ExpenseAuthenticator;

import java.time.LocalDate;
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
        System.out.println("8. Exit");
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
            case 8 -> exit();
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
        System.out.println("Enter \"get_it\" to get the input already, and start filtering. \nFor example, you \"get it\" from year, you'll get the filtered result that came from that year.");

        do {
            System.out.print("Enter year here: ");
            year = IN.nextLine();

            if (wantBreak(year)) return;
            else if (wantGet(year)) System.out.println("\"get_it\" can only be applied to the sections lower than year.");
            else if (!ExpenseAuthenticator.isProperYear(year)) ERP.printYearError(year);

        } while (!ExpenseAuthenticator.isProperYear(year));

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
    private static boolean wantBreak(String breakState) {
        return breakState.equalsIgnoreCase("break");
    }

    private static boolean wantGet(String getState) {
        return getState.equalsIgnoreCase("get_it");
    }

    public void exit() {
        mainConsoleIsRunning = false;
    }

    public static void main(String[] args) {
        new App().mainConsole();
    }
}