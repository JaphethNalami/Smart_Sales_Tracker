package com.example.smartsalestracker;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CheckoutPage extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView totalTextView,customerDetails,balance;
    TextInputEditText name, phone;
    TextInputLayout nameHolder,phoneHolder;
    ReceiptAdapter receiptAdapter;
    ArrayList<Product> cartItems = new ArrayList<>();
    RadioGroup radioGroup, radioGroup1;
    LinearLayout linearLayout2, linearLayout3;
    Button checkoutButton;
    EditText amountPaid;
    String gender;

    //firebase initialisations
    private FirebaseFirestore db ;
    private FirebaseUser user;
    private String userId;
    private FirebaseAuth mAuth;
    String paymentMethod, phoneNumber, customerName;
    Dialog dialog;
    String currentDate;
    ArrayList<Product> productArrayList;
    private FirebaseAnalytics mFirebaseAnalytics;

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
        radioGroup1 = findViewById(R.id.customerGenderRadioGroup);
        checkoutButton = findViewById(R.id.checkoutButton);
        linearLayout2 = findViewById(R.id.linear_Layout2);
        linearLayout3 = findViewById(R.id.linear_Layout3);
        amountPaid = findViewById(R.id.cashAmount);
        balance = findViewById(R.id.balance);
        productArrayList = new ArrayList<>();
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);


        //progress dialog
        dialog = new MaterialAlertDialogBuilder(this)
                .setView(new ProgressBar(this))
                .setTitle("Processing Payment")
                .setMessage("Please wait")
                .create();
        //getting current date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
         currentDate = dateFormat.format(new Date());

        //getting the payment method selected by the user
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.cash) {
                Toast.makeText(this, "Cash", Toast.LENGTH_SHORT).show();

                //make linear layout 2 and 3 and button visible
                linearLayout2.setVisibility(View.VISIBLE);
                linearLayout3.setVisibility(View.VISIBLE);
                phoneHolder.setVisibility(View.VISIBLE);
                nameHolder.setVisibility(View.VISIBLE);
                radioGroup1.setVisibility(View.VISIBLE);

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
                nameHolder.setVisibility(View.VISIBLE);
                radioGroup1.setVisibility(View.VISIBLE);

                //clear the balance text view and amount paid edit text
                balance.setText("");
                amountPaid.setText("");

            }
        });

        //get value selected by the user from the radio group1
        radioGroup1.setOnCheckedChangeListener((group, checkedId) -> {
            if (radioGroup1.getCheckedRadioButtonId() == R.id.male) {
                //toast message
                Toast.makeText(this, "Male", Toast.LENGTH_SHORT).show();
            }
            else if (radioGroup1.getCheckedRadioButtonId() == R.id.female) {
                Toast.makeText(this, "Female", Toast.LENGTH_SHORT).show();
            }
        });

        //clicking on the customer details to make the edit text visible
        customerDetails.setOnClickListener(v -> {
            nameHolder.setVisibility(View.VISIBLE);
            phoneHolder.setVisibility(View.VISIBLE);
            customerDetails.setVisibility(View.GONE);
            radioGroup1.setVisibility(View.VISIBLE);
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
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(receiptAdapter);

        // Calculate total price of all items in the cart
        double totalPrice = Product_Cart.getInstance().calculateTotalPrice();
        //convert the total price to string
        String totalPrice1 = String.valueOf(totalPrice);

        totalTextView.setText(totalPrice1);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userId = Objects.requireNonNull(user).getUid();


        //checkout button click to call the checkout method
        checkoutButton.setOnClickListener(v -> {
            //updateQuantities();
            //get phone number and name and payment method selected
             phoneNumber = Objects.requireNonNull(phone.getText()).toString();
             customerName = Objects.requireNonNull(name.getText()).toString();
             paymentMethod = "";
             gender = "";
            if (radioGroup.getCheckedRadioButtonId() == R.id.cash) {
                paymentMethod = "Cash";
            } else if (radioGroup.getCheckedRadioButtonId() == R.id.mpesa) {
                paymentMethod = "Mpesa";
            }

            if (radioGroup1.getCheckedRadioButtonId() == R.id.male) {
                gender = "Male";
            }
            else if (radioGroup1.getCheckedRadioButtonId() == R.id.female) {
                gender = "Female";
            }


            //validate the phone number and name and call the checkout method
            if (phoneNumber.isEmpty() || customerName.isEmpty() || gender.isEmpty()) {
                Toast.makeText(this, "Please fill in all the fields", Toast.LENGTH_SHORT).show();
                //make the customer details visible
                nameHolder.setVisibility(View.VISIBLE);
                phoneHolder.setVisibility(View.VISIBLE);
                radioGroup1.setVisibility(View.VISIBLE);
            } else {
                checkout();
                customerOrders();
                updateQuantities();
                soldProducts();

                // Log analytics for each item in the cart
                for (Product product : cartItems) {
                    Bundle bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Param.ITEM_ID, product.getItemId());
                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, product.getName());
                    bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "product");
                    mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                }
            }

        });
    }

    private void soldProducts() {
        for (final Product product : cartItems) {
            Map<String, Object> soldData = new HashMap<>();
            soldData.put("productName", product.getName());
            soldData.put("itemCount", product.getItemCount());
            soldData.put("paymentMethod", paymentMethod);
            soldData.put("totalPrice", product.getItemTotal());

            db.collection(userId)
                    .document("Shop")
                    .collection("Sales_Reports")
                    .document(currentDate)
                    .collection("Items")
                    .add(soldData)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            // Order saved successfully
                            Log.d(TAG, "Order saved successfully with ID: " + documentReference.getId());
                            //updateQuantities();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Failed to save order
                            Log.w(TAG, "Error saving order", e);
                            //dismiss the progress dialog
                            dialog.dismiss();
                        }
                    });


        }
    }

    private void checkout() {
        //get the order number
        String order= String.valueOf(cartItems.size());

        // Show progress dialog
        dialog.show();
        // Save customer details to the database
        Map<String, Object> customerData = new HashMap<>();
        customerData.put("customerName", customerName);
        customerData.put("phoneNumber", phoneNumber);
        customerData.put("customerPeriod", currentDate);
        customerData.put("customerOrder", order);
        customerData.put("customerGender", gender);

        db.collection(userId)
                .document("Shop")
                .collection("Customers_Details")
                .document(phoneNumber)
                .set(customerData)
                .addOnSuccessListener(aVoid -> {
                    // Customer details saved successfully
                    Log.d(TAG, "Customer details saved successfully");
                   // customerOrders();
                   // updateQuantities();
                })
                .addOnFailureListener(e -> {
                    dialog.dismiss();
                });
    }
    //method to save the order to the database
    private void customerOrders() {

        for (final Product product : cartItems) {
            Map<String, Object> orderData = new HashMap<>();
            orderData.put("productName", product.getName());
            orderData.put("itemCount", product.getItemCount());
            orderData.put("paymentMethod", paymentMethod);
            orderData.put("phoneNumber", phoneNumber);
            orderData.put("customerGender", gender);
            orderData.put("orderDate", currentDate);
            orderData.put("productCategory", product.getCategory());

            db.collection(userId)
                    .document("Shop")
                    .collection("Customers_Orders")
                    .document(phoneNumber)
                    .collection("Orders")
                    .add(orderData)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            // Order saved successfully
                            Log.d(TAG, "Order saved successfully with ID: " + documentReference.getId());
                            //updateQuantities();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Failed to save order
                            Log.w(TAG, "Error saving order", e);
                            //dismiss the progress dialog
                            dialog.dismiss();
                        }
                    });
        }
    }

    //checkout method to save the order to the database
    private void updateQuantities() {

            // Save order to the database
            for (final Product product : cartItems) {
                final String remainingQuantity = product.getRemainingQuantity();
                final String soldQuantity = product.getSoldQuantity();

                // Update quantity in Firestore
                db.collection(userId)
                        .document("Shop")
                        .collection("Products")
                        .document(product.getItemId())
                        .update("quantity", remainingQuantity, "soldQuantity", soldQuantity)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Quantity updated successfully
                                Log.d(TAG, "Quantity updated for product: " + product.getItemId());
                                //toast message
                               // Toast.makeText(CheckoutPage.this, "Order placed successfully", Toast.LENGTH_SHORT).show();
                                //dismiss the progress dialog
                                dialog.dismiss();
                                // Move to the home page
                                startActivity(new Intent(CheckoutPage.this, SaleCompletion.class));

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Failed to update quantity
                                Log.w(TAG, "Error updating quantity for product: " + product.getItemId(), e);

                                //dismiss the progress dialog
                                dialog.dismiss();
                            }
                        });
            }

        Product_Cart.getInstance().getSelectedProducts().clear();
        receiptAdapter.notifyDataSetChanged();

    }
}