package com.example.bagrutproject;

import com.google.firebase.database.IgnoreExtraProperties;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@IgnoreExtraProperties
public class Spendings {
    private double value;
    private static double MSpendSum = 0;
    private String date;
    private MBudget mBudget;

    // Required empty constructor for Firebase
    public Spendings() {
        this.value = 0.0;
        this.date = getCurrentDate();
        this.mBudget = null;
    }

    // Constructor with value
    public Spendings(double value) {
        this.value = value;
        MSpendSum += value;
        this.date = getCurrentDate();
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

    public void setValue(double value) {
        this.value = value;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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