package com.example.shoes_project.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.shoes_project.R;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.shoes_project.data.AppDatabase;
import com.example.shoes_project.model.Brand;
import com.example.shoes_project.model.Category;
import com.example.shoes_project.model.Product;
import com.bumptech.glide.Glide;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProductDetailActivity extends AppCompatActivity {
    private TextView tvProductName, tvBrand, tvCategory, tvPrice, tvQuantity,
            tvSellingPrice, tvSize, tvDescription, tvColor;
    private ImageView ivProduct;
    private Button btnEditProduct, btnDeleteProduct;
    private AppDatabase database;
    private Product currentProduct;
    private int productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        productId = getIntent().getIntExtra("product_id", -1);
        if (productId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy ID sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        initViews();
        initDatabase();
        loadProductDetail();
        setupButtonListeners();
    }

    private void initViews() {
        tvProductName = findViewById(R.id.tv_product_name);
        tvBrand = findViewById(R.id.tv_brand);
        tvCategory = findViewById(R.id.tv_category);
        tvPrice = findViewById(R.id.tv_price);
        tvQuantity = findViewById(R.id.tv_quantity);
        tvSellingPrice = findViewById(R.id.tv_selling_price);
        tvSize = findViewById(R.id.tv_size);
        tvDescription = findViewById(R.id.tv_description);
        tvColor = findViewById(R.id.tv_color);
        ivProduct = findViewById(R.id.iv_product);
        btnEditProduct = findViewById(R.id.btn_edit_product);
        btnDeleteProduct = findViewById(R.id.btn_delete_product);
    }

    private void initDatabase() {
        database = AppDatabase.getInstance(this);
    }

    private void loadProductDetail() {
        new Thread(() -> {
            currentProduct = database.productDao().getProductById(productId);

            // Lấy brand và category từ id
            Brand brand = database.brandDao().getBrandById(currentProduct.getBrandId());
            Category category = database.categoryDao().getCategoryById(currentProduct.getCategoryId());

            runOnUiThread(() -> {
                if (currentProduct != null) {
                    displayProductDetails(currentProduct, brand, category);
                }
            });
        }).start();
    }


    private void displayProductDetails(Product product, Brand brand, Category category) {
        tvProductName.setText(product.getProductName());
        tvBrand.setText("Brand: " + (brand != null ? brand.getName() : "Unknown"));
        tvCategory.setText("Category: " + (category != null ? category.getName() : "Unknown"));

        // SỬA: Format giá tiền thống nhất
        tvPrice.setText(String.format("Price: $%.2f", product.getPrice()));
        tvSellingPrice.setText(String.format("Selling Price: $%.2f", product.getSellingPrice()));

        tvQuantity.setText("Quantity: " + product.getQuantity());
        tvSize.setText("Size: " + product.getSize());
        tvDescription.setText("Description: " + product.getDescription());
        tvColor.setText("Color: " + product.getColor());

        // Load image với Glide
        loadProductImage(product.getImageUrl());
    }

    private void loadProductImage(String imagePath) {
        if (imagePath != null && !imagePath.isEmpty()) {
            // Debug log
            android.util.Log.d("ProductDetailActivity", "Loading image: " + imagePath);

            if (imagePath.startsWith("http") || imagePath.startsWith("https")) {
                // Load ảnh từ URL
                Glide.with(this)
                        .load(imagePath)
                        .placeholder(R.drawable.ic_image_placeholder)
                        .error(R.drawable.ic_image_placeholder)
                        .into(ivProduct);
            } else if (imagePath.startsWith("content://")) {
                // Load ảnh từ Content URI (Gallery)
                Glide.with(this)
                        .load(imagePath)
                        .placeholder(R.drawable.ic_image_placeholder)
                        .error(R.drawable.ic_image_placeholder)
                        .into(ivProduct);
            } else {
                // Handle file path (both absolute and relative)
                File imageFile;

                if (imagePath.startsWith("/")) {
                    // Absolute path
                    imageFile = new File(imagePath);
                } else {
                    // Relative path - construct full path
                    imageFile = new File(getFilesDir(), imagePath);
                }

                android.util.Log.d("ProductDetailActivity", "Image file path: " + imageFile.getAbsolutePath());
                android.util.Log.d("ProductDetailActivity", "Image file exists: " + imageFile.exists());

                if (imageFile.exists()) {
                    Glide.with(this)
                            .load(imageFile)
                            .placeholder(R.drawable.ic_image_placeholder)
                            .error(R.drawable.ic_image_placeholder)
                            .into(ivProduct);
                } else {
                    // Nếu file không tồn tại, hiển thị placeholder
                    ivProduct.setImageResource(R.drawable.ic_image_placeholder);
                    android.util.Log.e("ProductDetailActivity", "Image file not found: " + imageFile.getAbsolutePath());
                }
            }
        } else {
            // Nếu không có ảnh, hiển thị placeholder
            ivProduct.setImageResource(R.drawable.ic_image_placeholder);
            android.util.Log.d("ProductDetailActivity", "No image path provided");
        }
    }

    private void setupButtonListeners() {
        btnEditProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProduct();
            }
        });

        btnDeleteProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });
    }


    private void editProduct() {
        if (currentProduct != null) {
            // SỬA: Đổi thành EditProductActivity thay vì AddEditProductActivity
            Intent intent = new Intent(ProductDetailActivity.this, EditProductActivity.class);
            intent.putExtra("product_id", currentProduct.getId());
            startActivityForResult(intent, 100); // Request code 100 for edit
        }
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Product");
        builder.setMessage("Are you sure you want to delete this product? This action cannot be undone.");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteProduct();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void deleteProduct() {
        if (currentProduct != null) {
            new Thread(() -> {
                try {
                    // Xóa file ảnh nếu có
                    if (currentProduct.getImageUrl() != null && !currentProduct.getImageUrl().isEmpty()) {
                        deleteImageFile(currentProduct.getImageUrl());
                    }

                    database.productDao().deleteProduct(currentProduct);
                    runOnUiThread(() -> {
                        Toast.makeText(ProductDetailActivity.this, "Product deleted successfully", Toast.LENGTH_SHORT).show();
                        // Return to previous activity with result
                        setResult(RESULT_OK);
                        finish();
                    });
                } catch (Exception e) {
                    runOnUiThread(() -> {
                        Toast.makeText(ProductDetailActivity.this, "Error deleting product", Toast.LENGTH_SHORT).show();
                    });
                }
            }).start();
        }
    }

    private void deleteImageFile(String imagePath) {
        try {
            if (!imagePath.startsWith("http") && !imagePath.startsWith("content://")) {
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    imageFile.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            // Refresh product details after edit
            loadProductDetail();
        }
    }

}