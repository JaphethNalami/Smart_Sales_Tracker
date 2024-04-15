package com.example.smartsalestracker;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class UpdateAdapter extends RecyclerView.Adapter<UpdateAdapter.ProductViewHolder> {

    private Context context;
    private ArrayList<Product> productArrayList;
    private LayoutInflater inflater;

    public UpdateAdapter(Context context, ArrayList<Product> productArrayList) {
        this.context = context;
        this.productArrayList = productArrayList;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.item_management_card, parent, false);
        return new ProductViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productArrayList.get(position);
        holder.productName.setText(product.name);
        holder.productPrice.setText(product.price);
        holder.productQuantity.setText(product.quantity);
        holder.productId.setText(product.itemId);
        holder.productCategory.setText(product.category);
        holder.productBarcode.setText(product.barcode);
        Glide.with(context).load(product.image).placeholder(R.drawable.blue_circle).into(holder.productImage);

        // Set click listener
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open update page and pass product info
                Intent intent = new Intent(context, Update_page.class);
                intent.putExtra("productName", product.name);
                intent.putExtra("productPrice", product.price);
                intent.putExtra("productQuantity", product.quantity);
                intent.putExtra("productId", product.itemId);
                intent.putExtra("productCategory", product.category);
                intent.putExtra("productBarcode", product.barcode);
                intent.putExtra("productImage", product.image);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productArrayList.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {

        ImageView productImage;
        TextView productName, productPrice, productQuantity, productId, productCategory, productBarcode;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.user_card_image);
            productName = itemView.findViewById(R.id.name);
            productPrice = itemView.findViewById(R.id.price);
            productQuantity = itemView.findViewById(R.id.quantity);
            productId = itemView.findViewById(R.id.id);
            productCategory = itemView.findViewById(R.id.category);
            productBarcode = itemView.findViewById(R.id.barcode);
        }
    }
}
