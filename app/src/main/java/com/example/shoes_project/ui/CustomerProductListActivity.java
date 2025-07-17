package com.example.shoes_project.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.shoes_project.R;
import com.example.shoes_project.adapter.CustomerProductAdapter;
import com.example.shoes_project.data.AppDatabase;
import com.example.shoes_project.model.Product;
import com.example.shoes_project.model.ProductWithDetails;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CustomerProductListActivity extends AppCompatActivity {

    private AppDatabase database;
    private CustomerProductAdapter productAdapter;

    private RecyclerView recyclerView;
    private EditText etSearch;
    private Spinner spinnerCategory, spinnerBrand;
    private String currentEmail;
    private List<ProductWithDetails> allProducts = new ArrayList<>();
    private List<ProductWithDetails> filteredProducts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_product_list);
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        currentEmail = prefs.getString("email", null);

        Button btnProfile = findViewById(R.id.btn_profile);
        btnProfile.setOnClickListener(v -> openProfileScreen());
        initViews();
        initDatabase();
        loadProducts();
        setupRecyclerView();
        setupSpinners();
        setupSearchListener();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.rv_products);
        etSearch = findViewById(R.id.et_search);
        spinnerCategory = findViewById(R.id.spinner_category);
        spinnerBrand = findViewById(R.id.spinner_brand);
    }

    private void initDatabase() {
        database = AppDatabase.getInstance(this);
    }

    private void loadProducts() {
        allProducts = database.productDao().getAllProductsWithDetails();
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        productAdapter = new CustomerProductAdapter(this, filteredProducts);
        recyclerView.setAdapter(productAdapter);

        // THÊM CLICK LISTENER CHO ADAPTER
        productAdapter.setOnProductClickListener(new CustomerProductAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(Product product) {
                // Mở trang chi tiết sản phẩm
                openProductDetailScreen(product);
            }
        });
    }

    private void setupSpinners() {
        Set<String> categorySet = new HashSet<>();
        Set<String> brandSet = new HashSet<>();

        for (ProductWithDetails product : allProducts) {
            if (product.category != null && product.category.getName() != null) {
                categorySet.add(product.category.getName());
            }
            if (product.brand != null && product.brand.getName() != null) {
                brandSet.add(product.brand.getName());
            }
        }

        List<String> categoryList = new ArrayList<>();
        categoryList.add("All Categories");
        categoryList.addAll(categorySet);

        List<String> brandList = new ArrayList<>();
        brandList.add("All Brands");
        brandList.addAll(brandSet);

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categoryList);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        ArrayAdapter<String> brandAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, brandList);
        brandAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBrand.setAdapter(brandAdapter);

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                filterProducts();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerBrand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                filterProducts();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Initial filter
        filterProducts();
    }

    private void setupSearchListener() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts();
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void filterProducts() {
        String query = etSearch.getText().toString().trim().toLowerCase();
        String selectedCategory = getSafeSpinnerValue(spinnerCategory, "All Categories");
        String selectedBrand = getSafeSpinnerValue(spinnerBrand, "All Brands");

        filteredProducts.clear();

        for (ProductWithDetails pwd : allProducts) {
            Product p = pwd.product;

            boolean matchesSearch = query.isEmpty()
                    || p.getProductName().toLowerCase().contains(query)
                    || (pwd.brand != null && pwd.brand.getName().toLowerCase().contains(query))
                    || (pwd.category != null && pwd.category.getName().toLowerCase().contains(query))
                    || p.getDescription().toLowerCase().contains(query);

            boolean matchesCategory = selectedCategory.equals("All Categories")
                    || (pwd.category != null && selectedCategory.equals(pwd.category.getName()));

            boolean matchesBrand = selectedBrand.equals("All Brands")
                    || (pwd.brand != null && selectedBrand.equals(pwd.brand.getName()));

            if (matchesSearch && matchesCategory && matchesBrand) {
                filteredProducts.add(pwd);
            }
        }

        productAdapter.updateProductList(filteredProducts);
    }

    private String getSafeSpinnerValue(Spinner spinner, String defaultValue) {
        Object selectedItem = spinner.getSelectedItem();
        return selectedItem != null ? selectedItem.toString() : defaultValue;
    }

    private void openProfileScreen() {
        if (currentEmail == null) {
            Toast.makeText(this, "Phiên đăng nhập đã hết, vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, UserProfileActivity.class);
        intent.putExtra("email", currentEmail);
        startActivity(intent);
    }

    // THÊM METHOD MỚI ĐỂ MỞ TRANG CHI TIẾT SẢN PHẨM
    private void openProductDetailScreen(Product product) {
        Intent intent = new Intent(this, CustomerProductDetailActivity.class);
        intent.putExtra("product_id", product.getId());
        startActivity(intent);
    }
}