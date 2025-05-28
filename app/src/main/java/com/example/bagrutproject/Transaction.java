package com.example.bagrutproject;

public class Transaction {
    private double amount;
    private String date;
    private boolean isIncome;

    public Transaction() {
        // Required empty constructor for Firebase
    }

    public Transaction(double amount, String date, boolean isIncome) {
        this.amount = amount;
        this.date = date;
        this.isIncome = isIncome;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isIncome() {
        return isIncome;
    }

    public void setIncome(boolean income) {
        isIncome = income;
    }
} 