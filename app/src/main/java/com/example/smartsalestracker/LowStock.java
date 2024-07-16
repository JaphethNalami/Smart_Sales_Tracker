package com.example.smartsalestracker;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ProgressBar;

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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class LowStock extends AppCompatActivity {

    ImageButton back;
    RecyclerView recyclerView;
    FloatingActionButton fab;

    ArrayList<Product> productArrayList;
    ReportAdapter reportAdapter;
    FirebaseFirestore db;
    private FirebaseAuth mAuth;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_low_stock);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        back = findViewById(R.id.back);
        fab = findViewById(R.id.fab);

        back.setOnClickListener(v -> {
            finish();
        });

        fab.setOnClickListener(v -> {
            //clear arraylist
            productArrayList.clear();
            reportAdapter.notifyDataSetChanged();
            EventChangeListener();
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //dialog
        dialog = new MaterialAlertDialogBuilder(this)
                .setView(new ProgressBar(this))
                .setTitle("Fetching Data...")
                .setMessage("Please wait")
                .create();

        dialog.show();


        productArrayList = new ArrayList<>();
        reportAdapter = new ReportAdapter(LowStock.this, productArrayList);
        recyclerView.setAdapter(reportAdapter);

        //clear arraylist
        productArrayList.clear();
        reportAdapter.notifyDataSetChanged();

        db = FirebaseFirestore.getInstance();
        EventChangeListener();

    }

    private void EventChangeListener() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user1 = mAuth.getCurrentUser();
        String user2 = user1.getUid();
        db.collection(user2).document("Shop").collection("Products").addSnapshotListener((new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    return;
                }

                //clear arraylist
                productArrayList.clear();

                for (Product product : value.toObjects(Product.class)) {

                    // Check if quantity is less than 1
                    if (Integer.parseInt(product.quantity) < 1) {
                        productArrayList.add(product);
                    }
                }
                reportAdapter.notifyDataSetChanged();
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        }));

    }
}