package com.example.smartsalestracker;

public class Product {
    String name, image, price, quantity,barcode, itemId, category;

    public Product() {}

    public Product(String name, String image, String price, String quantity, String barcode, String itemId, String category) {
        this.name = name;
        this.image = image;
        this.price = price;
        this.quantity = quantity;
        this.barcode = barcode;
        this.itemId = itemId;
        this.category = category;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String id) {
        this.itemId = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

}
