package com.example.shoes_project.data.db.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

import java.util.UUID;

public class Category {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "categoryId")
    private UUID categoryId;
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "description")
    private String description;

    public Category() {
        this.categoryId = UUID.randomUUID();
    }

    public Category(UUID uuid, String name, String description) {
        this.categoryId = uuid;
        this.name = name;
        this.description = description;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
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
}
