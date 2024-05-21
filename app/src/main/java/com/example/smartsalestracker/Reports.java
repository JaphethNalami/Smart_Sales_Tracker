package com.example.smartsalestracker;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;

public class Reports extends AppCompatActivity {

    RelativeLayout lowStock,remainingStock;
    TextView lowStockText,remainingStockText,dateText,totalSalesText,salesText,mpesa,cash;
    ImageButton previousDay, currentDay;
    ListView salesList;
    int count = 0;
    String date;

    private FirebaseFirestore db ;
    private FirebaseUser user;
    private String userId;
    private FirebaseAuth mAuth;
    Dialog dialog;

    //listview adapter
    private ArrayAdapter<String> adapter;
    private ArrayList<String> orderList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        //initialize views
        lowStock = findViewById(R.id.low_stock_layout);
        remainingStock = findViewById(R.id.remaining_stock_layout);
        lowStockText = findViewById(R.id.low_stock_text);
        remainingStockText = findViewById(R.id.remaining_stock_text);
        dateText = findViewById(R.id.date);
        totalSalesText = findViewById(R.id.total_sales_text);
        salesText = findViewById(R.id.sale_text);
        mpesa = findViewById(R.id.mpesa);
        cash = findViewById(R.id.cash);
        previousDay = findViewById(R.id.previous_date);
        currentDay = findViewById(R.id.next_date);
        salesList = findViewById(R.id.listView);

        //initialize the firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userId = Objects.requireNonNull(user).getUid();

        //dialog
        dialog = new MaterialAlertDialogBuilder(this)
                .setView(new ProgressBar(this))
                .setTitle("Fetching Data...")
                .setMessage("Please wait")
                .create();
        dialog.show();

        //get current date
        LocalDate currentDate = LocalDate.now();
         date = currentDate.toString();

        // Set onclick listeners
        previousDay.setOnClickListener(v -> {
            count++;
            // Count should not exceed 6
            if (count < 6) {
                // Get previous dates and display
                LocalDate currentDate1 = LocalDate.now(); // Renamed to avoid confusion
                // Clear text before setting new date
                dateText.setText("");
                for (int i = 1; i <= count; i++) {
                    LocalDate previousDate = currentDate1.minusDays(i); // Use currentDate instead of LocalDate.now()
                    String month = previousDate.getMonth().toString();
                    month = month.substring(0, 1).toUpperCase() + month.substring(1).toLowerCase();
                    int previousDay = previousDate.getDayOfMonth();

                    //store the date in a string
                     date = previousDate.toString();

                    if (i == 1) {
                        dateText.setText("Yesterday: " + previousDay + " " + month);
                    } else {
                        dateText.setText(previousDay + " " + month);
                    }
                }
            }
        });

        currentDay.setOnClickListener(v -> {
        LocalDate currentDate1 = LocalDate.now();
        String month = currentDate1.getMonth().toString();
        month = month.substring(0,1).toUpperCase() + month.substring(1).toLowerCase();
        int day = currentDate1.getDayOfMonth();
        dateText.setText("Today: " +day + " " + month);

        String date = currentDate1.toString();

        });

        //method to get low stock items
        getLowStock();

        //method to get remaining stock items
        getRemainingStock();

        //method data from database using the date
        getData(date);


        //initialize the listview
        orderList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, orderList);
        salesList.setAdapter(adapter);
    }

    private void getRemainingStock() {

    }

    private void getLowStock() {

    }

    private void getData(String date) {

    }

}