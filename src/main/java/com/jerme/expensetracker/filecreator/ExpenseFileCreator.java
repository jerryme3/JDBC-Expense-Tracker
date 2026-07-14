package com.jerme.expensetracker.filecreator;

import com.jerme.expensetracker.models.Expense;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ExpenseFileCreator {

    public static boolean createExpenseForAll(List<Expense> expenses) {
        Path path = Path.of("C:\\Users\\Jerryme\\Downloads\\my_expenses");

        try {
            Files.createDirectories(path);

            var pathFile =  path.resolve("all_of_my_expenses.txt");

            try (var fw = new FileWriter(pathFile.toFile())) {
                fw.write(getFormattedTable(expenses));

                return true;
            }

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        return false;
    }

    public static boolean createFileForSpecificTimeframe(List<Expense> expenses, String nameOfFile) {
        Path path = Path.of("C:\\Users\\Jerryme\\Downloads\\my_expenses\\specific");

        try {
            Files.createDirectories(path);

            var filePath = path.resolve(nameOfFile + ".txt");

            try (var fw = new FileWriter(filePath.toFile())) {
                fw.write(getFormattedTable(expenses));

                return true;
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        return false;
    }

    private static String getFormattedTable(List<Expense> expenses) {
        var expense = new StringBuilder();

        expense.append("==========================================================================================================\n");
        expense.append(String.format("| %-5s | %-20s | %-40s | %-15s | %-10s |%n",
                "ID", "Name", "Description", "Date", "Amount"));
        expense.append("----------------------------------------------------------------------------------------------------------\n");

        for (var exp : expenses) {
            expense.append(String.format("| %-5d | %-20s | %-40s | %-15s | %-10.2f |%n",
                    exp.id(),
                    exp.name(),
                    exp.desc(),
                    exp.date(),
                    exp.amount()));
        }

        expense.append("==========================================================================================================");

        return expense.toString();
    }
}
