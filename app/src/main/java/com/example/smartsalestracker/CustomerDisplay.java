package com.example.smartsalestracker;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class CustomerDisplay extends AppCompatActivity {

    RecyclerView recyclerView;
    FloatingActionButton fab;
    ImageButton back;

    private FirebaseFirestore db;
    private FirebaseUser user;
    private String userId;
    private FirebaseAuth mAuth;
    Dialog dialog;
    ArrayList<Customer> customerArrayList;
    CustomerDetailsAdapter customerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_customer_display);

        // Set window insets to avoid system bars overlay
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize the views
        recyclerView = findViewById(R.id.recyclerView);
        fab = findViewById(R.id.refresh);
        back = findViewById(R.id.back);

        // Initialize Firebase instances
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userId = Objects.requireNonNull(user).getUid();

        // Initialize and show a dialog to indicate data retrieval is in progress
        dialog = new MaterialAlertDialogBuilder(this)
                .setView(new ProgressBar(this))
                .setTitle("Fetching Data...")
                .setMessage("Please wait")
                .create();
        dialog.show();

        // Set a click listener for the FloatingActionButton to refresh data
        fab.setOnClickListener(v -> {
            customerArrayList.clear();
            getCustomerDetails();
        });

        // Set a click listener for the back button to finish the activity
        back.setOnClickListener(v -> {
            finish();
        });

        // Setup the RecyclerView with a linear layout manager and adapter
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the ArrayList and adapter
        customerArrayList = new ArrayList<>();
        customerAdapter = new CustomerDetailsAdapter(CustomerDisplay.this, customerArrayList);
        recyclerView.setAdapter(customerAdapter);

        // Call method to get customer details from the database
        getCustomerDetails();
    }

    private void getCustomerDetails() {
        // Show the dialog indicating data retrieval is in progress
        dialog.show();

        db.collection(userId).document("Shop").collection("Customers_Details").addSnapshotListener((new EventListener<QuerySnapshot>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    return;
                }
                customerArrayList.clear(); // Clear existing data

                assert value != null;
                // Add new data from the snapshot to the list
                customerArrayList.addAll(value.toObjects(Customer.class));
                // Sort the list based on customer period
                customerArrayList.sort((o1, o2) -> o2.getCustomerPeriod().compareTo(o1.getCustomerPeriod()));
                // Notify the adapter that the data set has changed
                customerAdapter.notifyDataSetChanged();

                if (dialog.isShowing()) {
                    dialog.dismiss();
                }

                // Additional operations after data retrieval can be performed here
            }
        }));
    }
}
