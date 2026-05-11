package com.expensetracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

public class BudgetDTO {

    @Data
    public static class Request {
        @NotBlank(message = "Category is required")
        private String category;

        @NotNull(message = "Limit amount is required")
        @Positive(message = "Limit must be positive")
        private Double limitAmount;

        @NotNull
        private Integer month;

        @NotNull
        private Integer year;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private String category;
        private Double limitAmount;
        private Double spent;
        private Double remaining;
        private Double percentageUsed;
        private Integer month;
        private Integer year;
        private String status; // "SAFE", "WARNING", "EXCEEDED"
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnalyticsResponse {
        private Double totalExpense;
        private Double totalIncome;
        private Double savings;
        private Map<String, Double> categoryBreakdown;
        private List<DailyData> dailyExpenses;
        private List<MonthlyData> monthlyTrend;
        private List<BudgetDTO.Response> budgetStatus;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyData {
        private int day;
        private double amount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyData {
        private int month;
        private String monthName;
        private double amount;
    }
}
