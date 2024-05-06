package com.example.smartsalestracker;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SaleCompletion extends AppCompatActivity {

    TextView saleCompletionText;
    Button saleCompletionButton;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sale_completion);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        saleCompletionText = findViewById(R.id.hyperlink);
        saleCompletionButton = findViewById(R.id.new_sale_button);

        //toast message
        Toast.makeText(this, "Sale Completed", Toast.LENGTH_SHORT).show();

        saleCompletionText.setOnClickListener(v -> {
            // move to sales analytics
            startActivity(new Intent(SaleCompletion.this, SalesAnalysis.class));
        });

        saleCompletionButton.setOnClickListener(v -> {
            // Open the NewSale activity
            startActivity(new Intent(SaleCompletion.this, Home_Page.class));
        });
    }
}