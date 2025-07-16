package com.example.shoes_project.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "brand")
public class Brand implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String description;
    private String imageUrl;
    private boolean isActive;
    private String createdAt;
    private String updatedAt;


    public Brand() {
        // Constructor rỗng mà Room sẽ sử dụng
    }
    // Constructor tùy chỉnh - dùng để thêm mới (không dùng bởi Room)
    @Ignore
    public Brand(String name, String description, String imageUrl, boolean isActive) {
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.isActive = isActive;
    }

    // Getter & Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
