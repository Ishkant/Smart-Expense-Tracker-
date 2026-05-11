package com.expensetracker.repository;

import com.expensetracker.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByUserIdOrderByDateDesc(Long userId);

    List<Expense> findByUserIdAndCategoryOrderByDateDesc(Long userId, String category);

    List<Expense> findByUserIdAndDateBetweenOrderByDateDesc(Long userId, LocalDate start, LocalDate end);

    List<Expense> findByUserIdAndTypeOrderByDateDesc(Long userId, Expense.ExpenseType type);

    @Query("SELECT e FROM Expense e WHERE e.user.id = :userId " +
           "AND MONTH(e.date) = :month AND YEAR(e.date) = :year ORDER BY e.date DESC")
    List<Expense> findByUserIdAndMonthAndYear(@Param("userId") Long userId,
                                              @Param("month") int month,
                                              @Param("year") int year);

    @Query("SELECT e.category, SUM(e.amount) FROM Expense e " +
           "WHERE e.user.id = :userId AND e.type = 'EXPENSE' " +
           "AND MONTH(e.date) = :month AND YEAR(e.date) = :year " +
           "GROUP BY e.category")
    List<Object[]> findCategoryWiseSummary(@Param("userId") Long userId,
                                           @Param("month") int month,
                                           @Param("year") int year);

    @Query("SELECT SUM(e.amount) FROM Expense e " +
           "WHERE e.user.id = :userId AND e.type = 'EXPENSE' " +
           "AND MONTH(e.date) = :month AND YEAR(e.date) = :year")
    Double findTotalExpenseByMonth(@Param("userId") Long userId,
                                   @Param("month") int month,
                                   @Param("year") int year);

    @Query("SELECT SUM(e.amount) FROM Expense e " +
           "WHERE e.user.id = :userId AND e.type = 'INCOME' " +
           "AND MONTH(e.date) = :month AND YEAR(e.date) = :year")
    Double findTotalIncomeByMonth(@Param("userId") Long userId,
                                  @Param("month") int month,
                                  @Param("year") int year);

    @Query("SELECT FUNCTION('DAY', e.date) as day, SUM(e.amount) as total " +
           "FROM Expense e WHERE e.user.id = :userId AND e.type = 'EXPENSE' " +
           "AND MONTH(e.date) = :month AND YEAR(e.date) = :year " +
           "GROUP BY FUNCTION('DAY', e.date) ORDER BY day")
    List<Object[]> findDailyExpenseByMonth(@Param("userId") Long userId,
                                           @Param("month") int month,
                                           @Param("year") int year);

    @Query("SELECT e.category, SUM(e.amount) FROM Expense e " +
           "WHERE e.user.id = :userId AND e.type = 'EXPENSE' " +
           "AND e.date BETWEEN :start AND :end " +
           "GROUP BY e.category ORDER BY SUM(e.amount) DESC")
    List<Object[]> findTopCategoriesByDateRange(@Param("userId") Long userId,
                                                @Param("start") LocalDate start,
                                                @Param("end") LocalDate end);

    @Query("SELECT MONTH(e.date), SUM(e.amount) FROM Expense e " +
           "WHERE e.user.id = :userId AND e.type = 'EXPENSE' AND YEAR(e.date) = :year " +
           "GROUP BY MONTH(e.date) ORDER BY MONTH(e.date)")
    List<Object[]> findMonthlyExpenseSummary(@Param("userId") Long userId,
                                             @Param("year") int year);

    @Query("SELECT e FROM Expense e WHERE e.user.id = :userId " +
           "AND (LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(e.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY e.date DESC")
    List<Expense> searchExpenses(@Param("userId") Long userId, @Param("keyword") String keyword);
}
