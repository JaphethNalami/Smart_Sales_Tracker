package com.example.smartsalestracker;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Sign_In extends AppCompatActivity {

    TextInputEditText email, password;
    Button sign_in;
    TextView sign_up, forgot_password;
    CheckBox remember_me;


    //firebase authentication declaration
    private FirebaseAuth mAuth;

    private boolean doubleBackToExitPressedOnce = false;
    private static final int BACK_PRESS_DELAY = 2000; // 2 second

    // Override the onBackPressed method

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            finishAffinity();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce=false, BACK_PRESS_DELAY);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            startActivity(new Intent(Sign_In.this, Home_Page.class));

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FirebaseApp.initializeApp(this);

        //views binding
        email = findViewById(R.id.email_edit_text);
        password = findViewById(R.id.password_edit_text);
        sign_in = findViewById(R.id.btn_sign_in);
        sign_up = findViewById(R.id.sign_up_link);
        forgot_password = findViewById(R.id.forgot_password);
        remember_me = findViewById(R.id.remember_me);

        //initialize firebase authentication
        mAuth = FirebaseAuth.getInstance();

        //progress dialog
        Dialog dialog = new MaterialAlertDialogBuilder(this)
                .setView(new ProgressBar(this))
                .setTitle("Signing in")
                .setMessage("Please wait")
                .create();



        //sign in button click listener
        sign_in.setOnClickListener(v -> {
            //show progress dialog
            dialog.show();

            //get input values from views
            String email1 = email.getText().toString().trim();
            String password1 = password.getText().toString().trim();

            // validate inputs
            if (email1.isEmpty() || password1.isEmpty()) {
                dialog.dismiss();
                Toast.makeText(Sign_In.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
            else {
                //sign in with email and password
                mAuth.signInWithEmailAndPassword(email1, password1)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                dialog.dismiss();
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();
                                startActivity(new Intent(Sign_In.this, Home_Page.class));
                            } else {
                                dialog.dismiss();
                                // If sign in fails, display a message to the user.
                                Toast.makeText(Sign_In.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            }

        });

        //sign up link click listener
        sign_up.setOnClickListener(v -> {

            //navigate to sign up activity
            startActivity(new Intent(Sign_In.this, Sign_Up.class));
        });

        //forgot password click listener
        forgot_password.setOnClickListener(v -> {

            //navigate to password reset activity
            startActivity(new Intent(Sign_In.this, Password_Reset.class));
        });

    }
}