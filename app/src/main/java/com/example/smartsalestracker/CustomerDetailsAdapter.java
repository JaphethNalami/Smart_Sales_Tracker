package com.example.smartsalestracker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class CustomerDetailsAdapter extends RecyclerView.Adapter<CustomerDetailsAdapter.CustomerViewHolder> {

    // Context and ArrayList to hold the context and the customer data
    private final Context context;
    private final ArrayList<Customer> customerArrayList;
    private final LayoutInflater inflater;

    // Constructor for the adapter
    public CustomerDetailsAdapter(Context context, ArrayList<Customer> customerArrayList) {
        this.context = context;
        this.customerArrayList = customerArrayList;
        inflater = LayoutInflater.from(context);
    }

    // Method to create ViewHolder
    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.customer_card, parent, false);
        return new CustomerViewHolder(v);
    }

    // Method to bind data to ViewHolder
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {
        // Get the customer data for the current position
        Customer customer = customerArrayList.get(position);
        holder.customerName.setText(customer.getCustomerName());
        holder.customerPhone.setText(customer.getphoneNumber());

        // Get the initial of the customer's name
        holder.nameInitial.setText(String.valueOf(customer.getCustomerName().charAt(0)));

        // Get value of orders and convert to string
        String orders = String.valueOf(customer.getCustomerOrder());
        // Display the orders
        holder.orders.setText(orders + " Item(s)");

        // Set click listener for the item view
        holder.itemView.setOnClickListener(v -> {
            // Open customer orders page and pass customer info
            Intent intent = new Intent(context, CustomerOrdersDisplay.class);
            intent.putExtra("customerName", customer.getCustomerName());
            intent.putExtra("customerPhone", customer.getphoneNumber());
            // Add more data as needed
            context.startActivity(intent);
        });

        // Get stored date
        String date = customer.getCustomerPeriod();
        // Convert the string date to local date
        LocalDate storedDate = LocalDate.parse(date);

        // Get current date
        LocalDate currentDate = LocalDate.now();

        // Sample dates to compare
        LocalDate date2 = LocalDate.of(currentDate.getYear(), currentDate.getMonth(), currentDate.getDayOfMonth());
        LocalDate date1 = LocalDate.of(storedDate.getYear(), storedDate.getMonth(), storedDate.getDayOfMonth());

        // Calculate the difference in days
        long differenceInDays = ChronoUnit.DAYS.between(date1, date2);

        // Check if the difference is zero and display the period as Today
        if(differenceInDays == 0){
            holder.period.setText("Today");
        }
        // Check if the difference is one and display the period as Yesterday
        else if (differenceInDays == 1){
            holder.period.setText("Yesterday");
        }
        // Check if the difference is more than 30 days
        else if(differenceInDays > 30){
            // Convert the difference to months and display it
            String value = String.valueOf(differenceInDays / 30);
            holder.period.setText(value + " months ago");
        }
        else {
            // Convert the difference to string and display it in days
            String value = String.valueOf(differenceInDays);
            holder.period.setText(value + " days ago");
        }
    }

    // Method to get the item count
    @Override
    public int getItemCount() {
        return customerArrayList.size();
    }

    // ViewHolder class to hold the view elements
    public static class CustomerViewHolder extends RecyclerView.ViewHolder {

        TextView customerName, customerPhone, orders, period, nameInitial;

        // Constructor for ViewHolder
        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            customerName = itemView.findViewById(R.id.name);
            customerPhone = itemView.findViewById(R.id.phone);
            orders = itemView.findViewById(R.id.orders);
            period = itemView.findViewById(R.id.period);
            nameInitial = itemView.findViewById(R.id.nameInitial);
        }
    }
}
