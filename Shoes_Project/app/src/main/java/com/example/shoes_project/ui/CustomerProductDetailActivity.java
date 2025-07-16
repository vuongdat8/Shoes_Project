package com.example.shoes_project.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.shoes_project.R;
import com.example.shoes_project.data.AppDatabase;
import com.example.shoes_project.model.Product;

public class CustomerProductDetailActivity extends AppCompatActivity {
    private ImageButton btnBack;
    private ImageView ivProductImage;
    private TextView tvProductName, tvBrand, tvCategory, tvPrice, tvOriginalPrice,
            tvDiscountBadge, tvSize, tvColor, tvStockStatus, tvDescription,
            tvProductId, tvQuantity;
    private View viewStockIndicator;
    private Button btnAddToCart, btnBuyNow;
    private ImageButton btnCart;
    private AppDatabase database;
    private Product currentProduct;
    private int productId;

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
        btnCart = findViewById(R.id.btn_cart);

    }

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
            currentProduct = database.productDao().getProductById(productId);

            runOnUiThread(() -> {
                if (currentProduct != null) {
                    displayProductDetails();
                } else {
                    Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }).start();
    }

    private void displayProductDetails() {
        // Basic product information
        tvProductName.setText(currentProduct.getProductName());
        tvBrand.setText(currentProduct.getBrand());
        tvCategory.setText(currentProduct.getCategoryName());
        tvPrice.setText("$" + String.format("%.2f", currentProduct.getPrice()));
        tvSize.setText(String.valueOf(currentProduct.getSize()));
        tvColor.setText(currentProduct.getColor());
        tvDescription.setText(currentProduct.getDescription());
        tvProductId.setText(String.valueOf(currentProduct.getId()));

        // Handle original price and discount
        if (currentProduct.getSellingPrice() != currentProduct.getPrice()) {
            tvOriginalPrice.setText("$" + String.format("%.2f", (double) currentProduct.getSellingPrice()));
            tvOriginalPrice.setVisibility(View.VISIBLE);
            tvOriginalPrice.setPaintFlags(tvOriginalPrice.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);

            // Calculate and show discount percentage
            double discountPercent = ((currentProduct.getSellingPrice() - currentProduct.getPrice()) / currentProduct.getSellingPrice()) * 100;
            tvDiscountBadge.setText(String.format("%.0f%% OFF", discountPercent));
            tvDiscountBadge.setVisibility(View.VISIBLE);
        } else {
            tvOriginalPrice.setVisibility(View.GONE);
            tvDiscountBadge.setVisibility(View.GONE);
        }

        // Stock status and quantity
        updateStockStatus();

        // Load product image using Glide or Picasso if needed
        // Glide.with(this).load(currentProduct.getImageUrl()).into(ivProductImage);
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
        // TODO: Implement cart functionality
        // For now, just show a success message
        Toast.makeText(this, "Added to cart: " + currentProduct.getProductName(), Toast.LENGTH_SHORT).show();

        // You can implement cart logic here:
        // 1. Add product to cart database/shared preferences
        // 2. Update cart count in UI
        // 3. Show cart icon with badge
    }

    private void buyNow() {
        // TODO: Implement purchase flow
        // For now, show a message and navigate to checkout
        Toast.makeText(this, "Proceeding to checkout...", Toast.LENGTH_SHORT).show();

        // You can implement purchase logic here:
        // 1. Navigate to checkout activity
        // 2. Pass product information
        // 3. Handle payment processing

        // Example navigation to checkout:
        // Intent intent = new Intent(this, CheckoutActivity.class);
        // intent.putExtra("product_id", currentProduct.getId());
        // intent.putExtra("quantity", 1);
        // startActivity(intent);
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