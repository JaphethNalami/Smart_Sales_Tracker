package com.example.smartsalestracker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CardViewHolder> {

    // List to hold the cart items
    private final List<Product> cardItemList;
    // Context for accessing resources
    private final Context context;

    // Constructor to initialize the cardItemList and context
    public CartAdapter(List<Product> cardItemList, Context context) {
        this.cardItemList = cardItemList;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the cart card layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_card, parent, false);
        return new CardViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, @SuppressLint("RecyclerView") int position) {
        // Get the current product item
        Product cardItem = cardItemList.get(position);
        // Set product name
        holder.nameTextView.setText(cardItem.getName());
        // Set product count
        holder.countTextView.setText(String.valueOf(cardItem.getItemCount()));
        // Set product total price
        String itemTotal = String.valueOf(cardItem.getItemTotal());
        holder.itemTotalTextView.setText(String.format("Ksh %s", itemTotal));

        // Set onClickListeners for buttons if needed
        holder.reduceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get current value of item count and price
                String count1 = cardItem.getItemCount();
                String item_price = cardItem.getPrice();
                // Convert to integers
                int count = Integer.parseInt(count1);
                int price = Integer.parseInt(item_price);
                // Check if count is greater than 1 to reduce
                if (count > 1) {
                    count--;
                    // Set the new item count
                    cardItem.setItemCount(String.valueOf(count));
                    holder.countTextView.setText(String.valueOf(count));
                    // Update the total price
                    cardItem.setItemTotal(String.valueOf(count * price));
                    String itemTotal = String.valueOf(count * price);
                    holder.itemTotalTextView.setText(String.format("Ksh %s", itemTotal));
                    // Update the cart with the new count
                    Product_Cart.getInstance().addToCart(cardItem, count);
                    // Show toast message for the reduced item
                    Toast.makeText(v.getContext(), cardItem.getName() + " reduced to " + count, Toast.LENGTH_SHORT).show();
                    // Calculate remaining quantity of product
                    Product_Cart.getInstance().calculateRemainingQuantity(cardItem, count);
                    // Calculate sold quantity of product
                    Product_Cart.getInstance().calculateSoldQuantity(cardItem, count);
                    // Update total price
                    double totalPrice = Product_Cart.getInstance().calculateTotalPrice();
                    String totalPrice1 = String.valueOf(totalPrice);
                    Items_Cart.total_price.setText(String.format("Ksh %s", totalPrice1));
                } else {
                    // Show toast message if count is less than 1
                    Toast.makeText(v.getContext(), "Item count cannot be less than 1", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.incrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get current value of item count and price
                String count1 = cardItem.getItemCount();
                String item_price = cardItem.getPrice();
                // Get the quantity of the item
                String quantity = cardItem.getQuantity();
                // Convert to integers
                int quantity1 = Integer.parseInt(quantity);
                int count = Integer.parseInt(count1);
                int price = Integer.parseInt(item_price);
                // Check if count is less than quantity to increment
                if (count < quantity1) {
                    count++;
                    // Set the new item count
                    cardItem.setItemCount(String.valueOf(count));
                    holder.countTextView.setText(String.valueOf(count));
                    // Update the total price
                    cardItem.setItemTotal(String.valueOf(count * price));
                    String itemTotal = String.valueOf(count * price);
                    holder.itemTotalTextView.setText(String.format("Ksh %s", itemTotal));
                    // Update the cart with the new count
                    Product_Cart.getInstance().addToCart(cardItem, count);
                    // Show toast message for the incremented item
                    Toast.makeText(v.getContext(), cardItem.getName() + " incremented to " + count, Toast.LENGTH_SHORT).show();
                    // Calculate remaining quantity of product
                    Product_Cart.getInstance().calculateRemainingQuantity(cardItem, count);
                    // Calculate sold quantity of product
                    Product_Cart.getInstance().calculateSoldQuantity(cardItem, count);
                    // Update total price
                    double totalPrice = Product_Cart.getInstance().calculateTotalPrice();
                    String totalPrice1 = String.valueOf(totalPrice);
                    Items_Cart.total_price.setText(String.format("Ksh %s", totalPrice1));
                } else {
                    // Show toast message if no more quantity is available
                    Toast.makeText(v.getContext(), "No more " + cardItem.getName() + " available", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove the item from the cart
                cardItemList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, cardItemList.size());
                Product_Cart.getInstance().removeFromCart(cardItem);
                // Show toast message for the removed item
                Toast.makeText(v.getContext(), cardItem.getName()+" removed from cart", Toast.LENGTH_SHORT).show();

                //set total price
                double totalPrice = Product_Cart.getInstance().calculateTotalPrice();
                String totalPrice1 = String.valueOf(totalPrice);
                Items_Cart.total_price.setText(String.format("Ksh%s", totalPrice1));
            }
        });

        //set click for clear cat in the items cart page
        Items_Cart.clear_cart.setOnClickListener(v -> {
            // Clear the cart
            Product_Cart.getInstance().getSelectedProducts().clear();
            notifyDataSetChanged();
            //move to the home page
            context.startActivity(new Intent(context, Home_Page.class));

        });

        //calculate remaining quantity of product
        int count = Integer.parseInt(cardItem.getItemCount());
        Product_Cart.getInstance().calculateRemainingQuantity(cardItem, count);

        //calculate sold quantity of product
        Product_Cart.getInstance().calculateSoldQuantity(cardItem, count);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return cardItemList.size();
    }

    // Provide a reference to the views for each data item
    public static class CardViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView countTextView;
        public ImageButton reduceButton;
        public ImageButton incrementButton;
        public ImageButton deleteButton;
        public TextView itemTotalTextView;


        public CardViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.name);
            countTextView = itemView.findViewById(R.id.count_text_view);
            reduceButton = itemView.findViewById(R.id.reduce_button);
            incrementButton = itemView.findViewById(R.id.increment_button);
            deleteButton = itemView.findViewById(R.id.delete_button);
            itemTotalTextView = itemView.findViewById(R.id.item_total);
        }
    }
}

