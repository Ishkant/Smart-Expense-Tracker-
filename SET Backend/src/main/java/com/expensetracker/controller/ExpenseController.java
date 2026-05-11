package com.expensetracker.controller;

import com.expensetracker.config.CurrentUserHelper;
import com.expensetracker.dto.ApiResponse;
import com.expensetracker.dto.ExpenseDTO;
import com.expensetracker.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private CurrentUserHelper currentUserHelper;

    // Add expense or income
    @PostMapping
    public ResponseEntity<ApiResponse<ExpenseDTO.Response>> addExpense(
            @Valid @RequestBody ExpenseDTO.Request request) {
        Long userId = currentUserHelper.getCurrentUserId();
        ExpenseDTO.Response response = expenseService.addExpense(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Expense added successfully", response));
    }

    // Update expense
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ExpenseDTO.Response>> updateExpense(
            @PathVariable Long id,
            @Valid @RequestBody ExpenseDTO.Request request) {
        Long userId = currentUserHelper.getCurrentUserId();
        ExpenseDTO.Response response = expenseService.updateExpense(userId, id, request);
        return ResponseEntity.ok(ApiResponse.success("Expense updated successfully", response));
    }

    // Delete expense
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteExpense(@PathVariable Long id) {
        Long userId = currentUserHelper.getCurrentUserId();
        expenseService.deleteExpense(userId, id);
        return ResponseEntity.ok(ApiResponse.success("Expense deleted successfully", null));
    }

    // Get single expense
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ExpenseDTO.Response>> getExpense(@PathVariable Long id) {
        Long userId = currentUserHelper.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(expenseService.getExpenseById(userId, id)));
    }

    // Get all expenses
    @GetMapping
    public ResponseEntity<ApiResponse<List<ExpenseDTO.Response>>> getAllExpenses() {
        Long userId = currentUserHelper.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(expenseService.getAllExpenses(userId)));
    }

    // Get expenses by category
    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<List<ExpenseDTO.Response>>> getByCategory(
            @PathVariable String category) {
        Long userId = currentUserHelper.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(expenseService.getExpensesByCategory(userId, category)));
    }

    // Get expenses by month and year
    @GetMapping("/month")
    public ResponseEntity<ApiResponse<List<ExpenseDTO.Response>>> getByMonth(
            @RequestParam int month,
            @RequestParam int year) {
        Long userId = currentUserHelper.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(expenseService.getExpensesByMonth(userId, month, year)));
    }

    // Search expenses
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ExpenseDTO.Response>>> searchExpenses(
            @RequestParam String keyword) {
        Long userId = currentUserHelper.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(expenseService.searchExpenses(userId, keyword)));
    }

    // Get all available categories
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<String>>> getCategories() {
        return ResponseEntity.ok(ApiResponse.success(expenseService.getAllCategories()));
    }
}
