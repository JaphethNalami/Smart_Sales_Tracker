package com.example.smartsalestracker;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class User_Profile_Update extends AppCompatActivity {

    TextView user_name, user_email, user_phone,user_edit,dismiss_edit;
    TextInputEditText user_name_edit, user_phone_edit;
    Button update_profile;
    ImageButton back;
    LinearLayout user_profile_layout;
    String name, phone,userId;
    FirebaseFirestore db;
    private FirebaseAuth mAuth;
    FirebaseUser user;
    Dialog dialog,dialog2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_update);

        //initialize views
        user_name = findViewById(R.id.name);
        user_email = findViewById(R.id.email);
        user_phone = findViewById(R.id.phone);
        user_edit = findViewById(R.id.edit_profile);
        user_name_edit = findViewById(R.id.name_edit);
        user_phone_edit = findViewById(R.id.phone_edit);
        update_profile = findViewById(R.id.update);
        user_profile_layout = findViewById(R.id.edit_profile_layout);
        dismiss_edit = findViewById(R.id.dismiss_edit);
        back = findViewById(R.id.back_button);

        //firebase initialization
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        userId = user.getUid();

        dialog = new MaterialAlertDialogBuilder(this)
                .setView(new ProgressBar(this))
                .setTitle("Fetching Data")
                .setMessage("Please wait")
                .create();

        dialog2 = new MaterialAlertDialogBuilder(this)
                .setView(new ProgressBar(this))
                .setTitle("Updating Profile")
                .setMessage("Please wait")
                .create();

        //back button
        back.setOnClickListener(v -> finish());

        //edit user profile click
        user_edit.setOnClickListener(v -> {
            user_profile_layout.setVisibility(LinearLayout.VISIBLE);
            user_edit.setVisibility(TextView.GONE);
        });

        dismiss_edit.setOnClickListener(v -> {
            user_profile_layout.setVisibility(LinearLayout.GONE);
            user_edit.setVisibility(TextView.VISIBLE);
        });

        //update user profile click
        update_profile.setOnClickListener(v -> {
            //get the values from the edit text
            name = Objects.requireNonNull(user_name_edit.getText()).toString();
            phone = Objects.requireNonNull(user_phone_edit.getText()).toString();

            //validate the input
            if (name.isEmpty()) {
                user_name_edit.setError("Name is required");
                user_name_edit.requestFocus();
            }
           else if (phone.isEmpty()) {
                user_phone_edit.setError("Phone is required");
                user_phone_edit.requestFocus();
            }
            else if (phone.length()<10){
                user_phone_edit.setError("Invalid Phone Number");
                user_phone_edit.requestFocus();
            }
            else {
                //open dialog to confirm deletion
                new MaterialAlertDialogBuilder(this)
                        .setTitle("Update Profile")
                        .setMessage("Proceed to Update?")
                        .setPositiveButton("Yes", (dialog1, which) -> {
                            //call method to update profile
                            updateProfile();
                            dialog2.show();
                        })
                        .setNegativeButton("No", (dialog1, which) -> {
                            dialog1.dismiss();
                            dialog2.dismiss();
                        })
                        .show();
            }
        });

        //call method to get user details
        getUserDetails();
        dialog.show();
    }

    private void getUserDetails() {

        db.collection(userId).document("User_Details").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                dialog.dismiss();
                user_name.setText(Objects.requireNonNull(task.getResult()).getString("name"));
                user_email.setText(Objects.requireNonNull(task.getResult()).getString("email"));
                user_phone.setText(Objects.requireNonNull(task.getResult()).getString("phone"));
            }
            else {
                dialog.dismiss();
            }
        });
    }

    private void updateProfile() {
        db.collection(userId).document("User_Details")
                .update("name", name, "phone", phone)
                .addOnSuccessListener(aVoid -> {
                    dialog2.dismiss();
                    Toast.makeText(User_Profile_Update.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    //refresh the page
                    finish();
                })
                .addOnFailureListener(e -> {
                    //toast message
                    Toast.makeText(User_Profile_Update.this, "Profile update failed", Toast.LENGTH_SHORT).show();
                    dialog2.dismiss();
                });
    }
}