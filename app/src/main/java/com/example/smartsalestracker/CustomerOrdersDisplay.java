package com.example.smartsalestracker;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class CustomerOrdersDisplay extends AppCompatActivity {

    // UI components
    TextView customerName1, customerPhone1;
    ListView listView;
    FloatingActionButton fab;

    // Firebase initializations
    private FirebaseFirestore db;
    private FirebaseUser user;
    private String userId;
    private FirebaseAuth mAuth;
    String customerPhone, customerName;
    Dialog dialog;

    // ListView adapter
    private ArrayAdapter<String> adapter;
    private ArrayList<String> orderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_customer_orders_display);

        // Apply window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get name and phone number from intent
        customerName = getIntent().getStringExtra("customerName");
        customerPhone = getIntent().getStringExtra("customerPhone");

        // Initialize views
        customerName1 = findViewById(R.id.customerName);
        customerPhone1 = findViewById(R.id.customerPhoneNumber);
        listView = findViewById(R.id.listView);
        fab = findViewById(R.id.fab);

        // Set up dialog
        dialog = new MaterialAlertDialogBuilder(this)
                .setView(new ProgressBar(this))
                .setTitle("Processing Payment")
                .setMessage("Please wait")
                .create();

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userId = Objects.requireNonNull(user).getUid();

        // Set name and phone number in the views
        customerName1.setText(customerName);
        customerPhone1.setText(customerPhone);

        // Initialize order list and adapter
        orderList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, orderList);
        listView.setAdapter(adapter);

        // Set click listener on fab to refresh orders
        fab.setOnClickListener(v -> {
            // Clear orders and call getOrders method
            listView.setAdapter(null);
            getOrders();
        });

        // Call method to get orders initially
        getOrders();
    }

    private void getOrders() {
        // Get orders from Firestore using customer phone number as collection name
        db.collection(userId).document("Shop").collection("Customers_Orders").document(customerPhone).collection("Orders")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Clear the orderList before adding new orders
                        orderList.clear();

                        // Loop through each document in the result
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Get the ID of the document
                            String orderId = document.getId();

                            // Access fields within the document using IDs
                            String field1 = document.getString("itemCount");
                            String field2 = document.getString("paymentMethod");
                            String field3 = document.getString("customerGender");
                            String field4 = document.getString("productName");
                            String field5 = document.getString("orderDate");
                            String field6 = document.getString("productCategory");

                            // Format these fields as per requirements
                            String orderInfo = "Category: " + field6 + "\nItem: " + field4 + "\nQuantity: " + field1 + "\nPayment: " + field2 + "\nGender: " + field3 + "\nDate: " + field5;

                            // Add orderInfo to orderList
                            orderList.add(orderInfo);
                        }
                        // Notify adapter that data set has changed
                        adapter.notifyDataSetChanged();
                        dialog.dismiss();
                    } else {
                        // Show error message
                        dialog.dismiss();
                        new MaterialAlertDialogBuilder(this)
                                .setTitle("Error")
                                .setMessage("An error occurred while fetching orders")
                                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                                .show();
                    }
                });
    }
}