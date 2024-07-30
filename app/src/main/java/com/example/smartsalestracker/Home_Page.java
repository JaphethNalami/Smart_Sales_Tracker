package com.example.smartsalestracker;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
    FirebaseUser user;
    ImageButton btn_navigation,btn_cart,btn_logout;
    Button recommender,inventory,analysis,reports,clients,user_profile;
    TextView username;
    static TextView cart_count,current_total1;

    Dialog dialog;
    FloatingActionButton add;
    SearchView searchView;
    String userId;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // Initialize views
        recyclerView = findViewById(R.id.recycler_view);
        productArrayList = new ArrayList<>();
        btn_navigation = findViewById(R.id.navigation);
        btn_cart = findViewById(R.id.cart1);
        btn_logout = findViewById(R.id.logout);
        username = findViewById(R.id.user);
        add = findViewById(R.id.add);
        searchView = findViewById(R.id.search);
        mySwipeRefreshLayout = findViewById(R.id.main);
        cart_count = findViewById(R.id.cart_count);
        current_total1 = findViewById(R.id.current_total);
        recommender = findViewById(R.id.recommender);
        inventory = findViewById(R.id.inventory);
        analysis = findViewById(R.id.analysis);
        reports = findViewById(R.id.reports);
        clients = findViewById(R.id.clients);
        user_profile = findViewById(R.id.profile);

        //firebase initialization
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();
        assert user != null;
        userId = user.getUid();


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
        btn_navigation.setOnClickListener(v -> {
            startActivity(new Intent(Home_Page.this, Navigation_menu.class));
        });

        // Cart button
        btn_cart.setOnClickListener(v -> {
            startActivity(new Intent(Home_Page.this, Items_Cart.class));
        });

        //current count textview
        current_total1.setOnClickListener(v -> {
            startActivity(new Intent(Home_Page.this, Items_Cart.class));
        });

        //recommender button
        recommender.setOnClickListener(v -> {
            startActivity(new Intent(Home_Page.this, Recommender_Page.class));
        });

        //inventory button
        inventory.setOnClickListener(v -> {
            startActivity(new Intent(Home_Page.this, Item_Management.class));
        });

        //analysis button
        analysis.setOnClickListener(v -> {
            startActivity(new Intent(Home_Page.this, SalesAnalysis.class));
        });

        //reports button
        reports.setOnClickListener(v -> {
            startActivity(new Intent(Home_Page.this, Reports.class));
        });

        //clients button
        clients.setOnClickListener(v -> {
            startActivity(new Intent(Home_Page.this, CustomerDisplay.class));
        });

        //user_profile button
        user_profile.setOnClickListener(v -> {
            startActivity(new Intent(Home_Page.this, User_Profile_Update.class));
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

        //get current user details method
        CurrentUserDetails();
        EventChangeListener();

        // SwipeRefreshLayout
        mySwipeRefreshLayout.setOnRefreshListener(() -> {

            //clear the arraylist
            productArrayList.clear();

            //notify the adapter
            myAdapter.notifyDataSetChanged();

            //call the method to fetch the data
            EventChangeListener();

            //set the adapter again
            myAdapter = new MyAdapter(Home_Page.this, productArrayList);
            recyclerView.setAdapter(myAdapter);
            mySwipeRefreshLayout.setRefreshing(false);
                }
        );

        //check if cart is empty
        if (Product_Cart.getInstance().getSelectedProducts().isEmpty()) {
            //make cart count invisible
            cart_count.setVisibility(View.INVISIBLE);

        } else {
            //make cart count visible
            cart_count.setVisibility(View.VISIBLE);
            //set the cart count
            cart_count.setText(String.valueOf(Product_Cart.getInstance().getSelectedProducts().size()));
        }
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
        db.collection(userId).document("User_Details").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                username.setText(task.getResult().getString("name"));
            }
        });
    }

    private void EventChangeListener() {
        db.collection(userId).document("Shop").collection("Products").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    return;
                }
                assert value != null;
                //clear the arraylist
                productArrayList.clear();

                //add all products to the arraylist
                productArrayList.addAll(value.toObjects(Product.class));

                //sort the products by name
                productArrayList.sort((o1, o2) -> o1.getName().compareTo(o2.getName()));

                myAdapter.notifyDataSetChanged();
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });

    }
}