package com.example.bagrutproject;

public class User {
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    Spendings spendings;
    Incomes incomes;

    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    String password;
    String userName;

    public MBudget getmBudget() {
        return mBudget;
    }

    MBudget mBudget;

    public User(MBudget mBudget, Balance balance, int MSpendSum, int MIncomeSum) {
        this.mBudget = mBudget;
        this.balance = balance;
        this.MSpendSum = MSpendSum;
        this.MIncomeSum = MIncomeSum;
    }

    public Balance getBalance() {
        return balance;
    }

    Balance balance;

    public int getMSpendSum() {
        return MSpendSum;
    }

    int MSpendSum = spendings.getSpendSum();

    public int getMIncomeSum() {
        return MIncomeSum;
    }

    int MIncomeSum = incomes.getIncomeSum();
}
