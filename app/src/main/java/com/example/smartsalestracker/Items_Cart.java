package com.example.smartsalestracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Items_Cart extends AppCompatActivity {

    // Define the views
    ImageButton back;
   static TextView clear_cart, total_price;
    Button checkout;
    RecyclerView recyclerView;
    CartAdapter cartAdapter;
    ArrayList<Product> cartItems;

    @Override
    // Create the activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_items_cart);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize the views
        back = findViewById(R.id.back_button);
        clear_cart = findViewById(R.id.clear_cart);
        total_price = findViewById(R.id.total_amount);
        checkout = findViewById(R.id.checkout_button);
        recyclerView = findViewById(R.id.cart_recycler_view);
        cartItems = new ArrayList<>();

        // Set click listeners for the buttons
        back.setOnClickListener(v -> {
            //go back to the home page activity
            startActivity(new Intent(Items_Cart.this, Home_Page.class));
            //onBackPressed();
        });

        // Clear the cart
        clear_cart.setOnClickListener(v -> {
            // Clear the cart
            Product_Cart.getInstance().getSelectedProducts().clear();
            cartAdapter.notifyDataSetChanged();
        });

        // Move to the checkout page
        checkout.setOnClickListener(v -> {
            // move to the checkout page
            startActivity(new Intent(Items_Cart.this, CheckoutPage.class));

        });

        // Fetch item details from Product_Cart class
        Map<Product, Integer> selectedProductsMap = Product_Cart.getInstance().getSelectedProducts();
        for (Map.Entry<Product, Integer> entry : selectedProductsMap.entrySet()) {
            Product product = entry.getKey();
            int count = entry.getValue();
            for (int i = 0; i < count; i++) {
                cartItems.add(product);
            }
        }

        // Initialize CartAdapter with fetched item details
        cartAdapter = new CartAdapter(cartItems, this);

        // Set layout manager and adapter to the RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(cartAdapter);

        // Calculate total price of all items in the cart
        double totalPrice = Product_Cart.getInstance().calculateTotalPrice();
        //convert the total price to string
        String totalPrice1 = String.valueOf(totalPrice);
        //set the total price to the text view
        total_price.setText(totalPrice1);

        //set click listener for the total price text view to reload the value
        total_price.setOnClickListener(v -> {
            // Calculate total price of all items in the cart
            double totalPrice2 = Product_Cart.getInstance().calculateTotalPrice();
            //convert the total price to string
            String totalPrice3 = String.valueOf(totalPrice2);
            //set the total price to the text view
            total_price.setText(String.format("Ksh%s", totalPrice3));
        });


    }
}
