package com.example.bagrutproject;

import java.util.Calendar;

public class Spendings {
    public int getValue() {
        return value;
    }

    public Spendings(int value) {
        this.value = value;
    }

    public void setValue(int value) {
        this.value = value;
        if(mBudget.hasMBudget())
            mBudget.spend(value);
        MSpendSum +=value;
    }
    public void resetSum() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        if (day == 1 && hour == 0 && minute == 0 && second == 0) {
            this.MSpendSum = 0;
        }
    }

    int value;
    MBudget mBudget;

    public int getSpendSum() {
        return MSpendSum;
    }

    int MSpendSum = 0;
}
