package com.example.smartsalestracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ReceiptAdapter extends RecyclerView.Adapter<ReceiptAdapter.ViewHolder> {
//     // Define the data
    private final List<Product> receiptItemList;

    public ReceiptAdapter(List<Product> receiptItemList, CheckoutPage checkoutPage) {
        this.receiptItemList = receiptItemList;
    }

    @NonNull
    @Override
    // Create new views (invoked by the layout manager)
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.receipt_card, parent, false);
        return new ViewHolder(view);
    }
    @Override
    // Replace the contents of a view (invoked by the layout manager)
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product item = receiptItemList.get(position);

        holder.nameTextView.setText(item.getName());
        holder.quantityTextView.setText(String.valueOf(item.getItemCount()));
        holder.priceTextView.setText(String.valueOf(item.getItemTotal()));
        holder.priceTag.setText(String.valueOf(item.getPrice()));
    }

    @Override
    // Return the size of the list
    public int getItemCount() {
        return receiptItemList.size();
    }

    // Define the ViewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Define the views
        TextView nameTextView;
        TextView quantityTextView;
        TextView priceTextView;
        TextView priceTag;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize the views
            nameTextView = itemView.findViewById(R.id.name);
            quantityTextView = itemView.findViewById(R.id.count_text_view);
            priceTextView = itemView.findViewById(R.id.item_total);
            priceTag = itemView.findViewById(R.id.price);
        }
    }
}
