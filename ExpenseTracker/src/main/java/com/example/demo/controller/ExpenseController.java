package com.example.demo.controller;

import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.demo.model.Expense;
import com.example.demo.service.ExpenseService;

@Controller
public class ExpenseController {

    @Autowired
    private ExpenseService service;

    // Store income per month (key = "YYYY-MM")
    private Map<String, Double> monthlyIncomeMap = new HashMap<>();

    // Home / Dashboard
    @GetMapping("/")
    public String home(Model model) {

        List<Expense> expenses = service.getAllExpenses();

        // Current month key
        String currentMonth = YearMonth.now().toString();

        // Get monthly income (default 0 if not set)
        double monthlyIncome = monthlyIncomeMap.getOrDefault(currentMonth, 0.0);

        // Total expenses
        double expenseTotal = service.getTotals().getOrDefault("expense", 0.0);

        // Balance
        double balance = monthlyIncome - expenseTotal;

        // Send data to view
        model.addAttribute("monthlyIncome", monthlyIncome);
        model.addAttribute("expenseTotal", expenseTotal);
        model.addAttribute("balance", balance);
        model.addAttribute("expenses", expenses);

        // Chart data
        model.addAttribute("categoryTotals", service.getExpenseByCategory());
        model.addAttribute("monthlyTotals", service.getExpenseByMonth());

        return "index";
    }

    // Show form to set monthly income
    @GetMapping("/monthly-income")
    public String monthlyIncomeForm(Model model) {
        return "monthly-income";
    }

    // Save monthly income (ONLY ONCE per month)
    @PostMapping("/monthly-income")
    public String saveMonthlyIncome(@RequestParam("monthlyIncome") double income) {

        String currentMonth = YearMonth.now().toString();

        // Set only if not already set
        monthlyIncomeMap.putIfAbsent(currentMonth, income);

        return "redirect:/";
    }

    // Add Expense Form
    @GetMapping("/add")
    public String addForm(Model model) {
        Expense expense = new Expense();
        expense.setType("EXPENSE"); // always expense now
        model.addAttribute("expense", expense);
        return "add-expense";
    }

    // Save Expense
    @PostMapping("/save")
    public String saveExpense(@ModelAttribute Expense expense) {
        expense.setType("EXPENSE"); // enforce expense only
        service.saveExpense(expense);
        return "redirect:/";
    }

    // Delete Expense
    @GetMapping("/delete/{id}")
    public String deleteExpense(@PathVariable Long id) {
        service.deleteExpense(id);
        return "redirect:/";
    }

    // Edit Expense
    @GetMapping("/edit/{id}")
    public String editExpense(@PathVariable Long id, Model model) {
        Expense expense = service.getExpenseById(id);
        model.addAttribute("expense", expense);
        return "add-expense";
    }
}