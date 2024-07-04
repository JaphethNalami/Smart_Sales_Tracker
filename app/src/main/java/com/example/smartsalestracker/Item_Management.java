package com.example.smartsalestracker;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class Item_Management extends AppCompatActivity {


    RecyclerView recyclerView;
    Spinner spinner;
    Button sortCategory;
    ArrayList<Product> productArrayList;
    UpdateAdapter myAdapter1;
    FirebaseFirestore db;
    private FirebaseAuth mAuth;
    FloatingActionButton fab;

    FirebaseUser user;
    String userId;
    String categorySort;


    Dialog dialog;
    ImageButton back;

    private boolean doubleBackToExitPressedOnce = false;
    private static final int BACK_PRESS_DELAY = 2000; // 2 second

    // Override the onBackPressed method

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            finishAffinity();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce=false, BACK_PRESS_DELAY);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_item_management);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        fab = findViewById(R.id.refresh);
        spinner = findViewById(R.id.categorySpinner);
        sortCategory = findViewById(R.id.sort);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        assert user != null;
        userId = user.getUid();

        // Refresh button
        fab.setOnClickListener(v -> {
            productArrayList.clear();
            EventChangeListener();
        });

        back = findViewById(R.id.back);
        back.setOnClickListener(v -> {
            finish();
        });

        // Sort by category
        sortCategory.setOnClickListener(v -> {
            String category = spinner.getSelectedItem().toString();
            if (category.equals("All")) {
                productArrayList.clear();
                EventChangeListener();
            } else {
                productArrayList.clear();
                categorySort = category;
                CategoryListener();
                }
        });


        recyclerView = findViewById(R.id.recycler_view);
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
        myAdapter1 = new UpdateAdapter(Item_Management.this, productArrayList);
        recyclerView.setAdapter(myAdapter1);

        db = FirebaseFirestore.getInstance();
        EventChangeListener();

        ObtainCategory();

    }

    private void ObtainCategory() {

        db.collection(userId).document("Shop").collection("Categories").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<String> categories = new ArrayList<>();
                    categories.add(0, "All");
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String categoryName = document.getString("name");
                        categories.add(categoryName);
                    }

                    // For example, if you have a spinner:
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(Item_Management.this, android.R.layout.simple_spinner_item, categories);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);

                })
                .addOnFailureListener(e -> {
                    Log.e("Error", Objects.requireNonNull(e.getMessage()));
                });


    }

    private void EventChangeListener() {
        db.collection(userId).document("Shop").collection("Products").addSnapshotListener((new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    return;
                }
                assert value != null;
                productArrayList.addAll(value.toObjects(Product.class));
                productArrayList.sort((o1, o2) -> o1.getName().compareTo(o2.getName()));
                myAdapter1.notifyDataSetChanged();
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        }));

    }

    private void CategoryListener() {
        db.collection(userId).document("Shop").collection("Products").addSnapshotListener((new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    return;
                }
                assert value != null;
                //add items whose category matches the selected category
                for (QueryDocumentSnapshot document : value) {
                    if (Objects.equals(document.getString("category"), categorySort)) {
                        productArrayList.add(document.toObject(Product.class));
                    }
                }
                productArrayList.sort((o1, o2) -> o1.getName().compareTo(o2.getName()));
                myAdapter1.notifyDataSetChanged();
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        }));

    }
}