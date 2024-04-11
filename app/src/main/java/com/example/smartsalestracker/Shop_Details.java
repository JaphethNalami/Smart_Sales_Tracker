package com.example.smartsalestracker;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
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

import java.util.HashMap;
import java.util.Map;

public class Shop_Details extends AppCompatActivity {

    TextInputEditText shop_name, shop_address;
    Button submit;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_shop_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //binding views
        shop_name = findViewById(R.id.shop_name);
        shop_address = findViewById(R.id.shop_address);
        submit = findViewById(R.id.submit);

        //initialize firebase authentication
        mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        FirebaseUser user1 = mAuth.getCurrentUser();
        String user2 = user1.getUid();


        //progress dialog
        Dialog dialog = new MaterialAlertDialogBuilder(this)
                .setView(new ProgressBar(this))
                .setTitle("Finalizing Setup")
                .setMessage("Please wait...")
                .create();

        Spinner spinner = (Spinner) findViewById(R.id.shop_type);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.shop_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        //submit button click listener
        submit.setOnClickListener(v -> {
            dialog.show();
            // Get shop details
            String shopName = shop_name.getText().toString().trim();
            String shopAddress = shop_address.getText().toString().trim();
            String shopType = spinner.getSelectedItem().toString();

            // Check if shop details are empty
            if(shopName.isEmpty()){
                dialog.dismiss();
                shop_name.setError("Shop Name is required");
                shop_name.requestFocus();
                return;
            }
            else if(shopAddress.isEmpty()){
                dialog.dismiss();
                shop_address.setError("Shop Address is required");
                shop_address.requestFocus();
                return;
            }
            else{
                // Save shop details to firestore
                dialog.show();
                Map<String, Object> shop = new HashMap<>();
                shop.put("shop_name", shopName);
                shop.put("shop_address", shopAddress);
                shop.put("shop_type", shopType);

                db.collection(user2).document("Shop_Details")
                        .set(shop)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {

                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully written!");
                                dialog.dismiss();
                                //toast message
                                Toast.makeText(Shop_Details.this, "Setup complete", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Shop_Details.this, Item_Add.class));
                            }

                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dialog.dismiss();
                                Log.w(TAG, "Error writing document", e);
                            }
                        });
            }

        });


    }
}