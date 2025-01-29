package com.example.bagrutproject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Incomes {
    public Incomes(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
        this.MIncomeSum+= value;
    }
    public void resetSum() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        if (day == 1 && hour == 0 && minute == 0 && second == 0) {
            this.MIncomeSum = 0;
        }
    }

    int value;

    public int getIncomeSum() {
        return MIncomeSum;
    }

    int MIncomeSum = 0;
}
