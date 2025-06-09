package com.example.bagrutproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter class for displaying financial transactions in a RecyclerView.
 * This adapter handles both income and spending transactions, displaying them
 * with appropriate formatting and colors (green for income, red for expenses).
 */
public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {
    private List<Object> transactions = new ArrayList<>();

    /**
     * Creates a new ViewHolder for transaction items.
     * @param parent The ViewGroup into which the new View will be added
     * @param viewType The view type of the new View
     * @return A new TransactionViewHolder that holds a View of the given view type
     */
    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    /**
     * Binds transaction data to the ViewHolder.
     * Formats the amount with appropriate sign (+ for income, - for expenses)
     * and sets the text color based on transaction type.
     * @param holder The ViewHolder to bind data to
     * @param position The position of the item in the data set
     */
    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Object item = transactions.get(position);
        String amountText;
        String date;
        boolean isIncome;

        if (item instanceof Incomes) {
            Incomes income = (Incomes) item;
            amountText = "+₪" + String.format("%.2f", income.getValue());
            date = income.getDate();
            isIncome = true;
        } else {
            Spendings spending = (Spendings) item;
            amountText = "-₪" + String.format("%.2f", spending.getValue());
            date = spending.getDate();
            isIncome = false;
        }
        
        holder.textAmount.setText(amountText);
        holder.textAmount.setTextColor(holder.itemView.getContext().getResources().getColor(
            isIncome ? android.R.color.holo_green_dark : android.R.color.holo_red_dark));
        holder.textDate.setText(date);
    }

    /**
     * Returns the total number of transactions in the data set.
     * @return The total number of transactions
     */
    @Override
    public int getItemCount() {
        return transactions.size();
    }

    /**
     * Updates the list of transactions and refreshes the RecyclerView.
     * @param transactions The new list of transactions to display
     */
    public void setTransactions(List<Object> transactions) {
        this.transactions = transactions;
        notifyDataSetChanged();
    }

    /**
     * ViewHolder class for transaction items.
     * Holds references to the views that display transaction amount and date.
     */
    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView textAmount;
        TextView textDate;

        TransactionViewHolder(View itemView) {
            super(itemView);
            textAmount = itemView.findViewById(R.id.text_amount);
            textDate = itemView.findViewById(R.id.text_date);
        }
    }
} 