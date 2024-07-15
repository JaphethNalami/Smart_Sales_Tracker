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

    private final Context context;
    private final ArrayList<Customer> customerArrayList;
    private final LayoutInflater inflater;

    public CustomerDetailsAdapter(Context context, ArrayList<Customer> customerArrayList) {
        this.context = context;
        this.customerArrayList = customerArrayList;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.customer_card, parent, false);
        return new CustomerViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {
        Customer customer = customerArrayList.get(position);
        holder.customerName.setText(customer.getCustomerName());
        holder.customerPhone.setText(customer.getphoneNumber());
       // holder.orders.setText(String.valueOf(customer.getCustomerOrder()));
        holder.nameInitial.setText(String.valueOf(customer.getCustomerName().charAt(0)));
        //get value of orders and convert to string
        String orders = String.valueOf(customer.getCustomerOrder());
        //display the orders
        holder.orders.setText(orders + " Item(s)");

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            // Open customer orders page and pass customer info
            Intent intent = new Intent(context, CustomerOrdersDisplay.class);
            intent.putExtra("customerName", customer.getCustomerName());
            intent.putExtra("customerPhone", customer.getphoneNumber());
            // Add more data as needed
            context.startActivity(intent);
        });

        //get stored date
        String date = customer.getCustomerPeriod();
        //convert the string date to local date
        LocalDate storedDate = LocalDate.parse(date);

        //get current date
        LocalDate currentDate = LocalDate.now();

        // Sample dates to compare
        LocalDate date2 = LocalDate.of(currentDate.getYear(), currentDate.getMonth(), currentDate.getDayOfMonth());
        LocalDate date1 = LocalDate.of(storedDate.getYear(), storedDate.getMonth(), storedDate.getDayOfMonth());

        // Calculate the difference in days
        long differenceInDays = ChronoUnit.DAYS.between(date1, date2);

        //check if the difference is zero and display the period as Today
        if(differenceInDays == 0){
            holder.period.setText("Today");
        }
        else if (differenceInDays == 1){
            holder.period.setText("Yesterday");
        }
        else if(differenceInDays >30){
            //convert the difference to string
            String value = String.valueOf(differenceInDays/30);
            //display the difference in days
            holder.period.setText(value + " months ago");
        }
        else {
            //convert the difference to string
            String value = String.valueOf(differenceInDays);
            //display the difference in days
            holder.period.setText(value + " days ago");
        }

    }

    @Override
    public int getItemCount() {
        return customerArrayList.size();
    }

    public static class CustomerViewHolder extends RecyclerView.ViewHolder {

        TextView customerName, customerPhone, orders, period,nameInitial;

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

