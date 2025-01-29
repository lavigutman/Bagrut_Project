package com.example.bagrutproject;

import java.util.Calendar;

public class MBudget {
    public MBudget(int budget) {
        this.budget = budget;
    }

    public int getBudget() {
        return budget;
    }

    public void setBudget(int budget) {
        this.budget = budget;
        this.hasBudget = true;
    }

    public void setBudget() {
        this.hasBudget = false;
    }

    public boolean hasMBudget() {
        return hasBudget;
    }

    public void spend(int value) {
        this.budget -= value;
    }

    boolean hasBudget;
    int budget;

    public int getResetDay() {
        return resetDay;
    }

    public void setResetDay(int resetDay) {
        this.resetDay = resetDay;
    }

    int resetDay;

    public void setEndDate(int year, int month, int day) {
        this.endDate =  day + "/" + (month + 1) + "/" + year;
    }
    public void resetMbudget(){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String date = day + "/" + (month + 1) + "/" + year;
        if(date.equals(endDate)) {
            hasBudget = false;
            budget = 0;
        }
    }

    String endDate;
}
