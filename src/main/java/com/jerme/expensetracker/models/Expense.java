package com.jerme.expensetracker.models;

import java.time.LocalDate;

public record Expense(
        long id,
        String name,
        String desc,
        LocalDate date,
        double amount
) {

    public Expense(
        String name,
        String desc,
        LocalDate date,
        double amount) {
        this(-1L, name, desc, date, amount);
    }

}
