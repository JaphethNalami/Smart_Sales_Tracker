package com.example.smartsalestracker;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class Sign_Up extends AppCompatActivity {

    TextInputEditText name, phone, email, password, confirm_password;
    Button sign_up;
    TextView sign_in;

    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //binding views
        name = findViewById(R.id.name_edit_text);
        phone = findViewById(R.id.phone_edit_text);
        email = findViewById(R.id.email_edit_text);
        password = findViewById(R.id.password_edit_text);
        confirm_password = findViewById(R.id.confirm_password_edit_text);
        sign_in = findViewById(R.id.sign_in_link);
        sign_up = findViewById(R.id.btn_sign_in);

        //initialize firebase authentication
        mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //progress dialog
        Dialog dialog = new MaterialAlertDialogBuilder(this)
                .setView(new ProgressBar(this))
                .setTitle("Signing Up")
                .setMessage("Please wait")
                .create();

        //sign in button click listener
        sign_up.setOnClickListener(v -> {
            //get input values from views
            String nameStr = name.getText().toString().trim();
            String phoneStr = phone.getText().toString().trim();
            String emailStr = email.getText().toString().trim();
            String passwordStr = password.getText().toString().trim();
            String confirm_passwordStr = confirm_password.getText().toString().trim();

            //validate input values
            if (emailStr.isEmpty() || passwordStr.isEmpty() || nameStr.isEmpty() || phoneStr.isEmpty() || confirm_passwordStr.isEmpty()){
                dialog.dismiss();
                Toast.makeText(Sign_Up.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
            else if (!passwordStr.equals(confirm_passwordStr)){
                dialog.dismiss();
                Toast.makeText(Sign_Up.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            }
            else {
                dialog.show();
                //sign in with email and password
                mAuth.createUserWithEmailAndPassword(emailStr, passwordStr)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user1 = mAuth.getCurrentUser();
                                String user2 = user1.getUid();

                                // Add user details to firestore
                                db.collection(user2).document("User_Details")
                                        .set(new User(nameStr, phoneStr, emailStr, user2))
                                        .addOnSuccessListener(aVoid -> {
                                            // Sign in success, update UI with the signed-in user's information
                                            Toast.makeText(Sign_Up.this, "Sign up successful.",
                                                    Toast.LENGTH_SHORT).show();
                                            //navigate to user details activity
                                            startActivity(new Intent(Sign_Up.this, Shop_Details.class));
                                        })
                                        .addOnFailureListener(e -> {
                                            dialog.dismiss();
                                            Toast.makeText(Sign_Up.this, "Sign up failed.",
                                                    Toast.LENGTH_SHORT).show();
                                        });

                            } else {
                                dialog.dismiss();
                                // If sign in fails, display a message to the user.
                                Toast.makeText(Sign_Up.this, "Sign up failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });

            }

        });

        //sign up link click listener
        sign_in.setOnClickListener(v -> {
            //navigate to sign up activity
            startActivity(new Intent(Sign_Up.this, Sign_In.class));
        });


    }
}