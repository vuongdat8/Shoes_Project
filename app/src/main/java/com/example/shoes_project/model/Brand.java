package com.example.shoes_project.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
@Entity(tableName = "brand")
public class Brand implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String description;
    private boolean isActive;
    private String createdAt;
    private String updatedAt;
    private String imageUrl;

    // Constructors, getters và setters

//    public Brand(int id, String name, String description, boolean isActive, String createdAt, String updatedAt, String imageUrl) {
//        this.id = id;
//        this.name = name;
//        this.description = description;
//        this.isActive = isActive;
//        this.createdAt = createdAt;
//        this.updatedAt = updatedAt;
//        this.imageUrl = imageUrl;
//    }


    public Brand(String name, String description, String imageUrl, boolean isActive) {
            this.name=name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.isActive = isActive;
    }

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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "Brand{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", isActive=" + isActive +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}

