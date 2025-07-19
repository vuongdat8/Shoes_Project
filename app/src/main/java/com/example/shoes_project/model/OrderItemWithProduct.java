package com.example.shoes_project.model;

import androidx.room.Embedded;
import androidx.room.Relation;

public class OrderItemWithProduct {
    @Embedded
    public OrderItem orderItem;

    @Relation(
            parentColumn = "productId",
            entityColumn = "id"
    )
    public Product product;

    // Getters từ OrderItem
    public int getOrderItemId() { return orderItem.getOrderItemId(); }
    public int getOrderId() { return orderItem.getOrderId(); }
    public int getProductId() { return orderItem.getProductId(); }
    public String getProductName() { return orderItem.getProductName(); }
    public String getProductImage() { return orderItem.getProductImage(); }
    public double getProductPrice() { return orderItem.getProductPrice(); }
    public int getQuantity() { return orderItem.getQuantity(); }
    public String getSelectedColor() { return orderItem.getSelectedColor(); }
    public String getSelectedSize() { return orderItem.getSelectedSize(); }
    public double getSubtotal() { return orderItem.getSubtotal(); }

    // Getters từ Product
    public String getDescription() { return product != null ? product.getDescription() : ""; }
    public double getSize() { return product != null ? product.getSize() : 0; }
    public String getColor() { return product != null ? product.getColor() : orderItem.getSelectedColor(); }
    public int getStockQuantity() { return product != null ? product.getQuantity() : 0; }
    public double getSellingPrice() { return product != null ? product.getSellingPrice() : 0; }

    public String getActualProductImage() {
        if (product != null && product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            return product.getImageUrl();
        }
        return orderItem.getProductImage();
    }

    public boolean isProductAvailable() { return product != null; }

    public String getFullProductInfo() {
        if (product != null) {
            return product.getProductName() + " - Kích thước: " + product.getSize() + " - Màu: " + product.getColor();
        }
        return orderItem.getProductName() + " - Kích thước: " + orderItem.getSelectedSize() + " - Màu: " + orderItem.getSelectedColor();
    }

    public boolean isPriceChanged() {
        return product != null && product.getPrice() != orderItem.getProductPrice();
    }

    public double getCurrentPrice() {
        return product != null ? product.getPrice() : orderItem.getProductPrice();
    }
}