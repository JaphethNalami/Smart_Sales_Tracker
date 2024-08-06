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
                String ageText = age.getText().toString();
                String reviewRatingText = reviewRating.getText().toString();
                String previousPurchasesText = previousPurchases.getText().toString();
                String genderInput = genderSpinner.getSelectedItem().toString();
                String paymentMethodInput = paymentMethodSpinner.getSelectedItem().toString();

                // Check for empty input fields
                if (ageText.isEmpty()) {
                    Toast.makeText(Recommender_Page.this, "Please enter a valid age", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (reviewRatingText.isEmpty()) {
                    Toast.makeText(Recommender_Page.this, "Please enter a valid review rating", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (previousPurchasesText.isEmpty()) {
                    Toast.makeText(Recommender_Page.this, "Please enter a valid number of previous purchases", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (genderInput.isEmpty()) {
                    Toast.makeText(Recommender_Page.this, "Please select gender", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (paymentMethodInput.isEmpty()) {
                    Toast.makeText(Recommender_Page.this, "Please select payment method", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Parse the input values
                float ageInput = Float.parseFloat(ageText);
                float reviewRatingInput = Float.parseFloat(reviewRatingText);
                float previousPurchasesInput = Float.parseFloat(previousPurchasesText);

                // Validate the numeric input ranges
                if (ageInput < 0 || ageInput > 70) {
                    Toast.makeText(Recommender_Page.this, "Please enter a valid age", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (reviewRatingInput < 0 || reviewRatingInput > 10) {
                    Toast.makeText(Recommender_Page.this, "Please enter a valid review rating", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (previousPurchasesInput < 0 || previousPurchasesInput > 100) {
                    Toast.makeText(Recommender_Page.this, "Please enter a valid number of previous purchases", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create input array for the model
                float[] input = new float[5];
                input[0] = ageInput;
                input[1] = reviewRatingInput;
                input[2] = previousPurchasesInput;
                input[3] = encodeGender(genderInput); // Encode gender
                input[4] = encodePaymentMethod(paymentMethodInput); // Encode payment method

                // Prepare output array for the model prediction
                float[][] output = new float[1][3]; // Adjust size according to your model

                // Run the model
                tflite.run(input, output);

                // Handle the output properly
                float prediction1 = output[0][0];
                float prediction2 = output[0][1];
                float prediction3 = output[0][2];

                // Round off predictions to 2 decimal places and convert to percentages
                prediction1 = Math.round(prediction1 * 100.0) / 100.0f;
                prediction2 = Math.round(prediction2 * 100.0) / 100.0f;

                // Multiply by 100 to get percentage and convert to integer
                int prediction1Int = (int) (prediction1 * 100);
                int prediction2Int = (int) (prediction2 * 100);

                // Set the prediction result in the UI
               // predictionResult.setText("Predictions: " + prediction1Int + "%, " + prediction2Int + "%");

                //display name in string productNamesArray in index 10
                displayValuesAtIndices(prediction1Int, prediction2Int);





            }
        });

    }

    private void displayValuesAtIndices(int index1, int index2) {
        StringBuilder displayText = new StringBuilder();

        // Check and display the first index
        if (index1 >= 0 && index1 < productNamesArray.length) {
            String value1 = productNamesArray[index1];
            displayText.append("Prediction ").append("1").append(": ").append(value1).append("\n");
        } else {
            displayText.append("Prediction ").append("1").append(" not available\n");
        }

        // Check and display the second index
        if (index2 >= 0 && index2 < productNamesArray.length) {
            String value2 = productNamesArray[index2];
            displayText.append("Prediction ").append("2").append(": ").append(value2).append("\n");
        } else {
            displayText.append("Prediction ").append("2").append(" not available\n");
        }

        predictionResult.setText(displayText.toString());
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
