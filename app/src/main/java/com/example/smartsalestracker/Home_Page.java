package com.example.smartsalestracker;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Home_Page extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<Product> productArrayList;
    MyAdapter myAdapter;
    FirebaseFirestore db;
    private FirebaseAuth mAuth;
    ImageButton btn_profile,btn_cart,btn_logout;
    TextView username;
    static TextView cart_count;

    Dialog dialog;
    private FloatingActionButton add;

    SearchView searchView;

    // SwipeRefreshLayout
    private SwipeRefreshLayout mySwipeRefreshLayout;

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

    //search filter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);


        // Initialize views
        recyclerView = findViewById(R.id.recycler_view);
        productArrayList = new ArrayList<>();
        btn_profile = findViewById(R.id.profile);
        btn_cart = findViewById(R.id.cart1);
        btn_logout = findViewById(R.id.logout);
        username = findViewById(R.id.user);
        add = findViewById(R.id.add);
        searchView = findViewById(R.id.search);
        mySwipeRefreshLayout = findViewById(R.id.main);
        cart_count = findViewById(R.id.cart_count);

        // Add button
        add.setOnClickListener(v -> {
            startActivity(new Intent(Home_Page.this, Item_Add.class));

        });

        // Logout button
        btn_logout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(Home_Page.this, Sign_In.class));
            finish();
        });

        // Profile button
        btn_profile.setOnClickListener(v -> {
            startActivity(new Intent(Home_Page.this, Item_Management.class));
        });

        // Cart button
        btn_cart.setOnClickListener(v -> {
            startActivity(new Intent(Home_Page.this, Items_Cart.class));
        });

        // call method for search on click
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,3));

        //dialog box
        dialog = new MaterialAlertDialogBuilder(this)
                .setView(new ProgressBar(this))
                .setTitle("Fetching Data...")
                .setMessage("Please wait")
                .create();
        dialog.show();

    //set values to recycler view
        productArrayList = new ArrayList<>();
        myAdapter = new MyAdapter(Home_Page.this, productArrayList);
        recyclerView.setAdapter(myAdapter);

        //get data from firebase method
        db = FirebaseFirestore.getInstance();
        EventChangeListener();

        //get current user details method
        CurrentUserDetails();

        // SwipeRefreshLayout
        mySwipeRefreshLayout.setOnRefreshListener(() -> {
                    //reload activity
                    //finish();
                    //startActivity(getIntent())
                    //or
                  //clear the recycler view if its occupied and then fetch the data again
                         productArrayList.clear();
                         myAdapter.notifyDataSetChanged();
                        EventChangeListener();

                        mySwipeRefreshLayout.setRefreshing(false);
                }
        );

    }

    // Filter method
    private void filter(String text) {
        ArrayList<Product> filteredList = new ArrayList<>();
        for (Product item : productArrayList) {
            if (item.name.toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        myAdapter.filterList(filteredList);
    }

    private void CurrentUserDetails() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String user1 = user.getUid();
        db.collection(user1).document("User_Details").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                username.setText(task.getResult().getString("name"));
            }
        });
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
                for (Product product : value.toObjects(Product.class)) {
                    productArrayList.add(product);
                }
                myAdapter.notifyDataSetChanged();
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        }));

    }
}