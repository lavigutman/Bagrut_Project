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
}

