package com.example.bagrutproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {
    private List<Object> transactions = new ArrayList<>();

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

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

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public void setTransactions(List<Object> transactions) {
        this.transactions = transactions;
        notifyDataSetChanged();
    }

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