package com.example.smartsalestracker;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Map;

public class CheckoutPage extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView totalTextView,customerDetails,balance;
    TextInputEditText name, phone;
    TextInputLayout nameHolder,phoneHolder;
    ReceiptAdapter receiptAdapter;
    ArrayList<Product> cartItems = new ArrayList<>();
    RadioGroup radioGroup;
    LinearLayout linearLayout2, linearLayout3;
    Button checkoutButton;
    EditText amountPaid;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_page);

        recyclerView = findViewById(R.id.recyclerView);
        totalTextView = findViewById(R.id.totalPrice);
        customerDetails = findViewById(R.id.customerDetails);
        name = findViewById(R.id.name_edit_text);
        phone = findViewById(R.id.phone_edit_text);
        nameHolder = findViewById(R.id.textField_name);
        phoneHolder = findViewById(R.id.textField_phone);
        radioGroup = findViewById(R.id.paymentMethod);
        checkoutButton = findViewById(R.id.checkoutButton);
        linearLayout2 = findViewById(R.id.linear_Layout2);
        linearLayout3 = findViewById(R.id.linear_Layout3);
        amountPaid = findViewById(R.id.cashAmount);
        balance = findViewById(R.id.balance);

        //getting the payment method selected by the user
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.cash) {
                Toast.makeText(this, "Cash", Toast.LENGTH_SHORT).show();

                //make linear layout 2 and 3 and button visible
                linearLayout2.setVisibility(View.VISIBLE);
                linearLayout3.setVisibility(View.VISIBLE);
               // checkoutButton.setVisibility(View.VISIBLE);

                //automatically calculate the balance when the user types the amount paid
                amountPaid.setOnKeyListener((v, keyCode, event) -> {
                    //if its empty set do not do the calculation
                    if (amountPaid.getText().toString().isEmpty()) {
                        balance.setText("");
                    }
                    else {
                        double total = Double.parseDouble(totalTextView.getText().toString());
                        double paid = Double.parseDouble(amountPaid.getText().toString());
                        double balance1 = paid - total;
                        balance.setText(String.valueOf(balance1));

                        if (balance1 < 0) {
                            balance.setTextColor(getResources().getColor(R.color.red));
                            checkoutButton.setVisibility(View.GONE);
                        } else {
                            balance.setTextColor(getResources().getColor(R.color.green));
                            checkoutButton.setVisibility(View.VISIBLE);

                        }
                    }
                    return false;
                });


            } else if (checkedId == R.id.mpesa) {
                Toast.makeText(this, "Mpesa", Toast.LENGTH_SHORT).show();

                //make linear layout 2 and 3 invisible
                linearLayout2.setVisibility(View.GONE);
                linearLayout3.setVisibility(View.GONE);
                checkoutButton.setVisibility(View.VISIBLE);

                //make phone number edit text visible
                phoneHolder.setVisibility(View.VISIBLE);

                //clear the balance text view and amount paid edit text
                balance.setText("");
                amountPaid.setText("");

            }
        });

        //clicking on the customer details to make the edit text visible
        customerDetails.setOnClickListener(v -> {
            nameHolder.setVisibility(View.VISIBLE);
            phoneHolder.setVisibility(View.VISIBLE);
            customerDetails.setVisibility(View.GONE);
        });


        // Fetch item details from Product_Cart class
        Map<Product, Integer> selectedProductsMap = Product_Cart.getInstance().getSelectedProducts();
        for (Map.Entry<Product, Integer> entry : selectedProductsMap.entrySet()) {
            Product product = entry.getKey();
            int count = entry.getValue();
            for (int i = 0; i < count; i++) {
                cartItems.add(product);
            }
        }

        // Initialize CartAdapter with fetched item details
        receiptAdapter = new ReceiptAdapter(cartItems, this);

        // Set layout manager and adapter to the RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(receiptAdapter);

        // Calculate total price of all items in the cart
        double totalPrice = Product_Cart.getInstance().calculateTotalPrice();
        //convert the total price to string
        String totalPrice1 = String.valueOf(totalPrice);

        totalTextView.setText(totalPrice1);


    }
}