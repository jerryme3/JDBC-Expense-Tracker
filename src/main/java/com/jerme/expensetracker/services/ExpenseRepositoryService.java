package com.jerme.expensetracker.services;

import com.jerme.expensetracker.utils.ExpenseAuthenticator;

import java.time.LocalDate;

import static com.jerme.expensetracker.utils.ExpenseAuthenticator.isLeapYear;

public class ExpenseRepositoryService {

    public void printNamingError(String name) {
        if (name == null || name.isBlank()) {
            System.out.println("Expense name should not be blank.");
            return;
        }

        if (name.length() > 20) {
            System.out.println("Expense name length should not be exceed 20 characters.");
        }
    }

    public void printDescError(String desc) {
        if (desc == null || desc.isBlank()) {
            System.out.println("Expense description should not be blank.");
            return;
        }

        if (desc.length() > 40) {
            System.out.println("Expense description length should not be exceed 40 characters.");
        }
    }

    public void printDoubleError(String amount) {
        if (amount == null || amount.isBlank()) {
            System.out.println("Expense amount should not be blank.");
            return;
        }

        if (!amount.matches("\\d+") || !amount.matches("\\d+\\.\\d+")) {
            System.out.println("Expense amount should be in proper amount or decimal format.");
        }
    }

    public void printIntegerError(String integer, String whatToCall) {
        if (integer == null || integer.isBlank()) {
            System.out.println(whatToCall + " should not be blank.");
            return;
        }

        if (!integer.matches("\\d+")) {
            System.out.println(whatToCall + " should be in proper integer format.");
        }
    }

    public void printYearError(String year) {
        if (year == null || year.isBlank()) {
            System.out.println("Year should not be blank.");
            return;
        }

        if (!year.matches("\\d+")) {
            System.out.println("Year should be in proper year format.");
            return;
        }

        if (year.length() < 4) {
            System.out.println("You aren't even alive in this time, bro.");
            return;
        }

        if (year.length() > 4) {
            System.out.println("Do you see yourself in future spending to this?");
            return;
        }

        int intYear = Integer.parseInt(year);

        if (intYear < 1900) {
            System.out.println("Cannot accept year input that is too old!");
        } else if (intYear > 2026) {
            System.out.println("Cannot accept year input that from the future!");
        }
    }

    public void printMonthError(String year, String month) {
        if ((year == null || year.isBlank()) || (month == null || month.isBlank())) {
            System.out.println("Year or month should not be blank");
            return;
        }

        if (!month.matches("\\d{1,2}")) {
            System.out.println("Month should be in proper month format.");
            return;
        }

        int intMonth = Integer.parseInt(month);

        if (intMonth < 1 || intMonth > 12) {
            System.out.println("Month input is out of range. It must be between 1-12.");
            return;
        }

        if (year.equalsIgnoreCase(String.valueOf(LocalDate.now().getYear()))) {
            if (intMonth > LocalDate.now().getMonthValue()) {
                System.out.println("Invalid month.");
            }
        }
    }

    public void printDayError(String year, String month, String day) {
        if (!ExpenseAuthenticator.isProperYear(year)) {
            printYearError(year);
            return;
        }

        if (!ExpenseAuthenticator.isProperMonth(month)) {
            printMonthError(year, month);
            return;
        }

        if (day == null || day.isBlank()) {
            System.out.println("Day of the month should not be blank.");
            return;
        }

        if (!day.matches("\\d{1,2}")) {
            System.out.println("Day should be in the proper day format.");
            return;
        }

        if (month.length() == 1) month = "0" + month;

        int ceiling = switch (month) {
            case "04", "06", "09", "11" -> 30;
            case "01", "03", "05", "07", "08", "10", "12" -> 31;
            case "02" -> {
                if (isLeapYear(Integer.parseInt(year))) yield 29;
                else yield 28;
            }
            default -> -1;
        };

        int intDay = Integer.parseInt(day);

        if (intDay < 1 || intDay > ceiling) {
            System.out.println("Day must be between 1-" + ceiling + ".");
        }
    }
}
