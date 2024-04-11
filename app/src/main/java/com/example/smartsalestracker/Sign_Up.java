package com.example.smartsalestracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

public class Sign_Up extends AppCompatActivity {

    TextInputEditText name, phone, email, password, confirm_password;
    Button sign_up;
    TextView sign_in;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //binding views
        name = findViewById(R.id.name_edit_text);
        phone = findViewById(R.id.phone_edit_text);
        email = findViewById(R.id.email_edit_text);
        password = findViewById(R.id.password_edit_text);
        confirm_password = findViewById(R.id.confirm_password_edit_text);
        sign_in = findViewById(R.id.sign_in_link);
        sign_up = findViewById(R.id.btn_sign_in);

        //sign in button click listener
        sign_up.setOnClickListener(v -> {
            //get input values from views

        });

        //sign up link click listener
        sign_in.setOnClickListener(v -> {
            //navigate to sign up activity
            startActivity(new Intent(Sign_Up.this, Sign_In.class));
        });


    }
}