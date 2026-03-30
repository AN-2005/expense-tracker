package com.example.demo.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Expense;
import com.example.demo.repository.ExpenseRepository;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository repo;

    public void saveExpense(Expense expense) {
        repo.save(expense);
    }

    public List<Expense> getAllExpenses() {
        return repo.findAll();
    }

    public void deleteExpense(Long id) {
        repo.deleteById(id);
    }

    public Expense getExpenseById(Long id) {
        return repo.findById(id).orElse(null);
    }
    
 // Total expense by category
    public Map<String, Double> getExpenseByCategory() {
        List<Expense> all = repo.findAll();
        Map<String, Double> map = new HashMap<>();
        
        all.stream()
           .filter(e -> "EXPENSE".equals(e.getType()))
           .forEach(e -> map.merge(e.getCategory(), e.getAmount(), Double::sum));
        
        return map;
    }

    // Total expense by month
    public Map<String, Double> getExpenseByMonth() {
        List<Expense> all = repo.findAll();
        Map<String, Double> map = new HashMap<>();

        all.stream()
           .filter(e -> "EXPENSE".equals(e.getType()))
           .forEach(e -> {
               String monthKey = e.getDate().getYear() + "-" + e.getDate().getMonthValue();
               map.merge(monthKey, e.getAmount(), Double::sum);
           });

        return map;
    }

    // Totals calculation
    public Map<String, Double> getTotals() {
        List<Expense> all = repo.findAll();

        double income = all.stream()
                .filter(e -> "INCOME".equals(e.getType()))
                .mapToDouble(Expense::getAmount)
                .sum();

        double expense = all.stream()
                .filter(e -> "EXPENSE".equals(e.getType()))
                .mapToDouble(Expense::getAmount)
                .sum();

        Map<String, Double> totals = new HashMap<>();
        totals.put("income", income);
        totals.put("expense", expense);
        totals.put("balance", income - expense);

        return totals;
    }

    // Monthly income calculation
    public double getMonthlyIncome(int year, int month) {
        List<Expense> all = repo.findAll();
        return all.stream()
                .filter(e -> "INCOME".equals(e.getType()))
                .filter(e -> {
                    LocalDate date = e.getDate();
                    return date.getYear() == year && date.getMonthValue() == month;
                })
                .mapToDouble(Expense::getAmount)
                .sum();
    }
}