package com.example.bagrutproject;

import java.util.Calendar;

/**
 * Represents a monthly budget in the application.
 * This class manages budget information including amount, reset day, and end date.
 * Handles budget updates and spending tracking.
 */
public class MBudget {
    private boolean hasBudget;
    private double budget;
    private int resetDay;
    private double updateBudget;
    private String endDate;

    /**
     * Default constructor required for Firebase database operations.
     */
    public MBudget() {
    }

    /**
     * Creates a new monthly budget with specified parameters.
     * @param budget The total budget amount
     * @param resetDay The day of the month when the budget resets
     * @param endDate The date when the budget period ends
     */
    public MBudget(double budget, int resetDay, String endDate) {
        this.budget = budget;
        this.resetDay = resetDay;
        this.endDate = endDate;
        this.updateBudget = budget;
        this.hasBudget = true;
    }

    /**
     * Gets the current budget amount.
     * @return The budget amount as a double value
     */
    public double getBudget() {
        return this.budget;
    }

    /**
     * Sets a new budget amount and marks the budget as active.
     * @param budget The new budget amount to set
     */
    public void setBudget(double budget) {
        this.budget = budget;
        this.hasBudget = true;
    }

    /**
     * Disables the budget by setting hasBudget to false.
     */
    public void setBudget() {
        this.hasBudget = false;
    }

    /**
     * Checks if there is an active budget.
     * @return true if there is an active budget, false otherwise
     */
    public boolean hasMBudget() {
        return hasBudget;
    }

    /**
     * Gets the reset day of the month.
     * @return The day of the month when the budget resets
     */
    public int getResetDay() {
        return resetDay;
    }

    /**
     * Sets the reset day of the month.
     * @param resetDay The new reset day to set
     */
    public void setResetDay(int resetDay) {
        this.resetDay = resetDay;
    }

    /**
     * Gets the end date of the budget period.
     * @return The end date as a String in format "dd/MM/yyyy"
     */
    public String getEndDate() {
        return endDate;
    }

    /**
     * Sets the end date of the budget period.
     * @param endDate The new end date to set
     */
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    /**
     * Deducts an amount from the current budget.
     * @param value The amount to deduct from the budget
     */
    public void spend(double value) {
        this.updateBudget -= value;
    }

    /**
     * Sets whether there is an active budget.
     * @param hasBudget true to enable the budget, false to disable it
     */
    public void setHasBudget(boolean hasBudget) {
        this.hasBudget = hasBudget;
    }

    /**
     * Ends the budget if the current date matches the end date.
     * Sets the budget to 0 and marks it as inactive.
     */
    public void endMbudget() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String date = day + "/" + (month + 1) + "/" + year;
        if (date.equals(endDate)) {
            hasBudget = false;
            budget = 0;
        }
    }

    public void resetBudgetIfNeeded() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        if (day == resetDay) {
            budget = 0;
        }
    }
}