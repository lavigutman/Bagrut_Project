package com.example.bagrutproject;

/**
 * Represents a user's balance in the application.
 * This class manages the user's current balance and provides methods to update it
 * based on income and spending transactions.
 */
public class Balance {
    /**
     * Gets the current balance value.
     * @return The current balance as an integer
     */
    public int getValue() {
        return value;
    }

    /**
     * Sets the balance to a specific value.
     * @param value The new balance value to set
     */
    public void setValue(int value) {
        this.value = value;
    }

    /**
     * Resets the balance to zero.
     */
    public void setValue() {
        this.value = 0;
    }

    /**
     * Adds an income amount to the current balance.
     * @param incomes The income transaction to add to the balance
     */
    public void setValue(Incomes incomes) {
        this.value += incomes.getValue();
    }

    /**
     * Subtracts a spending amount from the current balance.
     * @param spendings The spending transaction to subtract from the balance
     */
    public void setValue(Spendings spendings) {
        this.value -= spendings.getValue();
    }

    /**
     * Creates a new Balance instance with an initial value.
     * @param value The initial balance value
     */
    public Balance(int value) {
        this.value = value;
    }

    int value;
}
