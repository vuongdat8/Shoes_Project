package com.example.shoes_project.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.shoes_project.R;
import com.example.shoes_project.adapter.CustomerProductAdapter;
import com.example.shoes_project.data.AppDatabase;
import com.example.shoes_project.model.Product;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CustomerProductListActivity extends AppCompatActivity implements CustomerProductAdapter.OnProductClickListener {
    private TextInputEditText etSearch;
    private Spinner spinnerCategory, spinnerBrand;
    private Button btnClearFilter;
    private RecyclerView rvProducts;
    private TextView tvProductCount;
    private LinearLayout layoutNoProducts;

    private CustomerProductAdapter productAdapter;
    private AppDatabase database;
    private List<Product> allProducts;
    private List<Product> filteredProducts;
    private List<String> categories;
    private List<String> brands;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_product_list);

        initViews();
        initDatabase();
        setupRecyclerView();
        setupSearchAndFilters();
        loadProducts();
    }

    private void initViews() {
        etSearch = findViewById(R.id.et_search);
        spinnerCategory = findViewById(R.id.spinner_category);
        spinnerBrand = findViewById(R.id.spinner_brand);
        btnClearFilter = findViewById(R.id.btn_clear_filter);
        rvProducts = findViewById(R.id.rv_products);
        tvProductCount = findViewById(R.id.tv_product_count);
        layoutNoProducts = findViewById(R.id.layout_no_products);
    }

    private void initDatabase() {
        database = AppDatabase.getInstance(this);
    }

    private void setupRecyclerView() {
        filteredProducts = new ArrayList<>();
        productAdapter = new CustomerProductAdapter(this, filteredProducts);
        productAdapter.setOnProductClickListener(this);

        rvProducts.setLayoutManager(new LinearLayoutManager(this));
        rvProducts.setAdapter(productAdapter);
    }

    private void setupSearchAndFilters() {
        // Search functionality
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Category filter
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterProducts();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Brand filter
        spinnerBrand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterProducts();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Clear filter button
        btnClearFilter.setOnClickListener(v -> clearFilters());
    }

    private void loadProducts() {
        new Thread(() -> {
            // Load only available products for customers
            allProducts = database.productDao().getAvailableProducts();

            runOnUiThread(() -> {
                if (allProducts != null && !allProducts.isEmpty()) {
                    filteredProducts.clear();
                    filteredProducts.addAll(allProducts);
                    productAdapter.notifyDataSetChanged();

                    setupFilterSpinners();
                    updateProductCount();
                    showProductList();
                } else {
                    showNoProducts();
                }
            });
        }).start();
    }

    private void setupFilterSpinners() {
        // Extract unique categories and brands
        categories = new ArrayList<>();
        brands = new ArrayList<>();
        Set<String> categorySet = new HashSet<>();
        Set<String> brandSet = new HashSet<>();

        categories.add("All Categories");
        brands.add("All Brands");

        for (Product product : allProducts) {
            if (product.getCategoryName() != null && !product.getCategoryName().isEmpty()) {
                categorySet.add(product.getCategoryName());
            }
            if (product.getBrand() != null && !product.getBrand().isEmpty()) {
                brandSet.add(product.getBrand());
            }
        }

        categories.addAll(categorySet);
        brands.addAll(brandSet);

        // Setup category spinner
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        // Setup brand spinner
        ArrayAdapter<String> brandAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, brands);
        brandAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBrand.setAdapter(brandAdapter);
    }

    private void filterProducts() {
        String searchQuery = etSearch.getText().toString().toLowerCase().trim();
        String selectedCategory = spinnerCategory.getSelectedItem() != null ?
                spinnerCategory.getSelectedItem().toString() : "All Categories";
        String selectedBrand = spinnerBrand.getSelectedItem() != null ?
                spinnerBrand.getSelectedItem().toString() : "All Brands";

        filteredProducts.clear();

        for (Product product : allProducts) {
            boolean matchesSearch = searchQuery.isEmpty() ||
                    product.getProductName().toLowerCase().contains(searchQuery) ||
                    product.getBrand().toLowerCase().contains(searchQuery) ||
                    product.getCategoryName().toLowerCase().contains(searchQuery) ||
                    product.getDescription().toLowerCase().contains(searchQuery);

            boolean matchesCategory = selectedCategory.equals("All Categories") ||
                    product.getCategoryName().equals(selectedCategory);

            boolean matchesBrand = selectedBrand.equals("All Brands") ||
                    product.getBrand().equals(selectedBrand);

            if (matchesSearch && matchesCategory && matchesBrand) {
                filteredProducts.add(product);
            }
        }

        productAdapter.notifyDataSetChanged();
        updateProductCount();

        if (filteredProducts.isEmpty()) {
            showNoProducts();
        } else {
            showProductList();
        }
    }

    private void clearFilters() {
        etSearch.setText("");
        spinnerCategory.setSelection(0);
        spinnerBrand.setSelection(0);
        filterProducts();
    }

    private void updateProductCount() {
        int count = filteredProducts.size();
        if (count == 0) {
            tvProductCount.setText("No products found");
        } else if (count == 1) {
            tvProductCount.setText("Showing 1 product");
        } else {
            tvProductCount.setText("Showing " + count + " products");
        }
    }

    private void showProductList() {
        rvProducts.setVisibility(View.VISIBLE);
        layoutNoProducts.setVisibility(View.GONE);
    }

    private void showNoProducts() {
        rvProducts.setVisibility(View.GONE);
        layoutNoProducts.setVisibility(View.VISIBLE);
    }

    @Override
    public void onProductClick(Product product) {
        // Navigate to customer product detail view
        Intent intent = new Intent(this, CustomerProductDetailActivity.class);
        intent.putExtra("product_id", product.getId());
        startActivity(intent);
    }

    @Override
    public void onViewDetailsClick(Product product) {
        // Same as product click for customers
        onProductClick(product);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh product list when returning from detail view
        loadProducts();
    }
}