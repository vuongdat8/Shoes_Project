package com.example.shoes_project.model;

import androidx.room.Embedded;
import androidx.room.Relation;

public class CartAWithProduct {

    @Embedded
    public CartA CartA;

    @Relation(
            parentColumn = "productId",
            entityColumn = "id"
    )
    public Product product;

    public double getTotalPrice() {
        return product.getSellingPrice() * CartA.getQuantity();
    }
}