package com.example.shoes_project.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.shoes_project.R;
import com.example.shoes_project.data.AppDatabase;
import com.example.shoes_project.model.Product;
import com.google.android.material.textfield.TextInputEditText;

public class EditProductActivity extends AppCompatActivity {
    private TextInputEditText etProductName, etBrand, etCategory, etPrice,
            etSellingPrice, etQuantity, etSize, etColor, etDescription;
    private ImageView ivProductPreview;
    private Button btnSave, btnCancel;
    private AppDatabase database;
    private Product currentProduct;
    private int productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        initViews();
        initDatabase();
        loadProductData();
        setupButtonListeners();
    }

    private void initViews() {
        etProductName = findViewById(R.id.et_product_name);
        etBrand = findViewById(R.id.et_brand);
        etCategory = findViewById(R.id.et_category);
        etPrice = findViewById(R.id.et_price);
        etSellingPrice = findViewById(R.id.et_selling_price);
        etQuantity = findViewById(R.id.et_quantity);
        etSize = findViewById(R.id.et_size);
        etColor = findViewById(R.id.et_color);
        etDescription = findViewById(R.id.et_description);
        ivProductPreview = findViewById(R.id.iv_product_preview);
        btnSave = findViewById(R.id.btn_save);
        btnCancel = findViewById(R.id.btn_cancel);
    }

    private void initDatabase() {
        database = AppDatabase.getInstance(this);
    }

    private void loadProductData() {
        productId = getIntent().getIntExtra("product_id", -1);
        if (productId != -1) {
            new Thread(() -> {
                currentProduct = database.productDao().getProductById(productId);
                runOnUiThread(() -> {
                    if (currentProduct != null) {
                        populateFields(currentProduct);
                    }
                });
            }).start();
        }
    }

    private void populateFields(Product product) {
        etProductName.setText(product.getProductName());
        etBrand.setText(String.valueOf(product.getBrandId())); // hoặc để trống nếu muốn sửa lại sau
        etCategory.setText(String.valueOf(product.getCategoryId()));
        etPrice.setText(String.valueOf(product.getPrice()));
        etSellingPrice.setText(String.valueOf(product.getSellingPrice()));
        etQuantity.setText(String.valueOf(product.getQuantity()));
        etSize.setText(String.valueOf(product.getSize()));
        etColor.setText(product.getColor());
        etDescription.setText(product.getDescription());

        // Load image using Glide or Picasso if you have image URL
        // Glide.with(this).load(product.getImageUrl()).into(ivProductPreview);
    }

    private void setupButtonListeners() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProduct();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void saveProduct() {
        if (validateInputs()) {
            updateProductFromInputs();

            new Thread(() -> {
                try {
                    database.productDao().updateProduct(currentProduct);
                    runOnUiThread(() -> {
                        Toast.makeText(EditProductActivity.this, "Product updated successfully", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    });
                } catch (Exception e) {
                    runOnUiThread(() -> {
                        Toast.makeText(EditProductActivity.this, "Error updating product", Toast.LENGTH_SHORT).show();
                    });
                }
            }).start();
        }
    }

    private boolean validateInputs() {
        // Validate Product Name
        if (TextUtils.isEmpty(etProductName.getText().toString().trim())) {
            etProductName.setError("Product name is required");
            etProductName.requestFocus();
            return false;
        }

        // Validate Brand
        if (TextUtils.isEmpty(etBrand.getText().toString().trim())) {
            etBrand.setError("Brand is required");
            etBrand.requestFocus();
            return false;
        }

        // Validate Category
        if (TextUtils.isEmpty(etCategory.getText().toString().trim())) {
            etCategory.setError("Category is required");
            etCategory.requestFocus();
            return false;
        }

        // Validate Price
        String priceStr = etPrice.getText().toString().trim();
        if (TextUtils.isEmpty(priceStr)) {
            etPrice.setError("Price is required");
            etPrice.requestFocus();
            return false;
        }
        try {
            double price = Double.parseDouble(priceStr);
            if (price < 0) {
                etPrice.setError("Price must be positive");
                etPrice.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            etPrice.setError("Invalid price format");
            etPrice.requestFocus();
            return false;
        }

        // Validate Selling Price
        String sellingPriceStr = etSellingPrice.getText().toString().trim();
        if (TextUtils.isEmpty(sellingPriceStr)) {
            etSellingPrice.setError("Selling price is required");
            etSellingPrice.requestFocus();
            return false;
        }
        try {
            int sellingPrice = Integer.parseInt(sellingPriceStr);
            if (sellingPrice < 0) {
                etSellingPrice.setError("Selling price must be positive");
                etSellingPrice.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            etSellingPrice.setError("Invalid selling price format");
            etSellingPrice.requestFocus();
            return false;
        }

        // Validate Quantity
        String quantityStr = etQuantity.getText().toString().trim();
        if (TextUtils.isEmpty(quantityStr)) {
            etQuantity.setError("Quantity is required");
            etQuantity.requestFocus();
            return false;
        }
        try {
            int quantity = Integer.parseInt(quantityStr);
            if (quantity < 0) {
                etQuantity.setError("Quantity must be positive");
                etQuantity.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            etQuantity.setError("Invalid quantity format");
            etQuantity.requestFocus();
            return false;
        }

        // Validate Size
        String sizeStr = etSize.getText().toString().trim();
        if (TextUtils.isEmpty(sizeStr)) {
            etSize.setError("Size is required");
            etSize.requestFocus();
            return false;
        }
        try {
            double size = Double.parseDouble(sizeStr);
            if (size <= 0) {
                etSize.setError("Size must be positive");
                etSize.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            etSize.setError("Invalid size format");
            etSize.requestFocus();
            return false;
        }

        // Validate Color
        if (TextUtils.isEmpty(etColor.getText().toString().trim())) {
            etColor.setError("Color is required");
            etColor.requestFocus();
            return false;
        }

        return true;
    }

    private void updateProductFromInputs() {
        currentProduct.setProductName(etProductName.getText().toString().trim());
        currentProduct.setBrandId(Integer.parseInt(etBrand.getText().toString().trim()));
        currentProduct.setCategoryId(Integer.parseInt(etCategory.getText().toString().trim())); // ✅
        currentProduct.setPrice(Double.parseDouble(etPrice.getText().toString().trim()));
        currentProduct.setSellingPrice(Integer.parseInt(etSellingPrice.getText().toString().trim()));
        currentProduct.setQuantity(Integer.parseInt(etQuantity.getText().toString().trim()));
        currentProduct.setSize(Double.parseDouble(etSize.getText().toString().trim()));
        currentProduct.setColor(etColor.getText().toString().trim());
        currentProduct.setDescription(etDescription.getText().toString().trim());
    }
}