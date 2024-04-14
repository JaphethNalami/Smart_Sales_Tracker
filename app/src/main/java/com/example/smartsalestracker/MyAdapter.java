package com.example.smartsalestracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ProductViewHolder>{

    private ArrayList<Product> productArrayList;
    private LayoutInflater inflater;

    // HashMap to store item counts temporarily
    private HashMap<Product, Integer> countMap = new HashMap<>();

    public MyAdapter(Context context, ArrayList<Product> productArrayList) {
        this.productArrayList = productArrayList;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.item_card, parent, false);
        return new ProductViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productArrayList.get(position);
        holder.productName.setText(product.name);
        holder.productPrice.setText(product.price);
        Glide.with(holder.itemView.getContext()).load(product.image).placeholder(R.drawable.blue_circle).into(holder.productImage);

        // Display the current count, or hide if count is zero
        int count = countMap.getOrDefault(product, 0);
        holder.countTextView.setText(String.valueOf(count));
        holder.countTextView.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return productArrayList.size();
    }

    public void filterList(ArrayList<Product> filteredList) {
        // Maintain count for already selected products
        HashMap<Product, Integer> newCountMap = new HashMap<>();

        for (Product p : filteredList) {
            int count = countMap.getOrDefault(p, 0);
            if (count > 0) {
                newCountMap.put(p, count);
            }
        }

        productArrayList = filteredList;
        countMap = newCountMap; // Update countMap with filtered items
        notifyDataSetChanged();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView productImage;
        TextView productName, productPrice, countTextView;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.user_card_image);
            productName = itemView.findViewById(R.id.name);
            productPrice = itemView.findViewById(R.id.price);
            countTextView = itemView.findViewById(R.id.count_text_view);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Product product = productArrayList.get(getAdapterPosition());
            int count = countMap.getOrDefault(product, 0) + 1;

            if (count == 0) {
                countTextView.setVisibility(View.GONE);
            } else {
                countTextView.setVisibility(View.VISIBLE);
                countMap.put(product, count);
                countTextView.setText(String.valueOf(count));
            }

            // Add the selected product to the cart
            Product_Cart.getInstance().addToCart(product, count);

            // Calculate total
            double total = 0;
            for (Map.Entry<Product, Integer> entry : Product_Cart.getInstance().getSelectedProducts().entrySet()) {
                Product p = entry.getKey();
                int selectedCount = entry.getValue();
                total += selectedCount * Double.parseDouble(p.price);
            }

            // Display total in a toast
            Toast.makeText(v.getContext(), "Total: " + total, Toast.LENGTH_SHORT).show();
        }
    }

}
