package com.example.bagrutproject;

public class User {
    public String name, surname, userName, password;
    public double balance;

    public User() {
    }
    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

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

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User(String userName, String password, String name, String surname, double balance) {
        this.name = name;
        this.surname = surname;
        this.userName = userName;
        this.password = password;
        this.balance = balance;
    }
}

