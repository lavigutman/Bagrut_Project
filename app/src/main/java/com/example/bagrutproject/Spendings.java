package com.example.bagrutproject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Spendings {
    private double value;
    private static double MSpendSum = 0;
    private String date; // New date field
    private MBudget mBudget;

    public Spendings(double value) {
        this.value = value;
        MSpendSum += value;
        this.date = getCurrentDate(); // Set the current date
        if (mBudget != null && mBudget.hasMBudget()) {
            mBudget.spend(value);
        }
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(new Date());
    }

    public double getValue() {
        return value;
    }

    public String getDate() {
        return date;
    }

    public void resetSpendSumIfNeeded() {
        Calendar calendar = Calendar.getInstance();
        if (calendar.get(Calendar.DAY_OF_MONTH) == 1) {
            MSpendSum = 0;
        }
    }

    public double getSpendSum() {
        return MSpendSum;
    }
}