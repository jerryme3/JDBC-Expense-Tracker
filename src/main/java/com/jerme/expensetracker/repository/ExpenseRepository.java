package com.jerme.expensetracker.repository;

import com.jerme.expensetracker.models.Expense;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExpenseRepository {

    //TODO make the method returns an Optional<Expense> then get the given ID in the DB for user access
    public Optional<Expense> insert(Expense expense) {
        var insert = """
                INSERT INTO expenses (expense_name, expense_desc, expense_date, expense_amount)
               VALUES (?, ?, ?, ?);""";

        try (var conn = DatabaseConnection.getConnection();
             var ps = conn.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, expense.name());
            ps.setString(2, expense.desc());
            ps.setDate(3, Date.valueOf(expense.date()));
            ps.setDouble(4, expense.amount());

            boolean inserted = ps.executeUpdate() > 0;

            try (var generatedKey = ps.getGeneratedKeys()) {
                if (generatedKey.next())
                    return inserted ? Optional.of(
                        new Expense(
                            generatedKey.getLong(1),
                            expense.name(),
                            expense.desc(),
                            expense.date(),
                            expense.amount()
                        )
                    ) : Optional.empty();
            }

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return Optional.empty();
    }

    public boolean delete(long id) {
        var delete = "DELETE FROM expenses WHERE expense_id = ?";

        try (var conn = DatabaseConnection.getConnection();
             var ps = conn.prepareStatement(delete)) {

            ps.setLong(1, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return false;
    }

    /**
    * Gets all the existing rows in the table: "expenses" and returns it as a list of expenses in ascending order by date
    */

    public List<Expense> getExpenses() {
        var read = new ArrayList<Expense>();
        var readAll = "SELECT * FROM expenses ORDER BY expense_date ASC";

        try (var conn = DatabaseConnection.getConnection();
        var ps = conn.prepareStatement(readAll)) {

            try (var rs = ps.executeQuery()) {
                while (rs.next()) read.add(map(rs));
            }

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return read;
    }

    public List<Expense> getExpenseByDate(LocalDate date) {
        var read = new ArrayList<Expense>();

        try (var conn = DatabaseConnection.getConnection();
        var ps = conn.prepareStatement("SELECT * FROM expenses WHERE expense_date = ? ORDER BY expense_id ASC")) {

            ps.setDate(1, Date.valueOf(date));

            try (var rs = ps.executeQuery()) {
                while (rs.next()) read.add(map(rs));
            }

        } catch (SQLException e) {
            System.err.printf(e.getMessage());
        }

        return read;
    }

    public List<Expense> getExpenseByDate(int year) {
        var read = new ArrayList<Expense>();

        try (var conn = DatabaseConnection.getConnection();
             var ps = conn.prepareStatement("SELECT * FROM expenses WHERE EXTRACT(YEAR FROM expense_date) = ? ORDER BY expense_id ASC")) {

            ps.setInt(1, year);

            try (var rs = ps.executeQuery()) {
                while (rs.next()) read.add(map(rs));
            }

        } catch (SQLException e) {
            System.err.printf(e.getMessage());
        }

        return read;
    }

    public boolean existsById(long id) {
        String checkExistence = "SELECT 1 FROM expenses WHERE expense_id = ?";

        try (var conn = DatabaseConnection.getConnection();
        var ps = conn.prepareStatement(checkExistence)) {

            ps.setLong(1, id);

            try (var rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return false;
    }

    public boolean update(Expense expense) {
        var update = """
                UPDATE expenses
                SET expense_name = ?,
                expense_desc = ?,
                expense_date = ?,
                expense_amount = ?
                WHERE expense_id = ?""";

        try (var conn = DatabaseConnection.getConnection();
        var ps = conn.prepareStatement(update)) {

            ps.setString(1, expense.name());
            ps.setString(2, expense.desc());
            ps.setDate(3, Date.valueOf(expense.date()));
            ps.setDouble(4, expense.amount());
            ps.setLong(5, expense.id());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return false;
    }

    public Optional<Expense> getExpenseById(long id) {
        String get = "SELECT * FROM expenses WHERE expense_id = ?";

        try (var conn = DatabaseConnection.getConnection();
             var ps = conn.prepareStatement(get)) {

            ps.setLong(1, id);

            try (var rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return Optional.empty();
    }

    public Expense map(ResultSet rs) throws SQLException {
        return new Expense(
                rs.getLong("expense_id"),
                rs.getString("expense_name"),
                rs.getString("expense_desc"),
                rs.getDate("expense_date").toLocalDate(),
                rs.getDouble("expense_amount")
        );
    }
}
