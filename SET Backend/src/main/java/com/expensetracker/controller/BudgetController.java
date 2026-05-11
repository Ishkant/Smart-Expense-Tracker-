package com.expensetracker.controller;

import com.expensetracker.config.CurrentUserHelper;
import com.expensetracker.dto.ApiResponse;
import com.expensetracker.dto.BudgetDTO;
import com.expensetracker.service.BudgetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    @Autowired
    private BudgetService budgetService;

    @Autowired
    private CurrentUserHelper currentUserHelper;

    // Set or update a budget for a category/month
    @PostMapping
    public ResponseEntity<ApiResponse<BudgetDTO.Response>> setBudget(
            @Valid @RequestBody BudgetDTO.Request request) {
        Long userId = currentUserHelper.getCurrentUserId();
        BudgetDTO.Response response = budgetService.setBudget(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Budget set successfully", response));
    }

    // Update budget by ID
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BudgetDTO.Response>> updateBudget(
            @PathVariable Long id,
            @Valid @RequestBody BudgetDTO.Request request) {
        Long userId = currentUserHelper.getCurrentUserId();
        BudgetDTO.Response response = budgetService.updateBudget(userId, id, request);
        return ResponseEntity.ok(ApiResponse.success("Budget updated successfully", response));
    }

    // Delete budget
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBudget(@PathVariable Long id) {
        Long userId = currentUserHelper.getCurrentUserId();
        budgetService.deleteBudget(userId, id);
        return ResponseEntity.ok(ApiResponse.success("Budget deleted successfully", null));
    }

    // Get budgets for a specific month
    @GetMapping("/month")
    public ResponseEntity<ApiResponse<List<BudgetDTO.Response>>> getBudgetsByMonth(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        Long userId = currentUserHelper.getCurrentUserId();
        int m = (month != null) ? month : LocalDate.now().getMonthValue();
        int y = (year != null) ? year : LocalDate.now().getYear();
        return ResponseEntity.ok(ApiResponse.success(budgetService.getBudgetsByMonth(userId, m, y)));
    }

    // Get all budgets
    @GetMapping
    public ResponseEntity<ApiResponse<List<BudgetDTO.Response>>> getAllBudgets() {
        Long userId = currentUserHelper.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(budgetService.getAllBudgets(userId)));
    }

    // Analytics endpoint - the smart dashboard data
    @GetMapping("/analytics")
    public ResponseEntity<ApiResponse<BudgetDTO.AnalyticsResponse>> getAnalytics(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        Long userId = currentUserHelper.getCurrentUserId();
        int m = (month != null) ? month : LocalDate.now().getMonthValue();
        int y = (year != null) ? year : LocalDate.now().getYear();
        BudgetDTO.AnalyticsResponse analytics = budgetService.getAnalytics(userId, m, y);
        return ResponseEntity.ok(ApiResponse.success("Analytics fetched successfully", analytics));
    }
}
