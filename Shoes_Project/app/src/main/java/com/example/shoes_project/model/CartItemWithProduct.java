package com.example.shoes_project.model;

import androidx.room.Embedded;
import androidx.room.Relation;

public class CartItemWithProduct {

    @Embedded
    public CartItem cartItem;

    @Relation(
            parentColumn = "productId",
            entityColumn = "id"
    )
    public Product product;

    public double getTotalPrice() {
        return product.getSellingPrice() * cartItem.getQuantity();
    }
}