package com.expensetracker.service;

import com.expensetracker.dto.BudgetDTO;

import java.util.List;

public interface BudgetService {
    BudgetDTO.Response setBudget(Long userId, BudgetDTO.Request request);
    BudgetDTO.Response updateBudget(Long userId, Long budgetId, BudgetDTO.Request request);
    void deleteBudget(Long userId, Long budgetId);
    List<BudgetDTO.Response> getBudgetsByMonth(Long userId, int month, int year);
    List<BudgetDTO.Response> getAllBudgets(Long userId);
    BudgetDTO.AnalyticsResponse getAnalytics(Long userId, int month, int year);
}
