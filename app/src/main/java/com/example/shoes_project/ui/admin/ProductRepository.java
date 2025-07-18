package com.example.shoes_project.ui.admin;

import android.content.Context;

import com.example.shoes_project.data.AppDatabase;
import com.example.shoes_project.model.Product;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProductRepository {
    private AppDatabase database;

    public ProductRepository(Context context) {
        database = AppDatabase.getInstance(context);
    }

    public List<String> getAvailableColors(int productId) {
        Product product = database.productDao().getProductById(productId);
        if (product != null && product.getColor() != null) {
            return Arrays.asList(product.getColor().split(","));
        }
        return new ArrayList<>();
    }

    public List<Double> getAvailableSizes(int productId) {
        Product product = database.productDao().getProductById(productId);
        return new ArrayList<>();
    }
}
