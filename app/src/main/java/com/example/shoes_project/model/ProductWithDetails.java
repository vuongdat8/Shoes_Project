package com.example.shoes_project.model;

import androidx.room.Embedded;
import androidx.room.Relation;

public class ProductWithDetails {
    @Embedded
    public Product product; // This embeds all fields of Product

    @Relation(
            parentColumn = "brandId", // Column in Product
            entityColumn = "id"       // Column in Brand
    )
    public Brand brand;

    @Relation(
            parentColumn = "categoryId", // Column in Product
            entityColumn = "id"          // Column in Category
    )
    public Category category;
}
