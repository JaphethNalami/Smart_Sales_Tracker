package com.example.smartsalestracker;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Item_Add extends AppCompatActivity {

    ShapeableImageView shapeableImageView;
    TextInputEditText itemName, itemPrice, itemQuantity;
    TextView itemBarcode,addCategory;
    Button save, discard, scanBarcode;
    Spinner category;

    Uri imageUri;
    String imageUrl;

    private FirebaseAuth mAuth;

    Dialog dialog;

   @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        assert data != null;
        imageUri = data.getData();
        shapeableImageView.setImageURI(imageUri);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_item_add);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        shapeableImageView = findViewById(R.id.ImageView);
        itemName = findViewById(R.id.item_name);
        itemPrice = findViewById(R.id.price);
        itemQuantity = findViewById(R.id.quantity);
        itemBarcode = findViewById(R.id.barcode);
        save = findViewById(R.id.add);
        discard = findViewById(R.id.discard);
        scanBarcode = findViewById(R.id.scan);
        addCategory = findViewById(R.id.add_category);
        category = findViewById(R.id.category);


        //dialog to show progress
        dialog = new MaterialAlertDialogBuilder(this)
                .setView(new ProgressBar(this))
                .setTitle("Saving item")
                .setMessage("Please wait")
                .create();




        shapeableImageView.setOnClickListener(
                v -> ImagePicker.with(this)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start()
        );

        mAuth = FirebaseAuth.getInstance();
        Categories();

        scanBarcode.setOnClickListener(v -> {
            //scan barcode
            scanCode();
        });

        save.setOnClickListener(view -> {
            //get user input
            String name = itemName.getText().toString().trim();
            String price = itemPrice.getText().toString().trim();
            String quantity = itemQuantity.getText().toString().trim();
            String bar = itemBarcode.getText().toString();

            //save details to firestore
            if (name.isEmpty() || price.isEmpty() || quantity.isEmpty() || bar.isEmpty() || imageUri == null) {
                Toast.makeText(Item_Add.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }else
             {
               uploadProfilePicture(imageUri);
            }

        });

        discard.setOnClickListener(v -> {
            //move to item add activity
            startActivity(new Intent(Item_Add.this, Item_Add.class));

        });

        addCategory.setOnClickListener(v -> {
            //add category
            showAddCategoryDialog();
        });


    }

    // Get categories from Fire_store
    private void Categories() {
        FirebaseUser user1 = mAuth.getCurrentUser();
        String user2 = user1.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(user2).document("Shop").collection("Categories").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<String> categories = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String categoryName = document.getString("name");
                        categories.add(categoryName);
                    }

                    // For example, if you have a spinner:
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(Item_Add.this, android.R.layout.simple_spinner_item, categories);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    category.setAdapter(adapter);

                })
                .addOnFailureListener(e -> {
                    Log.e("Error", e.getMessage());
                });
    }

    // Add a new category to Firestore
    private void showAddCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Item_Add.this);
        builder.setTitle("Add Category");
        final EditText input = new EditText(Item_Add.this);
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String categoryName = input.getText().toString();
            if (!categoryName.isEmpty()) {

                mAuth = FirebaseAuth.getInstance();
                FirebaseUser user1 = mAuth.getCurrentUser();
                String user2 = user1.getUid();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                // Add the new category to Firestore
                Map<String, Object> category = new HashMap<>();
                category.put("name", categoryName);
                db.collection(user2).document("Shop").collection("Categories")
                        .add(category)
                        .addOnSuccessListener(aVoid -> {
                            // Category added successfully
                            Log.d("TAG", "Category added: " + categoryName);
                            // Refresh the categories list
                            Categories();
                        })
                        .addOnFailureListener(e -> {
                            // Handle failure
                            Log.e("Error", "Error adding category", e);
                        });
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }
    // launching camera activity for scanning
    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result != null) {
            // handle result
            AlertDialog.Builder builder = new AlertDialog.Builder(Item_Add.this);

            //set the code to textview barcode
            itemBarcode.setText(result.getContents());
        }
    });


    // Upload the profile picture to Firebase Storage
    private void uploadProfilePicture(Uri imageUri) {

        dialog.show();
        // Initialize Firebase Storage
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageReference.child("shop/" + UUID.randomUUID().toString());

        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Image uploaded successfully, get the download URL
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Update imageUrl variable with the download URL
                        imageUrl = uri.toString();
                        // After getting the download URL, proceed to save user details in Firestore
                        saveUserDetails();
                    }).addOnFailureListener(e -> {
                        // Handle the failure to get the download URL
                        Toast.makeText(Item_Add.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                        dialog.dismiss();
                    });
                })
                .addOnFailureListener(e -> {
                    // Handle the failure to upload the image
                    Toast.makeText(Item_Add.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                    dialog.dismiss();
                });
    }


    // Save item details to Firestore
    public void saveUserDetails(){
        //get selected category
        String selectedCategory = category.getSelectedItem().toString();

        //save details to firestore
        String name = itemName.getText().toString().trim();
        String price = itemPrice.getText().toString().trim();
        String quantity = itemQuantity.getText().toString().trim();
        String bar = itemBarcode.getText().toString();

        FirebaseUser user1 = mAuth.getCurrentUser();
        String user2 = user1.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

            Map<String, Object> item = new HashMap<>();
            item.put("name", name);
            item.put("price", price);
            item.put("quantity", quantity);
            item.put("barcode", bar);
            item.put("category", selectedCategory);
            item.put("image", imageUrl);
            db.collection(user2).document("Shop").collection("Products").add(item)
                    .addOnSuccessListener(documentReference -> {
                        String itemId = documentReference.getId(); // Get the generated unique ID
                        item.put("itemId", itemId); // Store the ID in the item object
                        db.collection(user2).document("Shop").collection("Products").document(itemId)
                                .set(item) // Update the item with the generated ID
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(Item_Add.this, "Item added successfully", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                    startActivity(new Intent(Item_Add.this, Item_Add.class));
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(Item_Add.this, "Error updating item", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(Item_Add.this, "Error adding item", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    });

    }

}