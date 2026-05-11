package com.expensetracker.service.impl;

import com.expensetracker.dto.BudgetDTO;
import com.expensetracker.entity.Budget;
import com.expensetracker.entity.User;
import com.expensetracker.exception.BadRequestException;
import com.expensetracker.exception.ResourceNotFoundException;
import com.expensetracker.repository.BudgetRepository;
import com.expensetracker.repository.ExpenseRepository;
import com.expensetracker.repository.UserRepository;
import com.expensetracker.service.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BudgetServiceImpl implements BudgetService {

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public BudgetDTO.Response setBudget(Long userId, BudgetDTO.Request request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // If budget already exists for category/month/year, update it
        Optional<Budget> existing = budgetRepository.findByUserIdAndCategoryAndMonthAndYear(
                userId, request.getCategory(), request.getMonth(), request.getYear());

        Budget budget;
        if (existing.isPresent()) {
            budget = existing.get();
            budget.setLimitAmount(request.getLimitAmount());
        } else {
            budget = Budget.builder()
                    .category(request.getCategory())
                    .limitAmount(request.getLimitAmount())
                    .month(request.getMonth())
                    .year(request.getYear())
                    .user(user)
                    .build();
        }

        budget = budgetRepository.save(budget);
        return buildBudgetResponse(budget, userId);
    }

    @Override
    @Transactional
    public BudgetDTO.Response updateBudget(Long userId, Long budgetId, BudgetDTO.Request request) {
        Budget budget = getBudgetForUser(userId, budgetId);
        budget.setLimitAmount(request.getLimitAmount());
        budget.setCategory(request.getCategory());
        budget.setMonth(request.getMonth());
        budget.setYear(request.getYear());
        budgetRepository.save(budget);
        return buildBudgetResponse(budget, userId);
    }

    @Override
    @Transactional
    public void deleteBudget(Long userId, Long budgetId) {
        Budget budget = getBudgetForUser(userId, budgetId);
        budgetRepository.delete(budget);
    }

    @Override
    public List<BudgetDTO.Response> getBudgetsByMonth(Long userId, int month, int year) {
        return budgetRepository.findByUserIdAndMonthAndYear(userId, month, year)
                .stream()
                .map(b -> buildBudgetResponse(b, userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<BudgetDTO.Response> getAllBudgets(Long userId) {
        return budgetRepository.findByUserId(userId)
                .stream()
                .map(b -> buildBudgetResponse(b, userId))
                .collect(Collectors.toList());
    }

    @Override
    public BudgetDTO.AnalyticsResponse getAnalytics(Long userId, int month, int year) {
        // Total expense & income for the month
        Double totalExpense = expenseRepository.findTotalExpenseByMonth(userId, month, year);
        Double totalIncome = expenseRepository.findTotalIncomeByMonth(userId, month, year);
        totalExpense = totalExpense != null ? totalExpense : 0.0;
        totalIncome = totalIncome != null ? totalIncome : 0.0;
        double savings = totalIncome - totalExpense;

        // Category-wise breakdown
        List<Object[]> categoryData = expenseRepository.findCategoryWiseSummary(userId, month, year);
        Map<String, Double> categoryBreakdown = new LinkedHashMap<>();
        for (Object[] row : categoryData) {
            categoryBreakdown.put((String) row[0], ((Number) row[1]).doubleValue());
        }

        // Daily expense data for chart
        List<Object[]> dailyData = expenseRepository.findDailyExpenseByMonth(userId, month, year);
        List<BudgetDTO.DailyData> dailyExpenses = dailyData.stream()
                .map(row -> new BudgetDTO.DailyData(
                        ((Number) row[0]).intValue(),
                        ((Number) row[1]).doubleValue()))
                .collect(Collectors.toList());

        // Monthly trend (current year)
        List<Object[]> monthlyData = expenseRepository.findMonthlyExpenseSummary(userId, year);
        List<BudgetDTO.MonthlyData> monthlyTrend = monthlyData.stream()
                .map(row -> {
                    int m = ((Number) row[0]).intValue();
                    String monthName = Month.of(m).getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
                    return new BudgetDTO.MonthlyData(m, monthName, ((Number) row[1]).doubleValue());
                })
                .collect(Collectors.toList());

        // Budget status for the month
        List<BudgetDTO.Response> budgetStatus = getBudgetsByMonth(userId, month, year);

        return new BudgetDTO.AnalyticsResponse(
                totalExpense, totalIncome, savings,
                categoryBreakdown, dailyExpenses, monthlyTrend, budgetStatus
        );
    }

    private BudgetDTO.Response buildBudgetResponse(Budget budget, Long userId) {
        Double spent = expenseRepository.findTotalExpenseByMonth(userId, budget.getMonth(), budget.getYear());
        // Filter by category
        List<Object[]> catData = expenseRepository.findCategoryWiseSummary(userId, budget.getMonth(), budget.getYear());
        double categorySpent = catData.stream()
                .filter(row -> budget.getCategory().equals(row[0]))
                .mapToDouble(row -> ((Number) row[1]).doubleValue())
                .sum();

        double remaining = budget.getLimitAmount() - categorySpent;
        double percentageUsed = budget.getLimitAmount() > 0
                ? (categorySpent / budget.getLimitAmount()) * 100 : 0;

        String status;
        if (percentageUsed >= 100) status = "EXCEEDED";
        else if (percentageUsed >= 80) status = "WARNING";
        else status = "SAFE";

        return new BudgetDTO.Response(
                budget.getId(), budget.getCategory(), budget.getLimitAmount(),
                categorySpent, remaining, percentageUsed,
                budget.getMonth(), budget.getYear(), status
        );
    }

    private Budget getBudgetForUser(Long userId, Long budgetId) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found with id: " + budgetId));
        if (!budget.getUser().getId().equals(userId)) {
            throw new BadRequestException("Access denied: You don't own this budget");
        }
        return budget;
    }
}
