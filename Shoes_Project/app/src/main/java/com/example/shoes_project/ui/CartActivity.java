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
import com.example.shoes_project.model.CartItem;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

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
        findViewById(R.id.btnBack).setOnClickListener(v -> onBackPressed());

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

        cartViewModel.getCartItems().observe(this, cartItems -> {
            if (cartItems != null) {
                cartAdapter.setCartItems(cartItems);
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
            // Lấy data từ ViewModel thay vì adapter
            List<CartItem> allItems = cartViewModel.getCartItems().getValue();
            ArrayList<Product> products = new ArrayList<>();
            ArrayList<Integer> quantities = new ArrayList<>();

            if (allItems != null) {
                for (CartItem item : allItems) {
                    if (item.isSelected()) {
                        Product product = new Product();
                        product.setId(item.getProductId());
                        product.setProductName(item.getProductName());
                        product.setBrand("");
                        product.setPrice(item.getPrice());
                        product.setImageUrl(item.getImageUrl());

                        products.add(product);
                        quantities.add(item.getQuantity());
                    }
                }
            }

            if (!products.isEmpty()) {
                Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
                intent.putExtra("selected_products", products);
                intent.putExtra("quantities", quantities);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Vui lòng chọn sản phẩm để thanh toán", Toast.LENGTH_SHORT).show();
            }
        });
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
