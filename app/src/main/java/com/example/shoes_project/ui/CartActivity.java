package com.example.shoes_project.ui;


import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoes_project.R;
import com.example.shoes_project.adapter.CartAdapter;
import com.example.shoes_project.data.AppDatabase;
import com.example.shoes_project.model.CartA;
import com.example.shoes_project.model.Product;

import java.util.ArrayList;
import java.util.List;


public class CartActivity extends AppCompatActivity {
    private static final String PREF_AUTH = "auth";
    private CartViewModel cartViewModel;
    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private TextView tvTotalPrice, tvSelectedCount;
    private Button btnCheckout, btnDeleteSelected;
    private CheckBox cbSelectAll;
    private int currentUserId;
    private AppDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        database = AppDatabase.getInstance(this);
        initViews();
        setupViewModel();
        setupRecyclerView();
        setupClickListeners();

        // Lấy userId từ SharedPreferences hoặc Intent
        SharedPreferences prefs = getSharedPreferences(PREF_AUTH, MODE_PRIVATE);
        currentUserId = prefs.getInt("userId", -1);

        if (currentUserId != -1) {
            cartViewModel.setUserId(currentUserId);
        }
    }

    private void initViews() {
        // Header
        findViewById(R.id.btn_back).setOnClickListener(v -> onBackPressed());

        // Content
        recyclerView = findViewById(R.id.recyclerViewCart);

        // Footer
        cbSelectAll = findViewById(R.id.cbSelectAll);
        tvSelectedCount = findViewById(R.id.tvSelectedCount);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnCheckout = findViewById(R.id.btnCheckout);
        btnDeleteSelected = findViewById(R.id.btnDeleteSelected);
    }

    private void setupViewModel() {
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);

        cartViewModel.getCartAs().observe(this, CartAs -> {
            if (CartAs != null) {
                cartAdapter.setCartAs(CartAs);
            }
        });

        cartViewModel.getTotalPrice().observe(this, totalPrice -> {
            if (totalPrice != null) {
                tvTotalPrice.setText(String.format("%.0f VND", totalPrice));
            }
        });

        cartViewModel.getSelectedItemCount().observe(this, selectedCount -> {
            if (selectedCount != null) {
                tvSelectedCount.setText(String.format("(%d)", selectedCount));
                btnCheckout.setEnabled(selectedCount > 0);
            }
        });

        cartViewModel.getIsAllSelected().observe(this, isAllSelected -> {
            if (isAllSelected != null) {
                cbSelectAll.setOnCheckedChangeListener(null);
                cbSelectAll.setChecked(isAllSelected);
                cbSelectAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    cartViewModel.selectAllItems(isChecked);
                });
            }
        });
    }

    private void setupRecyclerView() {
        cartAdapter = new CartAdapter(this, cartViewModel);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(cartAdapter);
        Log.d("CartActivity", "RecyclerView item count: " + cartAdapter.getItemCount());
    }

    private void setupClickListeners() {
        btnCheckout.setOnClickListener(v -> {
            List<CartA> allItems = cartViewModel.getCartAs().getValue();
            ArrayList<Integer> productIds = new ArrayList<>();
            ArrayList<String> productNames = new ArrayList<>();
            ArrayList<String> imageUrls = new ArrayList<>();
            ArrayList<Double> prices = new ArrayList<>();
            ArrayList<Integer> quantities = new ArrayList<>();
            ArrayList<String> colors = new ArrayList<>();
            ArrayList<Double> sizes = new ArrayList<>();

            if (allItems != null) {
                int selectedCount = 0;
                for (CartA item : allItems) {
                    if (item.isSelected()) {
                        selectedCount++;
                        productIds.add(item.getProductId());
                        productNames.add(item.getProductName());
                        imageUrls.add(item.getImageUrl());
                        prices.add(item.getPrice());
                        quantities.add(item.getQuantity());
                        colors.add(item.getSelectedColor());
                        sizes.add(item.getSelectedSize());
                        Log.d("CartActivity", "Selected size for item " + item.getProductName() + ": " + item.getSelectedSize());

                        Log.d("CartActivity", "Selected item: " + item.getProductName() + ", ID: " + item.getProductId());
                    }
                }
                Log.d("CartActivity", "Total selected items: " + selectedCount);
            } else {
                Log.e("CartActivity", "Cart items list is null");
            }

            if (!productIds.isEmpty()) {
                Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
                intent.putIntegerArrayListExtra("product_ids", productIds);
                intent.putStringArrayListExtra("product_names", productNames);
                intent.putStringArrayListExtra("product_image_urls", imageUrls);
                intent.putExtra("product_prices", prices);                intent.putIntegerArrayListExtra("quantities", quantities);
                intent.putStringArrayListExtra("selected_colors", colors);
                intent.putExtra("selected_sizes", sizes);
                intent.putExtra("user_id", currentUserId);
                Log.d("CartActivity", "Starting CheckoutActivity with " + productIds.size() + " products");
                startActivity(intent);
                Toast.makeText(this, "Đang chuyển đến trang thanh toán...", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Vui lòng chọn sản phẩm để thanh toán", Toast.LENGTH_SHORT).show();
                Log.w("CartActivity", "No products selected for checkout");
            }
        });

        btnDeleteSelected.setOnClickListener(v -> showDeleteConfirmDialog());
    }
    private void showDeleteConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Xóa sản phẩm")
                .setMessage("Bạn có chắc chắn muốn xóa những sản phẩm đã chọn?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    cartViewModel.deleteSelectedItems();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
