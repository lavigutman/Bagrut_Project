package com.example.bagrutproject;

import java.util.Calendar;

public class MBudget {
    private boolean hasBudget;
    private double budget;
    private int resetDay;
    private double updateBudget;
    private String endDate;

    // Required empty constructor for Firebase
    public MBudget() {
    }

    public MBudget(double budget, int resetDay, String endDate) {
        this.budget = budget;
        this.resetDay = resetDay;
        this.endDate = endDate;
        this.updateBudget = budget;
        this.hasBudget = true;
    }

    public double getBudget() {
        return this.budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
        this.hasBudget = true;
    }

    public void setBudget() {
        this.hasBudget = false;
    }

    public boolean hasMBudget() {
        return hasBudget;
    }

    public int getResetDay() {
        return resetDay;
    }

    public void setResetDay(int resetDay) {
        this.resetDay = resetDay;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void spend(double value) {
        this.updateBudget -= value;
    }
    public void setHasBudget(boolean hasBudget) {
        this.hasBudget = hasBudget;
    }

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