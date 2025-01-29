package com.example.bagrutproject;

public class Balance {
    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
    public void setValue(){
        this.value = 0;
    }
    public void setValue(Incomes incomes){
        this.value+= incomes.getValue();
    }
    public void setValue(Spendings spendings){
        this.value-= spendings.getValue();
    }

    public Balance(int value) {
        this.value = value;
    }

    int value;
}
