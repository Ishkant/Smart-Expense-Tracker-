package com.expensetracker.service.impl;

import com.expensetracker.dto.ExpenseDTO;
import com.expensetracker.entity.Expense;
import com.expensetracker.entity.User;
import com.expensetracker.exception.BadRequestException;
import com.expensetracker.exception.ResourceNotFoundException;
import com.expensetracker.repository.ExpenseRepository;
import com.expensetracker.repository.UserRepository;
import com.expensetracker.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;

@Service
public class ExpenseServiceImpl implements ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    private static final List<String> DEFAULT_CATEGORIES = Arrays.asList(
            "Food & Dining", "Transportation", "Shopping", "Entertainment",
            "Bills & Utilities", "Healthcare", "Education", "Travel",
            "Personal Care", "Housing", "Salary", "Freelance", "Investment", "Other"
    );

    @Override
    @Transactional
    public ExpenseDTO.Response addExpense(Long userId, ExpenseDTO.Request request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Expense expense = Expense.builder()
                .title(request.getTitle())
                .amount(request.getAmount())
                .category(request.getCategory())
                .date(request.getDate())
                .description(request.getDescription())
                .paymentMethod(request.getPaymentMethod())
                .type(request.getType())
                .user(user)
                .build();

        return ExpenseDTO.Response.fromEntity(expenseRepository.save(expense));
    }

    @Override
    @Transactional
    public ExpenseDTO.Response updateExpense(Long userId, Long expenseId, ExpenseDTO.Request request) {
        Expense expense = getExpenseForUser(userId, expenseId);

        expense.setTitle(request.getTitle());
        expense.setAmount(request.getAmount());
        expense.setCategory(request.getCategory());
        expense.setDate(request.getDate());
        expense.setDescription(request.getDescription());
        expense.setPaymentMethod(request.getPaymentMethod());
        expense.setType(request.getType());

        return ExpenseDTO.Response.fromEntity(expenseRepository.save(expense));
    }

    @Override
    @Transactional
    public void deleteExpense(Long userId, Long expenseId) {
        Expense expense = getExpenseForUser(userId, expenseId);
        expenseRepository.delete(expense);
    }

    @Override
    public ExpenseDTO.Response getExpenseById(Long userId, Long expenseId) {
        return ExpenseDTO.Response.fromEntity(getExpenseForUser(userId, expenseId));
    }

    @Override
    public List<ExpenseDTO.Response> getAllExpenses(Long userId) {
        return expenseRepository.findByUserIdOrderByDateDesc(userId)
                .stream().map(ExpenseDTO.Response::fromEntity).collect(Collectors.toList());
    }

    @Override
    public List<ExpenseDTO.Response> getExpensesByCategory(Long userId, String category) {
        return expenseRepository.findByUserIdAndCategoryOrderByDateDesc(userId, category)
                .stream().map(ExpenseDTO.Response::fromEntity).collect(Collectors.toList());
    }

    @Override
    public List<ExpenseDTO.Response> getExpensesByMonth(Long userId, int month, int year) {
        return expenseRepository.findByUserIdAndMonthAndYear(userId, month, year)
                .stream().map(ExpenseDTO.Response::fromEntity).collect(Collectors.toList());
    }

    @Override
    public List<ExpenseDTO.Response> searchExpenses(Long userId, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new BadRequestException("Search keyword cannot be empty");
        }
        return expenseRepository.searchExpenses(userId, keyword.trim())
                .stream().map(ExpenseDTO.Response::fromEntity).collect(Collectors.toList());
    }

    @Override
    public List<String> getAllCategories() {
        return DEFAULT_CATEGORIES;
    }

    private Expense getExpenseForUser(Long userId, Long expenseId) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + expenseId));
        if (!expense.getUser().getId().equals(userId)) {
            throw new BadRequestException("Access denied: You don't own this expense");
        }
        return expense;
    }
}
