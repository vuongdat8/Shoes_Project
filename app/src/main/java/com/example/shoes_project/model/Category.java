package com.example.shoes_project.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.Index; // <-- THÊM DÒNG NÀY

import com.example.shoes_project.model.Brand;

import java.io.Serializable;

@Entity(tableName = "category",
        foreignKeys = @ForeignKey(entity = Brand.class,
                parentColumns = "id",
                childColumns = "brandId",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index(value = {"brandId"})}) // <-- THÊM DÒNG NÀY ĐỂ TẠO INDEX
public class Category implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private int brandId;

    private String name;
    private String description;
    private String imageUrl;
    private boolean isActive;
    private String createdAt;
    private String updatedAt;

    public Category() {
        // Constructor rỗng mà Room sẽ sử dụng
    }
    @Ignore
    public Category(int brandId, String name, String description, String imageUrl, boolean isActive) {
        this.brandId = brandId;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.isActive = isActive;
    }

    // ... các getter & setter
    public int getBrandId() {
        return brandId;
    }

    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}