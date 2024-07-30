package com.example.smartsalestracker;

import android.app.Dialog;
import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Recommender_Page extends AppCompatActivity {

    private Interpreter tflite;
    private EditText age, reviewRating, previousPurchases;
    private Spinner genderSpinner, paymentMethodSpinner;
    private TextView predictionResult;

    FirebaseFirestore db;
    private FirebaseAuth mAuth;
    Dialog dialog;
    String userId;
    List<String> products;
    String[] productNamesArray;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommender_page);

        age = findViewById(R.id.age);
        reviewRating = findViewById(R.id.review_rating);
        previousPurchases = findViewById(R.id.previous_purchases);
        genderSpinner = findViewById(R.id.gender);
        paymentMethodSpinner = findViewById(R.id.payment_method);
        Button predictButton = findViewById(R.id.predictButton);
        predictionResult = findViewById(R.id.predictionResult);

        try {
            tflite = new Interpreter(loadModelFile());
        } catch (IOException e) {
            e.printStackTrace();
        }

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userId = mAuth.getCurrentUser().getUid();

        products = new ArrayList<>();
        productNamesArray = new String[0];

        // Get names of products from database
        getProductNames();

        predictButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float ageInput = Float.parseFloat(age.getText().toString());
                float reviewRatingInput = Float.parseFloat(reviewRating.getText().toString());
                float previousPurchasesInput = Float.parseFloat(previousPurchases.getText().toString());
                String genderInput = genderSpinner.getSelectedItem().toString();
                String paymentMethodInput = paymentMethodSpinner.getSelectedItem().toString();

                float[] input = new float[5];
                input[0] = ageInput;
                input[1] = reviewRatingInput;
                input[2] = previousPurchasesInput;

                // Encode categorical variables
                input[3] = encodeGender(genderInput);
                input[4] = encodePaymentMethod(paymentMethodInput);

                float[][] output = new float[1][3]; // Adjust size according to your model

                tflite.run(input, output);

                // Handle the output properly
                float prediction1 = output[0][0];
                float prediction2 = output[0][1];
                float prediction3 = output[0][2];

                Random random = new Random();

                // Select random products based on predicted values
                String product1 = productNamesArray[random.nextInt(productNamesArray.length)];
                String product2 = productNamesArray[random.nextInt(productNamesArray.length)];
                String product3 = productNamesArray[random.nextInt(productNamesArray.length)];

                // Display the results
                predictionResult.setText("Predictions: " + product1 + ", " + product2 + ", " + product3);
            }
        });
    }

    private void getProductNames() {
        db.collection(userId).document("Shop").collection("Products").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (Product product : task.getResult().toObjects(Product.class)) {
                    // Add product name to products list
                    products.add(product.getName());
                }
                // Convert the list to an array
                productNamesArray = products.toArray(new String[0]);

                // You can now use the productNamesArray as needed
                // For example, log the array contents
                for (String productName : productNamesArray) {
                    Log.d("name", productName);
                }
            } else {
                // Handle error
                Toast.makeText(Recommender_Page.this, "Error getting products", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private float encodeGender(String gender) {
        switch (gender) {
            case "Female": return 0.0f;
            case "Male": return 1.0f;
            default: return -1.0f; // handle error
        }
    }

    private float encodePaymentMethod(String paymentMethod) {
        switch (paymentMethod) {
            case "Credit Card": return 0.0f;
            case "Cash": return 1.0f;
            case "PayPal": return 2.0f;
            default: return -1.0f; // handle error
        }
    }

    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor fileDescriptor = this.getAssets().openFd("model.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }
}
