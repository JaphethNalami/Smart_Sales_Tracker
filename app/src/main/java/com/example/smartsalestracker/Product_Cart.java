package com.example.smartsalestracker;

import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class Product_Cart {
    private static Product_Cart instance;
    private static HashMap<Product, Integer> selectedProducts;

    private Product_Cart() {
        selectedProducts = new HashMap<>();
    }

    public static synchronized Product_Cart getInstance() {
        if (instance == null) {
            instance = new Product_Cart();
        }
        return instance;
    }

    public void addToCart(Product product, int count) {
        // Check if the product already exists in the cart
        if (!selectedProducts.containsKey(product)) {
            selectedProducts.put(product, count);
        } else {
            // If the product exists, update the count
            int currentCount = selectedProducts.get(product);
            selectedProducts.put(product, currentCount);
        }
    }

    public void removeFromCart(Product product) {
        selectedProducts.remove(product);
    }

    public int getProductCount(Product product) {
        return selectedProducts.getOrDefault(product, 0);
    }

    public Map<Product, Integer> getSelectedProducts() {
        return selectedProducts;
    }

    public double calculateTotalPrice() {
        double totalPrice = 0;
        for (Map.Entry<Product, Integer> entry : selectedProducts.entrySet()) {
            Product product = entry.getKey();
            totalPrice += Double.parseDouble(product.getItemTotal());
        }
        return totalPrice;
    }



}
