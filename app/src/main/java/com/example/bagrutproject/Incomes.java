package com.example.bagrutproject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Incomes {
    private double value;
    private String date;

    // No-argument constructor required by Firebase
    public Incomes() {
        // Required empty constructor for Firebase
    }

    // Constructor with value
    public Incomes(double value) {
        this.value = value;
        this.date = getCurrentDate();
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
}