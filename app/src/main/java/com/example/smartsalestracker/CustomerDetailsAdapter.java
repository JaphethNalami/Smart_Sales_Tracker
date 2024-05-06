package com.example.smartsalestracker;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomerDetailsAdapter extends RecyclerView.Adapter<CustomerDetailsAdapter.CustomerViewHolder> {

    private Context context;
    private ArrayList<Customer> customerArrayList;
    private LayoutInflater inflater;

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

    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {
        Customer customer = customerArrayList.get(position);
        holder.customerName.setText(customer.getCustomerName());
        holder.customerPhone.setText(customer.getphoneNumber());
        holder.orders.setText(String.valueOf(customer.getCustomerOrder()));
        holder.period.setText(customer.getCustomerPeriod());
        holder.nameInitial.setText(String.valueOf(customer.getCustomerName().charAt(0)));



        // Set click listener
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open customer orders page and pass customer info
                Intent intent = new Intent(context, CustomerOrdersDisplay.class);
                intent.putExtra("customerName", customer.getCustomerName());
                intent.putExtra("customerPhone", customer.getphoneNumber());
                // Add more data as needed
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return customerArrayList.size();
    }

    public class CustomerViewHolder extends RecyclerView.ViewHolder {

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

