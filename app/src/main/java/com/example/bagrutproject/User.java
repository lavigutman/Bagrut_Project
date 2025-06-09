package com.example.bagrutproject;

/**
 * Represents a user in the application.
 * This class stores user information including personal details and financial data.
 * Used for Firebase database operations and user management.
 */
public class User {
    public String name, surname, userName, password;
    public double balance;

    /**
     * Default constructor required for Firebase database operations.
     */
    public User() {
    }

    /**
     * Gets the user's current balance.
     * @return The user's balance as a double value
     */
    public double getBalance() {
        return balance;
    }

    /**
     * Sets the user's balance.
     * @param balance The new balance value to set
     */
    public void setBalance(double balance) {
        this.balance = balance;
    }

    /**
     * Gets the user's password.
     * @return The user's password as a String
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the user's password.
     * @param password The new password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the user's username.
     * @return The user's username as a String
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets the user's username.
     * @param userName The new username to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Gets the user's surname.
     * @return The user's surname as a String
     */
    public String getSurname() {
        return surname;
    }

    /**
     * Sets the user's surname.
     * @param surname The new surname to set
     */
    public void setSurname(String surname) {
        this.surname = surname;
    }

    /**
     * Gets the user's first name.
     * @return The user's first name as a String
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the user's first name.
     * @param name The new first name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Creates a new User instance with all required information.
     * @param userName The username for the new user
     * @param password The password for the new user
     * @param name The first name of the user
     * @param surname The surname of the user
     * @param balance The initial balance for the user
     */
    public User(String userName, String password, String name, String surname, double balance) {
        this.name = name;
        this.surname = surname;
        this.userName = userName;
        this.password = password;
        this.balance = balance;
    }
}

