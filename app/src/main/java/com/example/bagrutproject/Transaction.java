package com.example.bagrutproject;

import com.google.firebase.database.IgnoreExtraProperties;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Represents a financial transaction in the application.
 * This class serves as a base class for both income and spending transactions,
 * providing common functionality for transaction tracking and date management.
 */
@IgnoreExtraProperties
public class Transaction {
    private double amount;
    private String date;
    private boolean isIncome;

    /**
     * Default constructor required for Firebase database operations.
     * Initializes a transaction with zero amount and current date.
     */
    public Transaction() {
        this.amount = 0.0;
        this.date = getCurrentDate();
        this.isIncome = false;
    }

    /**
     * Creates a new transaction with the specified amount and type.
     * @param amount The amount of the transaction
     * @param isIncome True if this is an income transaction, false if it's a spending
     */
    public Transaction(double amount, boolean isIncome) {
        this.amount = amount;
        this.date = getCurrentDate();
        this.isIncome = isIncome;
    }

    /**
     * Gets the current date in the format "dd/MM/yyyy".
     * @return The current date as a formatted String
     */
    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(new Date());
    }

    /**
     * Gets the amount of the transaction.
     * @return The transaction amount as a double
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Sets the amount of the transaction.
     * @param amount The new amount to set
     */
    public void setAmount(double amount) {
        this.amount = amount;
    }

    /**
     * Gets the date of the transaction.
     * @return The date as a String in format "dd/MM/yyyy"
     */
    public String getDate() {
        return date;
    }

    /**
     * Sets the date of the transaction.
     * @param date The new date to set
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Checks if this is an income transaction.
     * @return True if this is an income transaction, false if it's a spending
     */
    public boolean isIncome() {
        return isIncome;
    }

    /**
     * Sets whether this is an income transaction.
     * @param income True for income, false for spending
     */
    public void setIncome(boolean income) {
        isIncome = income;
    }
} 