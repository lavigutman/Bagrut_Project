package com.example.bagrutproject;

import com.google.firebase.database.IgnoreExtraProperties;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Represents a spending transaction in the application.
 * This class tracks individual spending transactions and maintains a monthly spending total.
 * Includes functionality for date tracking and budget integration.
 */
@IgnoreExtraProperties
public class Spendings {
    private double value;
    private static double MSpendSum = 0;
    private String date;
    private MBudget mBudget;

    /**
     * Default constructor required for Firebase database operations.
     * Initializes a spending with zero value and current date.
     */
    public Spendings() {
        this.value = 0.0;
        this.date = getCurrentDate();
        this.mBudget = null;
    }

    /**
     * Creates a new spending transaction with the specified value.
     * Updates the monthly spending total and integrates with budget if available.
     * @param value The amount of the spending transaction
     */
    public Spendings(double value) {
        this.value = value;
        MSpendSum += value;
        this.date = getCurrentDate();
        if (mBudget != null && mBudget.hasMBudget()) {
            mBudget.spend(value);
        }
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
     * Gets the value of the spending transaction.
     * @return The spending amount as a double
     */
    public double getValue() {
        return value;
    }

    /**
     * Sets the value of the spending transaction.
     * @param value The new spending amount to set
     */
    public void setValue(double value) {
        this.value = value;
    }

    /**
     * Gets the date of the spending transaction.
     * @return The date as a String in format "dd/MM/yyyy"
     */
    public String getDate() {
        return date;
    }

    /**
     * Sets the date of the spending transaction.
     * @param date The new date to set
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Resets the monthly spending total if it's the first day of the month.
     */
    public void resetSpendSumIfNeeded() {
        Calendar calendar = Calendar.getInstance();
        if (calendar.get(Calendar.DAY_OF_MONTH) == 1) {
            MSpendSum = 0;
        }
    }

    /**
     * Gets the total spending amount for the current month.
     * @return The monthly spending total as a double
     */
    public double getSpendSum() {
        return MSpendSum;
    }
}