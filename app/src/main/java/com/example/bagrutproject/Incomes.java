package com.example.bagrutproject;

import com.google.firebase.database.IgnoreExtraProperties;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;

/**
 * Represents an income transaction in the application.
 * This class tracks individual income transactions and maintains a monthly income total.
 * Includes functionality for date tracking and balance updates.
 */
@IgnoreExtraProperties
public class Incomes {
    private double value;
    private static double MIncomeSum = 0;
    private String date;

    /**
     * Default constructor required for Firebase database operations.
     * Initializes an income with zero value and current date.
     */
    public Incomes() {
        this.value = 0.0;
        this.date = getCurrentDate();
    }

    /**
     * Creates a new income transaction with the specified value.
     * Updates the monthly income total.
     * @param value The amount of the income transaction
     */
    public Incomes(double value) {
        this.value = value;
        MIncomeSum += value;
        this.date = getCurrentDate();
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
     * Gets the value of the income transaction.
     * @return The income amount as a double
     */
    public double getValue() {
        return value;
    }

    /**
     * Sets the value of the income transaction.
     * @param value The new income amount to set
     */
    public void setValue(double value) {
        this.value = value;
    }

    /**
     * Gets the date of the income transaction.
     * @return The date as a String in format "dd/MM/yyyy"
     */
    public String getDate() {
        return date;
    }

    /**
     * Sets the date of the income transaction.
     * @param date The new date to set
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Resets the monthly income total if it's the first day of the month.
     */
    public void resetIncomeSumIfNeeded() {
        Calendar calendar = Calendar.getInstance();
        if (calendar.get(Calendar.DAY_OF_MONTH) == 1) {
            MIncomeSum = 0;
        }
    }

    /**
     * Gets the total income amount for the current month.
     * @return The monthly income total as a double
     */
    public double getIncomeSum() {
        return MIncomeSum;
    }
}