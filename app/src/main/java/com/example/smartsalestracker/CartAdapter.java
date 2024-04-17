package com.example.smartsalestracker;

import android.content.Context;
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

    private final List<Product> cardItemList;
    private Context context;

    // Constructor to initialize the cardItemList
    public CartAdapter(List<Product> cardItemList, Context context) {
        this.cardItemList = cardItemList;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_card, parent, false);
        return new CardViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        Product cardItem = cardItemList.get(position);
        holder.nameTextView.setText(cardItem.getName());
        holder.countTextView.setText(String.valueOf(cardItem.getItemCount()));
        holder.itemTotalTextView.setText(String.valueOf(cardItem.getItemTotal()));
       // Glide.with(holder.itemView.getContext()).load(cardItem.image).placeholder(R.drawable.blue_circle).into(holder.productImage);

        // Set onClickListeners for buttons if needed
        holder.reduceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get current value of item count
                String count1 = cardItem.getItemCount();
                String item_price = cardItem.getPrice();
                // decrement the value
                int count = Integer.parseInt(count1);
                int price = Integer.parseInt(item_price);
                if (count > 1) {
                    count--;
                    // set the new value
                    cardItem.setItemCount(String.valueOf(count));
                    holder.countTextView.setText(String.valueOf(count));
                    // update the total
                    cardItem.setItemTotal(String.valueOf(count*price));
                    holder.itemTotalTextView.setText(String.valueOf(count*price));
                    // update the cart
                    Product_Cart.getInstance().addToCart(cardItem, count);
                    //toast name of item removed
                    Toast.makeText(v.getContext(), cardItem.getName()+" reduced to" + count, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(v.getContext(), "Item count cannot be less than 1", Toast.LENGTH_SHORT).show();
            }
        }
        });

        holder.incrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get current value of item count
                String count1 = cardItem.getItemCount();
                String item_price = cardItem.getPrice();
                //get quantity of item
                String quantity = cardItem.getQuantity();

                // increment the value
                int quantity1 = Integer.parseInt(quantity);
                int count = Integer.parseInt(count1);
                int price = Integer.parseInt(item_price);
                if (count < quantity1){
                count++;
                // set the new value
                cardItem.setItemCount(String.valueOf(count));
                holder.countTextView.setText(String.valueOf(count));
                // update the total
                cardItem.setItemTotal(String.valueOf(count*price));
                holder.itemTotalTextView.setText(String.valueOf(count*price));
                // update the cart
                Product_Cart.getInstance().addToCart(cardItem, count);
                //toast name of item added
                Toast.makeText(v.getContext(), cardItem.getName()+" incremented to" + count, Toast.LENGTH_SHORT).show();
            }
                else {
                    Toast.makeText(v.getContext(), "No more"+cardItem.getName()+"available", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //delete the item from the cart
                cardItemList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, cardItemList.size());
                Product deletedProduct = cardItem;
                Product_Cart.getInstance().removeFromCart(deletedProduct);
                Toast.makeText(v.getContext(), cardItem.getName()+" removed from cart", Toast.LENGTH_SHORT).show();
            }
        });

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
        public ImageView productImage;

        public CardViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.name);
            countTextView = itemView.findViewById(R.id.count_text_view);
            reduceButton = itemView.findViewById(R.id.reduce_button);
            incrementButton = itemView.findViewById(R.id.increment_button);
            deleteButton = itemView.findViewById(R.id.delete_button);
            itemTotalTextView = itemView.findViewById(R.id.item_total);
            productImage = itemView.findViewById(R.id.user_card_image);
        }
    }
}

