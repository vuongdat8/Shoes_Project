package com.example.shoes_project.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.shoes_project.R;
import com.example.shoes_project.data.AppDatabase;
import com.example.shoes_project.model.CartA;
import com.example.shoes_project.model.Product;
import com.example.shoes_project.model.ProductWithDetails;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerProductDetailActivity extends AppCompatActivity {
    private ImageButton btnBack;
    private ImageView ivProductImage;
    private TextView tvProductName, tvBrand, tvCategory, tvPrice, tvOriginalPrice,
            tvDiscountBadge, tvSize, tvColor, tvStockStatus, tvDescription,
            tvProductId, tvQuantity;
    private View viewStockIndicator;
    private Button btnAddToCart, btnBuyNow;
    private ProductWithDetails productDetails;

    private AppDatabase database;
    private Product currentProduct;
    private int productId;
    private ImageButton btnCart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_product_detail);

        initViews();
        initDatabase();
        getProductIdFromIntent();
        loadProductDetails();
        setupClickListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        ivProductImage = findViewById(R.id.iv_product_image);
        tvProductName = findViewById(R.id.tv_product_name);
        tvBrand = findViewById(R.id.tv_brand);
        tvCategory = findViewById(R.id.tv_category);
        tvPrice = findViewById(R.id.tv_price);
        tvOriginalPrice = findViewById(R.id.tv_original_price);
        tvDiscountBadge = findViewById(R.id.tv_discount_badge);
        tvSize = findViewById(R.id.tv_size);
        tvColor = findViewById(R.id.tv_color);
        tvStockStatus = findViewById(R.id.tv_stock_status);
        tvDescription = findViewById(R.id.tv_description);
        tvProductId = findViewById(R.id.tv_product_id);
        tvQuantity = findViewById(R.id.tv_quantity);
        viewStockIndicator = findViewById(R.id.view_stock_indicator);
        btnAddToCart = findViewById(R.id.btn_add_to_cart);
        btnBuyNow = findViewById(R.id.btn_buy_now);
        btnCart = findViewById(R.id.btn_cart);}

    private void initDatabase() {
        database = AppDatabase.getInstance(this);
    }

    private void getProductIdFromIntent() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("product_id")) {
            productId = intent.getIntExtra("product_id", -1);
        } else {
            Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadProductDetails() {
        if (productId == -1) return;

        new Thread(() -> {
            productDetails = database.productDao().getProductWithDetailsById(productId);

            runOnUiThread(() -> {
                if (productDetails != null) {
                    currentProduct = productDetails.product;
                    displayProductDetails(productDetails);
                } else {
                    Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }).start();
    }


    private void displayProductDetails(ProductWithDetails details) {
        Product product = details.product;

        tvProductName.setText(product.getProductName());
        tvBrand.setText(details.brand != null ? details.brand.getName() : "Unknown Brand");
        tvCategory.setText(details.category != null ? details.category.getName() : "Unknown Category");

        tvPrice.setText("$" + String.format("%.3f", product.getPrice()));
        tvSize.setText(String.valueOf(product.getSize()));
        tvColor.setText(product.getColor());
        tvDescription.setText(product.getDescription());
        tvProductId.setText(String.valueOf(product.getId()));

        // Original price & discount
        if (product.getSellingPrice() != product.getPrice()) {
            tvOriginalPrice.setText("$" + String.format("%.0f", (double) product.getSellingPrice()));
            tvOriginalPrice.setVisibility(View.VISIBLE);
            tvOriginalPrice.setPaintFlags(tvOriginalPrice.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);

            double discountPercent = ((product.getSellingPrice() - product.getPrice()) / product.getSellingPrice()) * 1;
            tvDiscountBadge.setText(String.format("%.0f%% OFF", discountPercent));
            tvDiscountBadge.setVisibility(View.VISIBLE);
        } else {
            tvOriginalPrice.setVisibility(View.GONE);
            tvDiscountBadge.setVisibility(View.GONE);
        }

        updateStockStatus();
        loadProductImage(product.getImageUrl());
    }


    // THÊM METHOD LOAD IMAGE - QUAN TRỌNG
    private void loadProductImage(String imagePath) {
        if (imagePath != null && !imagePath.isEmpty()) {
            if (imagePath.startsWith("http") || imagePath.startsWith("https")) {
                // Load image from URL
                Glide.with(this)
                        .load(imagePath)
                        .placeholder(R.drawable.ic_image_placeholder)
                        .error(R.drawable.ic_image_placeholder)
                        .into(ivProductImage);
            } else if (imagePath.startsWith("content://")) {
                // Load image from Content URI (Gallery)
                Glide.with(this)
                        .load(imagePath)
                        .placeholder(R.drawable.ic_image_placeholder)
                        .error(R.drawable.ic_image_placeholder)
                        .into(ivProductImage);
            } else {
                // Handle both absolute and relative paths
                File imageFile;

                if (imagePath.startsWith("/")) {
                    // Absolute path
                    imageFile = new File(imagePath);
                } else {
                    // Relative path - construct full path
                    imageFile = new File(getFilesDir(), imagePath);
                }

                if (imageFile.exists()) {
                    Glide.with(this)
                            .load(imageFile)
                            .placeholder(R.drawable.ic_image_placeholder)
                            .error(R.drawable.ic_image_placeholder)
                            .into(ivProductImage);
                } else {
                    // If file doesn't exist, show placeholder
                    ivProductImage.setImageResource(R.drawable.ic_image_placeholder);
                    // Log for debugging
                    android.util.Log.e("ImageLoad", "Image file not found: " + imageFile.getAbsolutePath());
                }
            }
        } else {
            // No image, show placeholder
            ivProductImage.setImageResource(R.drawable.ic_image_placeholder);
        }
    }


    private void updateStockStatus() {
        int quantity = currentProduct.getQuantity();
        tvQuantity.setText(quantity + " items");

        if (quantity > 0) {
            if (quantity > 10) {
                tvStockStatus.setText("In Stock (" + quantity + " available)");
                tvStockStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                viewStockIndicator.setBackgroundResource(R.drawable.circle_green);
                enablePurchaseButtons(true);
            } else {
                tvStockStatus.setText("Low Stock (" + quantity + " left)");
                tvStockStatus.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
                viewStockIndicator.setBackgroundResource(R.drawable.circle_orange);
                enablePurchaseButtons(true);
            }
        } else {
            tvStockStatus.setText("Out of Stock");
            tvStockStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            viewStockIndicator.setBackgroundResource(R.drawable.circle_red);
            enablePurchaseButtons(false);
        }
    }

    private void enablePurchaseButtons(boolean enabled) {
        btnAddToCart.setEnabled(enabled);
        btnBuyNow.setEnabled(enabled);

        if (enabled) {
            btnAddToCart.setAlpha(1.0f);
            btnBuyNow.setAlpha(1.0f);
        } else {
            btnAddToCart.setAlpha(0.5f);
            btnBuyNow.setAlpha(0.5f);
        }
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnAddToCart.setOnClickListener(v -> {
            if (currentProduct != null && currentProduct.getQuantity() > 0) {
                addToCart();
            } else {
                Toast.makeText(this, "Product is out of stock", Toast.LENGTH_SHORT).show();
            }
        });

        btnBuyNow.setOnClickListener(v -> {
            if (currentProduct != null && currentProduct.getQuantity() > 0) {
                buyNow();
            } else {
                Toast.makeText(this, "Product is out of stock", Toast.LENGTH_SHORT).show();

            }
        });
        btnCart.setOnClickListener(v -> {
            Intent intent = new Intent(CustomerProductDetailActivity.this, CartActivity.class);
            startActivity(intent);
        });
    }

    private void addToCart() {
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);


        if (userId == -1) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        Double selectedSize = currentProduct.getSize();
        String selectedColor = currentProduct.getColor();

        new Thread(() -> {
            // Kiểm tra sản phẩm đã tồn tại chưa
            CartA existing = database.cartADao().getExistingCartA(userId, currentProduct.getId(), selectedColor, selectedSize);
            if (existing != null) {
                existing.setQuantity(existing.getQuantity() + 1);
                database.cartADao().insertCartA(existing);
            } else {
                CartA newItem = new CartA(
                        userId,
                        currentProduct.getId(),
                        currentProduct.getProductName(),
                        currentProduct.getImageUrl(),
                        currentProduct.getPrice(),
                        1,
                        selectedColor,
                        selectedSize
                );
                newItem.setSelected(true);
                database.cartADao().insertCartA(newItem);
            }

            runOnUiThread(() -> {
                Toast.makeText(this, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(CustomerProductDetailActivity.this, CartActivity.class));
            });
        }).start();
    }

    private void buyNow() {
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);

        if (userId == -1) {
            Toast.makeText(this, "Người dùng chưa đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare the current product as a "single-item cart"
        ArrayList<Integer> productIds = new ArrayList<>();
        ArrayList<String> productNames = new ArrayList<>();
        ArrayList<String> imageUrls = new ArrayList<>();
        ArrayList<Double> prices = new ArrayList<>();
        ArrayList<Integer> quantities = new ArrayList<>();
        ArrayList<String> colors = new ArrayList<>();
        ArrayList<Double> sizes = new ArrayList<>();

        productIds.add(currentProduct.getId());
        productNames.add(currentProduct.getProductName());
        imageUrls.add(currentProduct.getImageUrl());
        prices.add(currentProduct.getPrice());
        quantities.add(1); // Default quantity = 1
        colors.add(currentProduct.getColor());
        sizes.add(currentProduct.getSize());

        Intent intent = new Intent(CustomerProductDetailActivity.this, CheckoutActivity.class);
        intent.putIntegerArrayListExtra("product_ids", productIds);
        intent.putStringArrayListExtra("product_names", productNames);
        intent.putStringArrayListExtra("product_image_urls", imageUrls);
        intent.putExtra("product_prices", prices);
        intent.putIntegerArrayListExtra("quantities", quantities);
        intent.putStringArrayListExtra("selected_colors", colors);
        intent.putExtra("selected_sizes", sizes);

        startActivity(intent);
        Toast.makeText(this, "Đang chuyển đến trang thanh toán...", Toast.LENGTH_SHORT).show();
    }



    @Override
    protected void onResume() {
        super.onResume();
        // Refresh product details when returning from other activities
        if (currentProduct != null) {
            loadProductDetails();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}