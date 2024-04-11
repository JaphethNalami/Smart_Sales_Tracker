package com.example.smartsalestracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

public class Sign_In extends AppCompatActivity {

    TextInputEditText email, password;
    Button sign_in;
    TextView sign_up, forgot_password;
    CheckBox remember_me;

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

        //views binding
        email = findViewById(R.id.email_edit_text);
        password = findViewById(R.id.password_edit_text);
        sign_in = findViewById(R.id.btn_sign_in);
        sign_up = findViewById(R.id.sign_up_link);
        forgot_password = findViewById(R.id.forgot_password);
        remember_me = findViewById(R.id.remember_me);

        //sign in button click listener
        sign_in.setOnClickListener(v -> {
            //get input values from views

        });

        //sign up link click listener
        sign_up.setOnClickListener(v -> {

            //navigate to sign up activity
            startActivity(new Intent(Sign_In.this, Sign_Up.class));
        });

        //forgot password click listener
        forgot_password.setOnClickListener(v -> {

        });

    }
}