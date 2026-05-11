package com.expensetracker.service;

import com.expensetracker.dto.ExpenseDTO;

import java.util.List;

public interface ExpenseService {
    ExpenseDTO.Response addExpense(Long userId, ExpenseDTO.Request request);
    ExpenseDTO.Response updateExpense(Long userId, Long expenseId, ExpenseDTO.Request request);
    void deleteExpense(Long userId, Long expenseId);
    ExpenseDTO.Response getExpenseById(Long userId, Long expenseId);
    List<ExpenseDTO.Response> getAllExpenses(Long userId);
    List<ExpenseDTO.Response> getExpensesByCategory(Long userId, String category);
    List<ExpenseDTO.Response> getExpensesByMonth(Long userId, int month, int year);
    List<ExpenseDTO.Response> searchExpenses(Long userId, String keyword);
    List<String> getAllCategories();
}
