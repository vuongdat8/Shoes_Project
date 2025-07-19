package com.example.shoes_project.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.example.shoes_project.R;
import com.example.shoes_project.data.AppDatabase;
import com.example.shoes_project.model.Product;
import com.example.shoes_project.model.Brand;
import com.example.shoes_project.model.Category;
import com.google.android.material.textfield.TextInputEditText;
import com.bumptech.glide.Glide;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.UUID;

public class EditProductActivity extends AppCompatActivity {
    private TextInputEditText etProductName, etBrand, etCategory, etPrice,
            etSellingPrice, etQuantity, etSize, etColor, etDescription;
    private ImageView ivProductPreview;
    private Button btnSave, btnCancel, btnChangeImage;
    private AppDatabase database;
    private Product currentProduct;
    private int productId;
    private String selectedImagePath; // Lưu đường dẫn ảnh mới được chọn
    private boolean isImageChanged = false; // Kiểm tra xem ảnh có được thay đổi không

    // ActivityResultLauncher để chọn ảnh
    private ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        handleImageSelection(imageUri);
                    }
                }
            });

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
        btnChangeImage = findViewById(R.id.btn_change_image); // Thêm button này trong layout
    }

    private void initDatabase() {
        database = AppDatabase.getInstance(this);
    }

    private void loadProductData() {
        productId = getIntent().getIntExtra("product_id", -1);
        if (productId != -1) {
            new Thread(() -> {
                currentProduct = database.productDao().getProductById(productId);

                // Lấy thông tin Brand và Category
                Brand brand = database.brandDao().getBrandById(currentProduct.getBrandId());
                Category category = database.categoryDao().getCategoryById(currentProduct.getCategoryId());

                runOnUiThread(() -> {
                    if (currentProduct != null) {
                        populateFields(currentProduct, brand, category);
                        selectedImagePath = currentProduct.getImageUrl(); // Khởi tạo với ảnh hiện tại
                    }
                });
            }).start();
        }
    }

    private void populateFields(Product product, Brand brand, Category category) {
        etProductName.setText(product.getProductName());
        etBrand.setText(brand != null ? brand.getName() : "");
        etCategory.setText(category != null ? category.getName() : "");
        etPrice.setText(String.valueOf(product.getPrice()));
        etSellingPrice.setText(String.valueOf(product.getSellingPrice()));
        etQuantity.setText(String.valueOf(product.getQuantity()));
        etSize.setText(String.valueOf(product.getSize()));
        etColor.setText(product.getColor());
        etDescription.setText(product.getDescription());

        // Load image using Glide
        loadProductImage(product.getImageUrl());
    }

    private void loadProductImage(String imagePath) {
        if (imagePath != null && !imagePath.isEmpty()) {
            if (imagePath.startsWith("http") || imagePath.startsWith("https")) {
                // Load image from URL
                Glide.with(this)
                        .load(imagePath)
                        .placeholder(R.drawable.ic_image_placeholder)
                        .error(R.drawable.ic_image_placeholder)
                        .into(ivProductPreview);
            } else if (imagePath.startsWith("content://")) {
                // Load image from Content URI
                Glide.with(this)
                        .load(imagePath)
                        .placeholder(R.drawable.ic_image_placeholder)
                        .error(R.drawable.ic_image_placeholder)
                        .into(ivProductPreview);
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

                if (imageFile.exists()) {
                    Glide.with(this)
                            .load(imageFile)
                            .placeholder(R.drawable.ic_image_placeholder)
                            .error(R.drawable.ic_image_placeholder)
                            .into(ivProductPreview);
                } else {
                    // File doesn't exist, show placeholder
                    ivProductPreview.setImageResource(R.drawable.ic_image_placeholder);
                }
            }
        } else {
            // No image path, show placeholder
            ivProductPreview.setImageResource(R.drawable.ic_image_placeholder);
        }
    }

    private void setupButtonListeners() {
        btnSave.setOnClickListener(v -> saveProduct());
        btnCancel.setOnClickListener(v -> finish());

        // Thêm listener cho button thay đổi ảnh
        btnChangeImage.setOnClickListener(v -> openImagePicker());

        // Hoặc có thể click vào ảnh để thay đổi
        ivProductPreview.setOnClickListener(v -> openImagePicker());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void handleImageSelection(Uri imageUri) {
        try {
            // Hiển thị ảnh ngay lập tức
            Glide.with(this)
                    .load(imageUri)
                    .placeholder(R.drawable.ic_image_placeholder)
                    .error(R.drawable.ic_image_placeholder)
                    .into(ivProductPreview);

            // Lưu ảnh vào internal storage
            String fileName = "product_" + UUID.randomUUID().toString() + ".jpg";
            File imageFile = new File(getFilesDir(), fileName);

            // Copy file từ Uri vào internal storage
            try (InputStream inputStream = getContentResolver().openInputStream(imageUri);
                 FileOutputStream outputStream = new FileOutputStream(imageFile)) {

                if (inputStream != null) {
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = inputStream.read(buffer)) > 0) {
                        outputStream.write(buffer, 0, length);
                    }
                }
            }

            // Cập nhật đường dẫn ảnh mới
            selectedImagePath = fileName; // Lưu relative path
            isImageChanged = true;

            Toast.makeText(this, "Image selected successfully", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(this, "Error selecting image", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
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

        double price;
        try {
            price = Double.parseDouble(priceStr);
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

        double sellingPrice;
        try {
            sellingPrice = Double.parseDouble(sellingPriceStr);
            if (sellingPrice < 0) {
                etSellingPrice.setError("Selling price must be positive");
                etSellingPrice.requestFocus();
                return false;
            }

            if (sellingPrice > price) {
                etSellingPrice.setError("Selling price should not exceed original price");
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
        currentProduct.setCategoryId(Integer.parseInt(etCategory.getText().toString().trim()));
        currentProduct.setPrice(Double.parseDouble(etPrice.getText().toString().trim()));
        currentProduct.setSellingPrice(Double.parseDouble(etSellingPrice.getText().toString().trim())); // Sửa lại thành Double
        currentProduct.setQuantity(Integer.parseInt(etQuantity.getText().toString().trim()));
        currentProduct.setSize(Double.parseDouble(etSize.getText().toString().trim()));
        currentProduct.setColor(etColor.getText().toString().trim());
        currentProduct.setDescription(etDescription.getText().toString().trim());

        // Cập nhật đường dẫn ảnh nếu có thay đổi
        if (isImageChanged) {
            currentProduct.setImageUrl(selectedImagePath);
        }
    }
}