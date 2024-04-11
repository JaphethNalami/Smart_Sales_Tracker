package com.example.smartsalestracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class Password_Reset extends AppCompatActivity {

    Button reset, back;
    TextInputEditText email;

    //firebase auth object
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_password_reset);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        reset = findViewById(R.id.reset);
        back = findViewById(R.id.back);
        email = findViewById(R.id.email);

        //firebase auth object
        mAuth = FirebaseAuth.getInstance();

        reset.setOnClickListener(v -> {
            // Get email
            String emailStr = email.getText().toString().trim();

            // Check if email is empty
            if(emailStr.isEmpty()){
                email.setError("Email is required");
                email.requestFocus();
                return;
            }
            else{
                // Send password reset email and on success go back to login
                mAuth.sendPasswordResetEmail(emailStr).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        // display a message to the user that an email has been sent to their email address
                        Toast.makeText(Password_Reset.this, "Reset Password link has been sent to your registered Email", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else{
                        // display a message to the user that an email has been sent to their email address
                        Toast.makeText(Password_Reset.this, "Failed to send reset password link", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        back.setOnClickListener(v -> {
            // Move to sign in activity
            startActivity(new Intent(Password_Reset.this, Sign_In.class));

        });

    }
}