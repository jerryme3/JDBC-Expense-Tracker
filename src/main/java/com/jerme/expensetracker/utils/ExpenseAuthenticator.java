package com.jerme.expensetracker.utils;

import java.time.LocalDate;

public class ExpenseAuthenticator {

    public static boolean isProperName(String name) {
        return name != null && !name.isBlank() && name.length() <= 20;
    }

    public static boolean isProperDesc(String desc) {
        return desc != null && !desc.isBlank() && desc.length() <= 40;
    }

    /**
     * This method is intended to check if the month has proper format and
     * if its lesser or equal to the current year's current month
     */
    public static boolean isProperMonth(String month) {
        if (month == null || month.isBlank()) return false;

        if (month.matches("\\d{1,2}")) {
            int intMonth = Integer.parseInt(month);

            if (intMonth < 1 || intMonth > 12) return false;

            return intMonth <= LocalDate.now().getMonthValue();
        }

        return false;
    }

    public static boolean isProperYear(String year) {
        if (year == null || year.isBlank()) return false;

        if (isProperInteger(year)) {
            int intYear = Integer.parseInt(year);

            return intYear >= 1901 && intYear <= LocalDate.now().getYear();
        }

        return false;
    }

    /**
     * This method is intended to check if the month has proper format and
     * if its lesser or equal to the current year's current month. In addition,
     * it also handles the months for the years that had passed.
     */
    public static boolean isProperMonth(String year, String month) {
        if (!isProperYear(year) || month == null || month.isBlank()) return false;

        int intYear = Integer.parseInt(year);

        if (!month.matches("\\d{1,2}")) return false;

        int intMonth = Integer.parseInt(month);

        if (intMonth < 1 || intMonth > 12) return false;

        if (intYear == LocalDate.now().getYear()) {
            return intMonth <= LocalDate.now().getMonthValue();
        }

        return intYear <= LocalDate.now().getYear();
    }

    /**
     * This method checks if the format of the day in the month is correct and is in the proper range in the given month.
     * @param year passed to be checked if the year is a leap year. If yes, February will have a ceiling of 29, otherwise, it'll have 28.
     * @param month passed to be the basis of the variable ceiling.
     * @param day the one that will be assessed based on the previous parameters.
     * @return boolean
     */
    public static boolean isProperDayFromAMonth(String year, String month, String day) {
        if (month == null || month.isBlank() || day == null || day.isBlank() || !isProperYear(year)) return false;

        if (!isProperInteger(month)) return false;

        //Concatenates a "0" in front to make it conform to the right format of months in the YYYY-MM-DD format.
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

        if (ceiling == -1) return false;

        if (!isProperInteger(day)) return false;

        int intDay = Integer.parseInt(day);

        return intDay >= 1 && intDay <= ceiling;
    }

    public static boolean isProperInteger(String integer) {
        if (integer == null || integer.isBlank()) return false;

        if (!integer.matches("\\d{1,10}")) return false;

        long integer1 = Long.parseLong(integer);

        return integer1 <= (long) Integer.MAX_VALUE;
    }

    public static boolean isProperDouble(String decimal) {
        if (decimal == null || decimal.isBlank()) return false;

        if (!decimal.matches("\\d{1,10}\\.\\d+") && !isProperInteger(decimal)) return false;

        return Double.parseDouble(decimal) <= Double.MAX_VALUE;

    }

    public static boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 4 == 0 && year % 400 == 0);
    }

    public static boolean hasSpecialChar(String input) {
        return input
                .chars()
                .mapToObj(i -> (char) i)
                .anyMatch(ch -> !Character.isDigit(ch) && !Character.isLetter(ch) && !Character.isWhitespace(ch));
    }
}