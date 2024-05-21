package com.example.smartsalestracker;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;

public class Reports extends AppCompatActivity {

    RelativeLayout lowStock,remainingStock;
    TextView lowStockText,remainingStockText,dateText,totalSalesText,salesText,mpesa,cash,mpesaText,cashText;
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
    private ArrayList<ReportClass> itemsArrayList;


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
        mpesaText = findViewById(R.id.mpesa_text);
        cashText = findViewById(R.id.cash_text);
        previousDay = findViewById(R.id.previous_date);
        currentDay = findViewById(R.id.next_date);
        salesList = findViewById(R.id.listView);
        itemsArrayList = new ArrayList<>();

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
            else {
                count = 6;
            }
            //clear arraylist
            itemsArrayList.clear();
            orderList.clear();
            totalSalesText.setText("Total Sales: 0");
            mpesa.setText("Mpesa: 0");
            cash.setText("Cash: 0");
            cashText.setText("Cash: 0");
            mpesaText.setText("Mpesa: 0");
            salesText.setText("Sales: 0");

            //clear listview
            adapter.clear();
            adapter.notifyDataSetChanged();
            getData();
        });

        currentDay.setOnClickListener(v -> {
            count--;
            // Count should not be less than 0
            if (count >= 0) {
                // Get next dates and display
                LocalDate currentDate1 = LocalDate.now(); // Renamed to avoid confusion
                // Clear text before setting new date
                dateText.setText("");
                for (int i = 0; i <= count; i++) {
                    LocalDate previousDate = currentDate1.minusDays(i); // Use currentDate instead of LocalDate.now()
                    String month = previousDate.getMonth().toString();
                    month = month.substring(0,1).toUpperCase() + month.substring(1).toLowerCase();
                    int previousDay = previousDate.getDayOfMonth();

                    //store the date in a string
                    date = previousDate.toString();

                    if (i == 0) {
                        dateText.setText("Today: " + previousDay + " " + month);
                    }
                    else if (i == 1) {
                        dateText.setText("Yesterday: " + previousDay + " " + month);
                    } else {
                        dateText.setText(previousDay + " " + month);
                    }
                }
            }
            else {
                count = 0;
            }
            //clear arraylist
            itemsArrayList.clear();
            orderList.clear();
            totalSalesText.setText("Total Sales: 0");
            mpesa.setText("Mpesa: 0");
            cash.setText("Cash: 0");
            cashText.setText("Cash: 0");
            mpesaText.setText("Mpesa: 0");
            salesText.setText("Sales: 0");

            //clear listview
            adapter.clear();
            adapter.notifyDataSetChanged();

            getData();

        });

        lowStock.setOnClickListener(v -> {
            //navigate to low stock activity
            startActivity(new Intent(Reports.this, LowStock.class));
        });

        remainingStock.setOnClickListener(v -> {
            //navigate to remaining stock activity
            startActivity(new Intent(Reports.this, RemainingStock.class));
        });

        //method to get low stock items
        getLowStock();

        //method to get remaining stock items
        getRemainingStock();

        //method data from database using the date
        getData();

        //initialize the listview
        orderList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, orderList);
        salesList.setAdapter(adapter);

    }

    private void getRemainingStock() {

        //get name of the item and the quantity and diplay count of those having quantity greater than 0
        db.collection(userId)
                .document("Shop")
                .collection("Products")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        // Handle the error
                        Log.e(TAG, "Error getting remaining stock data: ", error);
                        return;
                    }

                    if (value.isEmpty()) {
                        // Display a message if no remaining stock data is available
                        Toast.makeText(Reports.this, "No remaining stock data available", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // Add retrieved items to the list
                    int remainingStockCount = 0;
                    for (DocumentSnapshot doc : value.getDocuments()) {
                        Product product = doc.toObject(Product.class);
                        if (product != null) {
                            //convrt quantity to integer
                            int quantity = Integer.parseInt(product.getQuantity());
                            if (quantity > 0) {
                                remainingStockCount++;
                            }
                        }
                    }
                    remainingStockText.setText(remainingStockCount + "  Items");
                });

    }

    private void getLowStock() {

        //get name of the item and the quantity and diplay count of those having quantity 0
        db.collection(userId)
                .document("Shop")
                .collection("Products")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        // Handle the error
                        Log.e(TAG, "Error getting low stock data: ", error);
                        return;
                    }

                    if (value.isEmpty()) {
                        // Display a message if no low stock data is available
                        Toast.makeText(Reports.this, "No low stock data available", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // Add retrieved items to the list
                    int lowStockCount = 0;
                    for (DocumentSnapshot doc : value.getDocuments()) {
                        Product product = doc.toObject(Product.class);
                        if (product != null) {
                            //convrt quantity to integer
                            int quantity = Integer.parseInt(product.getQuantity());
                            if (quantity == 0) {
                                lowStockCount++;
                            }
                        }
                    }
                    lowStockText.setText(lowStockCount + "  Items");
                });

    }

    private void getData() {
        // Clear the list before adding new items
        itemsArrayList.clear();

        db.collection(userId)
                .document("Shop")
                .collection("Sales_Reports")
                .document(date)
                .collection("Items")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        // Handle the error
                        Log.e(TAG, "Error getting sales data: ", error);
                        return;
                    }

                    if (value.isEmpty()) {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        // Display a message if no sales data is available
                        Toast.makeText(Reports.this, "No sales data available for the selected date", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // Add retrieved items to the list
                    for (DocumentSnapshot doc : value.getDocuments()) {
                        ReportClass reportClass = doc.toObject(ReportClass.class);
                        if (reportClass != null) {
                            itemsArrayList.add(reportClass);
                            // Add the item to the listview
                            orderList.add(Objects.requireNonNull(reportClass).getProductName() + " \n" +
                                    "Count: " + reportClass.getItemCount());
                            adapter.notifyDataSetChanged();
                        }
                    }
                    salesText.setText(itemsArrayList.size()+"  Items");

                    //convert to integer and add all totalPrices to get total sales
                    float totalSales = 0;
                    for (ReportClass reportClass : itemsArrayList) {
                        totalSales += Float.parseFloat(reportClass.getTotalPrice());
                    }
                    totalSalesText.setText("KSH: " + totalSales);

                    //get and count the number of mpesa and cash payments
                    int mpesaCount = 0;
                    int cashCount = 0;
                    float mpesaTotal = 0;
                    float cashTotal = 0;

                    for (ReportClass reportClass : itemsArrayList) {
                        if (reportClass.getPaymentMethod().equals("Mpesa")) {
                            mpesaCount++;
                            // accumulate totalPrice for mpesa payments
                            mpesaTotal += Float.parseFloat(reportClass.getTotalPrice());
                        } else {
                            cashCount++;
                            // accumulate totalPrice for cash payments
                            cashTotal += Float.parseFloat(reportClass.getTotalPrice());
                        }
                    }
                    // Update TextViews with the accumulated totals
                    mpesa.setText(mpesaCount +"  Items");
                    cash.setText( cashCount +"  Items");
                    mpesaText.setText("KSH: " + mpesaTotal);
                    cashText.setText("KSH: " + cashTotal);

                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                });
    }


}