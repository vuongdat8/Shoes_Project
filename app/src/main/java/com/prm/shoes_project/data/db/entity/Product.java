package com.prm.shoes_project.data.db.entity;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import org.jetbrains.annotations.NotNull;
import java.util.UUID;

public class Product {
    @PrimaryKey
    @NotNull
    @ColumnInfo(name = "productId")
    private UUID productId;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "importPrice")
    private double importPrice;
    @ColumnInfo(name = "sellingPrice")
    private double sellingPrice;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "stock")
    private int stock;

    @ColumnInfo(name = "sold")
    private int sold;

    @ColumnInfo(name = "categoryId")
    private UUID categoryId;

    @ColumnInfo(name = "brandId")
    private UUID brandId;

    @ColumnInfo(name = "image")
    private String image;

    // Constructor
    public Product() {
        this.productId = UUID.randomUUID();
    }

    // Getters and Setters
    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getImportPrice() {
        return importPrice;
    }

    public void setImportPrice(double importPrice) {
        this.importPrice = importPrice;
    }

    public double getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public int getSold() {
        return sold;
    }

    public void setSold(int sold) {
        this.sold = sold;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }

    public UUID getBrandId() {
        return brandId;
    }

    public void setBrandId(UUID brandId) {
        this.brandId = brandId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
