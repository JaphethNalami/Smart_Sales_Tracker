package com.example.smartsalestracker;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;
import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SalesAnalysis extends AppCompatActivity {

    AnyChartView anyChartView;
    RecyclerView recyclerView;

    private FirebaseFirestore db ;
    private FirebaseUser user;
    private String userId;
    private FirebaseAuth mAuth;
    Dialog dialog;
    ArrayList<Product> productArrayList;
    ArrayList<DataEntry> data;
    ArrayList<String> products;
    TextView textView;
    FloatingActionButton fab;
    ImageButton back;
    ShapeableImageView maxProductImage,quantityProductImage,highPriceProductImage,lowPriceProductImage,lowQuantityProductImage;
    TextView maxProductTextView, maxProductQuantityTextView,quantityNameTextView,quantityQuantityTextView,highPriceNameTextView,highPriceTextView,lowPriceNameTextView,lowPriceTextView,lowQuantityNameTextView,lowQuantityTextView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_analysis);

        //initialize the views
        anyChartView = findViewById(R.id.any_chart_view);
        productArrayList = new ArrayList<>();
        products = new ArrayList<>();
        textView = findViewById(R.id.textView);
        fab = findViewById(R.id.fab);
        maxProductImage = findViewById(R.id.max_image);
        maxProductTextView = findViewById(R.id.max_name);
        maxProductQuantityTextView = findViewById(R.id.max_quantity);
        quantityProductImage = findViewById(R.id.quantityImage);
        quantityNameTextView = findViewById(R.id.quantityName);
        quantityQuantityTextView = findViewById(R.id.quantityQuantity);
        highPriceProductImage = findViewById(R.id.highPriceImage);
        highPriceNameTextView = findViewById(R.id.highPriceName);
        highPriceTextView = findViewById(R.id.highPriceQuantity);
        lowPriceProductImage = findViewById(R.id.lowPriceImage);
        lowPriceNameTextView = findViewById(R.id.lowPriceName);
        lowPriceTextView = findViewById(R.id.lowPriceQuantity);
        lowQuantityProductImage = findViewById(R.id.lowQuantityImage);
        lowQuantityNameTextView = findViewById(R.id.lowQuantityName);
        lowQuantityTextView = findViewById(R.id.lowQuantityQuantity);
        back = findViewById(R.id.exit);

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

        //get the data from the database
        getDataFromDatabase();


        // Refresh button
        fab.setOnClickListener(v -> {
            //set data to the anyChartView
            setChartData();
            //set the maximum product
            maximumProduct();

            highestQuantityProduct();
            lowestQuantityProduct();
            lowestPriceProduct();
            highestPriceProduct();
        });

        //back button
        back.setOnClickListener(v -> {
            //finish();
            startActivity(new Intent(SalesAnalysis.this, Navigation_menu.class));
        });

    }

    private void lowestQuantityProduct() {
        // the highest selling product and display it in the textview
        int min1 = 1000000;
        String minProduct1 = "";
        String image1 = "";
        for (Product product : productArrayList) {
            if (Integer.parseInt(product.getQuantity()) < min1) {

                min1 = Integer.parseInt(product.getQuantity());
                minProduct1 = product.getName();
                image1 = product.getImage();
                lowQuantityNameTextView.setText(minProduct1);
                //set string value of max
                lowQuantityTextView.setText(String.valueOf(min1));
                //load the image from the url
                Glide.with(this).load(image1).into(lowQuantityProductImage);
            }
        }
    }

    private void lowestPriceProduct() {
        // the highest selling product and display it in the textview
        int min1 = 1000000;
        String minProduct1 = "";
        String image1 = "";
        for (Product product : productArrayList) {
            if (Integer.parseInt(product.getPrice()) < min1) {

                min1 = Integer.parseInt(product.getPrice());
                minProduct1 = product.getName();
                image1 = product.getImage();
                lowPriceNameTextView.setText(minProduct1);
                //set string value of max
                lowPriceTextView.setText(String.valueOf(min1));
                //load the image from the url
                Glide.with(this).load(image1).into(lowPriceProductImage);
            }
        }
    }

    private void highestPriceProduct() {
        // the highest selling product and display it in the textview
        int max1 = 0;
        String maxProduct1 = "";
        String image1 = "";
        for (Product product : productArrayList) {
            if (Integer.parseInt(product.getPrice()) > max1) {

                max1 = Integer.parseInt(product.getPrice());
                maxProduct1 = product.getName();
                image1 = product.getImage();
                highPriceNameTextView.setText(maxProduct1);
                //set string value of max
                highPriceTextView.setText(String.valueOf(max1));
                //load the image from the url
                Glide.with(this).load(image1).into(highPriceProductImage);
            }
        }
    }

    private void highestQuantityProduct() {
        // the highest selling product and display it in the textview
        int max1 = 0;
        String maxProduct1 = "";
        String image1 = "";
        for (Product product : productArrayList) {
            if (Integer.parseInt(product.getQuantity()) > max1) {

                max1 = Integer.parseInt(product.getQuantity());
                maxProduct1 = product.getName();
                image1 = product.getImage();
                quantityNameTextView.setText(maxProduct1);
                //set string value of max
                quantityQuantityTextView.setText(String.valueOf(max1));
                //load the image from the url
                Glide.with(this).load(image1).into(quantityProductImage);
            }
        }
    }

    private void maximumProduct() {
        // the highest selling product and display it in the textview
        int max = 0;
        String maxProduct = "";
        String image = "";
        for (Product product : productArrayList) {
            if (Integer.parseInt(product.getSoldQuantity()) > max) {
                max = Integer.parseInt(product.getSoldQuantity());
                maxProduct = product.getName();
                image = product.getImage();
                maxProductTextView.setText(maxProduct);
                //set string value of max
                maxProductQuantityTextView.setText(String.valueOf(max));
                //load the image from the url
                Glide.with(this).load(image).into(maxProductImage);
            }
        }
    }


    private void getDataFromDatabase() {
        // Show a dialog indicating data retrieval is in progress
        dialog.show();

        db.collection(userId).document("Shop").collection("Products").addSnapshotListener((new EventListener<QuerySnapshot>() {
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
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                //set data to the anyChartView
                setChartData();

                //set the maximum product
                maximumProduct();

                //set the highest quantity product
                highestQuantityProduct();

                //set the lowest quantity product
                lowestQuantityProduct();

                //set the lowest price product
                lowestPriceProduct();

                //set the highest price product
                highestPriceProduct();
            }
        }));
    }

    private void setChartData() {
        Cartesian cartesian = AnyChart.column();

        data = new ArrayList<>();
        for (Product product : productArrayList) {
            data.add(new ValueDataEntry(product.getName(), Integer.parseInt(product.getSoldQuantity())));
        }

        Column column = cartesian.column(data);

        column.tooltip()
                .titleFormat("{%X}")
                .position(Position.CENTER_BOTTOM)
                .anchor(Anchor.CENTER_BOTTOM)
                .offsetX(0d)
                .offsetY(5d)
                .format("{%Value}{groupsSeparator: }");

        cartesian.animation(true);
        cartesian.title("Product Sales Analysis");

        cartesian.yScale().minimum(0d);

        cartesian.yAxis(0).labels().format("{%Value}{groupsSeparator: }");

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.interactivity().hoverMode(HoverMode.BY_X);

        cartesian.xAxis(0).title("Product");
        cartesian.yAxis(0).title("Quantity Sold");

        anyChartView.setChart(cartesian);

    }

}