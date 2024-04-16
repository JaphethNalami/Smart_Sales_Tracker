package com.example.smartsalestracker;

import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
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
        selectedProducts.put(product, count);
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
}
