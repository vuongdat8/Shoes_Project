package com.example.shoes_project.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "cart_items")
public class CartItem {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int userId;
    private int productId;
    private String productName;
    private String imageUrl;
    private double price;
    private int quantity;
    private String selectedColor;
    private String selectedSize;
    private boolean isSelected;

    // Constructors
    public CartItem() {}
    @Ignore
    public CartItem(int userId, int productId, String productName, String imageUrl,
                    double price, int quantity, String selectedColor, String selectedSize) {
        this.userId = userId;
        this.productId = productId;
        this.productName = productName;
        this.imageUrl = imageUrl;
        this.price = price;
        this.quantity = quantity;
        this.selectedColor = selectedColor;
        this.selectedSize = selectedSize;
        this.isSelected = false;
    }

    public CartItem(int userId, int productId, String selectedColor, String selectedSize) {
        this.userId = userId;
        this.productId = productId;
        this.productName = "";
        this.imageUrl = "";
        this.price = 0.0;
        this.quantity = 1;
        this.selectedColor = selectedColor;
        this.selectedSize = selectedSize;
        this.isSelected = false;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getSelectedColor() { return selectedColor; }
    public void setSelectedColor(String selectedColor) { this.selectedColor = selectedColor; }

    public String getSelectedSize() { return selectedSize; }
    public void setSelectedSize(String selectedSize) { this.selectedSize = selectedSize; }

    public boolean isSelected() { return isSelected; }
    public void setSelected(boolean selected) { isSelected = selected; }
}

