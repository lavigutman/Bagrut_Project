package com.example.bagrutproject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Incomes {
    private double value;
    private static double MIncomeSum = 0;
    private String date; // New date field

    // Constructor
    public Incomes(double value) {
        this.value = value;
        MIncomeSum += value;
        this.date = getCurrentDate(); // Set the date when the object is created
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

    public void resetIncomeSumIfNeeded() {
        Calendar calendar = Calendar.getInstance();
        if (calendar.get(Calendar.DAY_OF_MONTH) == 1) {
            MIncomeSum = 0;
        }
    }

    public double getIncomeSum() {
        return MIncomeSum;
    }
}