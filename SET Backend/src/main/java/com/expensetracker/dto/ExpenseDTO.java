package com.expensetracker.dto;

import com.expensetracker.entity.Expense;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ExpenseDTO {

    @Data
    public static class Request {
        @NotBlank(message = "Title is required")
        private String title;

        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be positive")
        private Double amount;

        @NotBlank(message = "Category is required")
        private String category;

        @NotNull(message = "Date is required")
        private LocalDate date;

        private String description;
        private Expense.PaymentMethod paymentMethod;

        @NotNull(message = "Type is required (EXPENSE or INCOME)")
        private Expense.ExpenseType type;
    }

    @Data
    public static class Response {
        private Long id;
        private String title;
        private Double amount;
        private String category;
        private LocalDate date;
        private String description;
        private Expense.PaymentMethod paymentMethod;
        private Expense.ExpenseType type;
        private LocalDateTime createdAt;

        public static Response fromEntity(Expense expense) {
            Response r = new Response();
            r.setId(expense.getId());
            r.setTitle(expense.getTitle());
            r.setAmount(expense.getAmount());
            r.setCategory(expense.getCategory());
            r.setDate(expense.getDate());
            r.setDescription(expense.getDescription());
            r.setPaymentMethod(expense.getPaymentMethod());
            r.setType(expense.getType());
            r.setCreatedAt(expense.getCreatedAt());
            return r;
        }
    }
}
