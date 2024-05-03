package com.example.smartsalestracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ReceiptAdapter extends RecyclerView.Adapter<ReceiptAdapter.ViewHolder> {

    private final List<Product> receiptItemList;

    public ReceiptAdapter(List<Product> receiptItemList, CheckoutPage checkoutPage) {
        this.receiptItemList = receiptItemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.receipt_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product item = receiptItemList.get(position);

        holder.nameTextView.setText(item.getName());
        holder.quantityTextView.setText(String.valueOf(item.getItemCount()));
        holder.priceTextView.setText(String.valueOf(item.getPrice()));
    }

    @Override
    public int getItemCount() {
        return receiptItemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView quantityTextView;
        TextView priceTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.name);
            quantityTextView = itemView.findViewById(R.id.count_text_view);
            priceTextView = itemView.findViewById(R.id.item_total);
        }
    }
}
