package com.prm.shoes_project.controller;

import android.content.Context;

import androidx.fragment.app.Fragment;

import com.prm.shoes_project.data.DatabaseClient;
import com.prm.shoes_project.data.db.entity.Product;
import com.prm.shoes_project.ui.view.base.ProductView;

import java.util.List;
import java.util.UUID;

public class ProductController {
    private ProductView view;
    private DatabaseClient dbClient;

    public ProductController(ProductView view) {
        this.view = view;
        // Lấy Context từ Fragment hoặc Activity một cách an toàn
        Context context;
        if (view instanceof Fragment) {
            context = ((Fragment) view).requireContext();
        } else if (view instanceof Context) {
            context = (Context) view;
        } else {
            throw new IllegalArgumentException("ProductView must be a Fragment or Context");
        }
        this.dbClient = DatabaseClient.getInstance(context);
    }

    public void loadProducts() {
        dbClient.getExecutorService().execute(() -> {
            final List<Product> products = dbClient.getAppDatabase().productDao().getAllProducts();

            dbClient.getMainHandler().post(() -> {
                view.displayProducts(products);
            });
        });
    }

    public void loadProductDetails(UUID productId) {
        dbClient.getExecutorService().execute(() -> {
            final Product product = dbClient.getAppDatabase().productDao().getProductById(productId);

            dbClient.getMainHandler().post(() -> {
                view.displayProductDetails(product);
            });
        });
    }

    public void loadProductsByBrand(UUID brandId){
        dbClient.getExecutorService().execute(() -> {
            final List<Product> products = dbClient.getAppDatabase().productDao().getProductsByBrand(brandId);

            dbClient.getMainHandler().post(() -> {
                view.displayProducts(products);
            });
        });
    }

    public void loadProductsByCategory(UUID categoryId){
        dbClient.getExecutorService().execute(() -> {
            final List<Product> products = dbClient.getAppDatabase().productDao().getProductsByCategory(categoryId);

            dbClient.getMainHandler().post(() -> {
                view.displayProducts(products);
            });
        });
    }

    //add product
    public void addProduct(Product product) {
        dbClient.getExecutorService().execute(() -> {
            dbClient.getAppDatabase().productDao().insert(product);
            dbClient.getMainHandler().post(() -> {
                // Có thể thêm thông báo hoặc cập nhật giao diện nếu cần
            });
        });
    }


    public void updateProduct(Product product) {
        dbClient.getExecutorService().execute(() -> {
            dbClient.getAppDatabase().productDao().update(product);
            dbClient.getMainHandler().post(() -> {
                // Có thể thêm thông báo hoặc cập nhật giao diện nếu cần
            });
        });
    }

    public void deleteProduct(Product product) {
        dbClient.getExecutorService().execute(() -> {
            dbClient.getAppDatabase().productDao().delete(product);
            dbClient.getMainHandler().post(() -> {
                // Có thể thêm thông báo hoặc cập nhật giao diện nếu cần
            });
        });
    }


}
