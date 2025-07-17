package com.example.shoes_project.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoes_project.R;
import com.example.shoes_project.adapter.ProductAdapter;
import com.example.shoes_project.data.AppDatabase;
import com.example.shoes_project.model.Product;
import com.example.shoes_project.model.ProductWithDetails;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements ProductAdapter.OnProductClickListener {
    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private List<ProductWithDetails> productList;
    private AppDatabase database;
    private SearchView searchView;
    private Button btnAddProduct, btnViewOrders;
    private String currentEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        currentEmail = prefs.getString("email", null);

        Button btnProfile = findViewById(R.id.profile);
        btnProfile.setOnClickListener(v -> openProfileScreen());

        initViews();
        initDatabase();
        setupRecyclerView();
        loadProducts();
        setupSearchView();
        setupButtons();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view);
        searchView = findViewById(R.id.search_view);
        btnAddProduct = findViewById(R.id.btn_add_product);
    }

    private void initDatabase() {
        database = AppDatabase.getInstance(this);
    }

    private void setupRecyclerView() {
        productList = new ArrayList<>();
        adapter = new ProductAdapter(this); // chỉ truyền Context
        adapter.setOnProductClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadProducts() {
        new Thread(() -> {
            List<ProductWithDetails> products = database.productDao().getAllProductsWithDetails(); // DAO trả về ProductWithDetails
            runOnUiThread(() -> {
                productList.clear();
                productList.addAll(products);
                adapter.updateProducts(productList);
            });
        }).start();
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchProducts(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    loadProducts();
                } else {
                    searchProducts(newText);
                }
                return true;
            }
        });
    }

    private void searchProducts(String query) {
        new Thread(() -> {
            List<ProductWithDetails> searchResults = database.productDao().searchProductsWithDetailsByName("%" + query + "%");
            runOnUiThread(() -> {
                productList.clear();
                productList.addAll(searchResults);
                adapter.updateProducts(productList);
            });
        }).start();
    }

    private void setupButtons() {
        btnAddProduct.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddEditProductActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onProductClick(ProductWithDetails product) {
        Intent intent = new Intent(this, ProductDetailActivity.class);
        intent.putExtra("product_id", product.product.getId());
        startActivity(intent);
    }

    @Override
    public void onProductLongClick(ProductWithDetails product) {
        Intent intent = new Intent(this, AddEditProductActivity.class);
        intent.putExtra("product_id", product.product.getId());
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProducts();
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
}
