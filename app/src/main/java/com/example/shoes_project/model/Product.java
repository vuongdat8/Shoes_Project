package com.example.shoes_project.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "product",
        foreignKeys = {
                @ForeignKey(entity = Brand.class,
                        parentColumns = "id",
                        childColumns = "brandId",
                        onDelete = ForeignKey.RESTRICT),
                @ForeignKey(entity = Category.class,
                        parentColumns = "id",
                        childColumns = "categoryId",
                        onDelete = ForeignKey.RESTRICT)
        },
        indices = {@Index(value = {"brandId"}), @Index(value = {"categoryId"})})
public class Product {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String productName;
    private double price;
    private int quantity;
    private double sellingPrice;
    private String imageUrl;
    private double size;
    private String description;
    private String color;

    private int brandId;
    private int categoryId;

    // Constructor - ĐÃ CẬP NHẬT KIỂU DỮ LIỆU sellingPrice
    public Product(String productName, double price, int quantity, double sellingPrice, // Đã thay đổi
                   String imageUrl, double size, String description, String color,
                   int brandId, int categoryId) {
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.sellingPrice = sellingPrice;
        this.imageUrl = imageUrl;
        this.size = size;
        this.description = description;
        this.color = color;
        this.brandId = brandId;
        this.categoryId = categoryId;
    }

    public Product() {
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getSellingPrice() { return sellingPrice; }
    public void setSellingPrice(double sellingPrice) { this.sellingPrice = sellingPrice; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public double getSize() { return size; }
    public void setSize(double size) { this.size = size; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public int getBrandId() { return brandId; }
    public void setBrandId(int brandId) { this.brandId = brandId; }
    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
}