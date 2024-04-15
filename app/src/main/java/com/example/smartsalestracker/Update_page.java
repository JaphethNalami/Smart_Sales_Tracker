package com.example.smartsalestracker;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class Update_page extends AppCompatActivity {

    TextInputEditText Name, Price, Quantity;
    TextView Category, Barcode, Id,Units;
    Button update,delete;
    ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        Name = findViewById(R.id.item_name);
        Price = findViewById(R.id.price);
        Quantity = findViewById(R.id.quantity);
        Category = findViewById(R.id.item_category);
        Barcode = findViewById(R.id.item_barcode);
        Id = findViewById(R.id.item_id);
        Units = findViewById(R.id.current_quantity);
        update = findViewById(R.id.update);
        delete = findViewById(R.id.delete);
        back = findViewById(R.id.back_button);

        // Get product info from intent
        String productName = getIntent().getStringExtra("productName");
        String productPrice = getIntent().getStringExtra("productPrice");
        String productQuantity = getIntent().getStringExtra("productQuantity");
        String productId = getIntent().getStringExtra("productId");
        String productCategory = getIntent().getStringExtra("productCategory");
        String productBarcode = getIntent().getStringExtra("productBarcode");
        String productImage = getIntent().getStringExtra("productImage");

        // Set product info to the views
        Name.setText(productName);
        Price.setText(productPrice);

        //Quantity.setText(productQuantity);
        Category.setText(productCategory);
        Barcode.setText(productBarcode);
        Id.setText(productId);
        Units.setText(productQuantity);

        // Initialize Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //initialize firebase and get the current user
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String user1 = user.getUid();

        //dialog to show progress
        Dialog dialog = new MaterialAlertDialogBuilder(this)
                .setView(new ProgressBar(this))
                .setTitle("Finishing up")
                .setMessage("Please wait")
                .create();

            //back button
        back.setOnClickListener(v -> {
            // Go back to item management page
            startActivity(new Intent(Update_page.this, Item_Management.class));
            finish();
        });

        // Update product
        update.setOnClickListener(v -> {
            // Get updated product info
            String updatedName = Name.getText().toString();
            String updatedPrice = Price.getText().toString();
            String updatedQuantity = Quantity.getText().toString();
            String updatedId = Id.getText().toString();
            String updatedUnits = Units.getText().toString();
            //validate the fields
            if (updatedName.isEmpty()|| updatedPrice.isEmpty()|| updatedQuantity.isEmpty()) {
                Toast.makeText(Update_page.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
            else {
                //convert the quantity to integer and add the new quantity
                int newQuantity = Integer.parseInt(updatedQuantity) + Integer.parseInt(updatedUnits);
                String updatedQuantity1 = String.valueOf(newQuantity);

                // Show progress dialog
                dialog.show();
                // Update product
                db.collection(user1).document("Shop").collection("Products").document(updatedId)
                        .update("name", updatedName, "price", updatedPrice, "quantity", updatedQuantity1)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully updated!");
                                dialog.dismiss();
                                Toast.makeText(Update_page.this, updatedName + " updated successfully", Toast.LENGTH_SHORT).show();
                                //move to item management page
                                startActivity(new Intent(Update_page.this, Item_Management.class));
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error updating document", e);
                                dialog.dismiss();
                                Toast.makeText(Update_page.this, "Product update failed", Toast.LENGTH_SHORT).show();

                            }
                        });
            }
        });

        // Delete product
        delete.setOnClickListener(v -> {
            // Get product id
            String updatedId = Id.getText().toString();

            //open dialog to confirm deletion
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Delete Product")
                    .setMessage("Are you sure you want to delete this product?")
                    .setPositiveButton("Yes", (dialog1, which) -> {
                        // Delete product
                        // Show progress dialog
                        dialog.show();

                        // Delete product
                        db.collection(user1).document("Shop").collection("Products").document(updatedId)
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                        dialog.dismiss();
                                        Toast.makeText(Update_page.this, "Product deleted successfully", Toast.LENGTH_SHORT).show();
                                        // Go back to item management page
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error deleting document", e);
                                        dialog.dismiss();
                                    }
                                });
                    })
                    .setNegativeButton("No", (dialog1, which) -> {
                        dialog1.dismiss();
                    })
                    .show();
        });
    }
}