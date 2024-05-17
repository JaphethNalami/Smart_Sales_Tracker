package com.example.smartsalestracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Navigation_menu extends AppCompatActivity {

    ImageButton btn_clients, btn_products, btn_user_profile, btn_analysis, btn_exit;
    TextView txt_name, txt_address, txt_category , txt_date, txt_time;

    private FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_menu);

        //views
        btn_products = findViewById(R.id.products);
        btn_analysis = findViewById(R.id.analysis);
        btn_clients = findViewById(R.id.customer);
        btn_user_profile = findViewById(R.id.user_profile);
        btn_exit = findViewById(R.id.exit);
        txt_category = findViewById(R.id.category);
        txt_address = findViewById(R.id.shop_address);
        txt_name = findViewById(R.id.shop_name);
        txt_date = findViewById(R.id.date);
        txt_time = findViewById(R.id.time);
        db = FirebaseFirestore.getInstance();

        //btn_products click event
        btn_products.setOnClickListener(v -> {
            startActivity(new Intent(Navigation_menu.this, Item_Management.class));
        });

        // btn_exit click event
        btn_exit.setOnClickListener(v -> {
           // onBackPressed();
            //go back to home page
            startActivity(new Intent(Navigation_menu.this, Home_Page.class));
        });

        // btn_analysis click event
        btn_analysis.setOnClickListener(v -> {
            startActivity(new Intent(Navigation_menu.this, SalesAnalysis.class));
        });

        // btn_analysis click event
        btn_clients.setOnClickListener(v -> {
            startActivity(new Intent(Navigation_menu.this, CustomerDisplay.class));
        });

        CurrentShopDetails();

        //get current date
        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        //display current date and time
        txt_date.setText(date);

        //get current time and auto update every second
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(() -> {
                            String time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                            txt_time.setText(time);
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();



    }

    private void CurrentShopDetails() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String user1 = user.getUid();
        db.collection(user1).document("Shop_Details").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                txt_name.setText(task.getResult().getString("shop_name"));
                txt_address.setText(task.getResult().getString("shop_address"));
                txt_category.setText(task.getResult().getString("shop_type"));
            }
        });
    }
}