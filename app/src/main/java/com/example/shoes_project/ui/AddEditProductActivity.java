package com.example.shoes_project.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import com.example.shoes_project.R;
import com.example.shoes_project.data.AppDatabase;
import com.example.shoes_project.model.Brand;
import com.example.shoes_project.model.Category;
import com.example.shoes_project.model.Product;
import com.bumptech.glide.Glide;
import com.example.shoes_project.model.ProductViewModel;

import java.io.InputStream;
import java.io.FileOutputStream;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
public class AddEditProductActivity extends AppCompatActivity {

    private EditText etProductName, etPrice, etQuantity, etSellingPrice, etSize, etColor, etDescription;
    private ImageView ivProductPreview;
    private Spinner spinnerBrand, spinnerCategory;
    private Button btnAddImage, btnSave, btnCancel;

    private AppDatabase db;
    private ProductViewModel viewModel;
    private List<Brand> brandList = new ArrayList<>();
    private List<Category> categoryList = new ArrayList<>();

    private String imagePath;
    private Uri selectedImageUri;

    // Sử dụng ActivityResultLauncher cho việc chọn ảnh
    private ActivityResultLauncher<String> imagePickerLauncher;
    private ActivityResultLauncher<String> permissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_product);

        db = AppDatabase.getInstance(this);
        viewModel = new ViewModelProvider(this).get(ProductViewModel.class);

        initViews();
        observeSpinners();
        setupImagePickers();
        setupListeners();
    }

    private void initViews() {
        etProductName = findViewById(R.id.et_product_name);
        etPrice = findViewById(R.id.et_price);
        etQuantity = findViewById(R.id.et_quantity);
        etSellingPrice = findViewById(R.id.et_selling_price);
        etSize = findViewById(R.id.et_size);
        etColor = findViewById(R.id.et_color);
        etDescription = findViewById(R.id.et_description);
        ivProductPreview = findViewById(R.id.iv_product_preview);
        spinnerBrand = findViewById(R.id.spinner_brand);
        spinnerCategory = findViewById(R.id.spinner_category);
        btnAddImage = findViewById(R.id.btn_add_image);
        btnSave = findViewById(R.id.btn_save);
        btnCancel = findViewById(R.id.btn_cancel);
    }

    private void observeSpinners() {
        viewModel.getBrandList().observe(this, brands -> {
            brandList = brands;
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, getNamesFromBrands(brands));
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerBrand.setAdapter(adapter);
        });

        viewModel.getCategoryList().observe(this, categories -> {
            categoryList = categories;
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, getNamesFromCategories(categories));
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCategory.setAdapter(adapter);
        });
    }

    private List<String> getNamesFromBrands(List<Brand> list) {
        List<String> names = new ArrayList<>();
        for (Brand b : list) names.add(b.getName());
        return names;
    }

    private List<String> getNamesFromCategories(List<Category> list) {
        List<String> names = new ArrayList<>();
        for (Category c : list) names.add(c.getName());
        return names;
    }

    private void setupImagePickers() {
        // Launcher để chọn ảnh từ gallery
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selectedImageUri = uri;
                        saveImageToInternalStorage(uri);
                    }
                }
        );

        // Launcher để xin permission
        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                granted -> {
                    if (granted) {
                        openGallery();
                    } else {
                        Toast.makeText(this, "Permission denied. Cannot access gallery.", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void setupListeners() {
        btnAddImage.setOnClickListener(v -> {
            checkPermissionAndOpenGallery();
        });

        btnSave.setOnClickListener(v -> saveProduct());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void checkPermissionAndOpenGallery() {
        // Kiểm tra permission dựa trên version Android
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ sử dụng READ_MEDIA_IMAGES
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
            } else {
                openGallery();
            }
        } else {
            // Android 12 và thấp hơn sử dụng READ_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            } else {
                openGallery();
            }
        }
    }

    private void openGallery() {
        // Sử dụng GetContent contract để chọn ảnh
        imagePickerLauncher.launch("image/*");
    }

    private void saveImageToInternalStorage(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream != null) {
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();

                if (bitmap != null) {
                    // Create a unique filename
                    String fileName = "product_" + System.currentTimeMillis() + ".jpg";

                    // Save to internal storage in a shared directory
                    File imageDir = new File(getFilesDir(), "product_images");
                    if (!imageDir.exists()) {
                        imageDir.mkdirs();
                    }

                    File imageFile = new File(imageDir, fileName);

                    // Save bitmap to file
                    try (FileOutputStream out = new FileOutputStream(imageFile)) {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                        out.flush();
                    }

                    // Store relative path instead of absolute path
                    imagePath = "product_images/" + fileName;

                    // Display image in ImageView
                    Glide.with(this)
                            .load(imageFile) // Load from file object
                            .into(ivProductPreview);

                    Toast.makeText(this, "Image loaded successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Failed to decode image", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to load image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void saveProduct() {
        String name = etProductName.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String qtyStr = etQuantity.getText().toString().trim();
        String sellStr = etSellingPrice.getText().toString().trim();
        String sizeStr = etSize.getText().toString().trim();

        if (name.isEmpty() || priceStr.isEmpty() || qtyStr.isEmpty() || sellStr.isEmpty() || sizeStr.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double price = Double.parseDouble(priceStr);
            int quantity = Integer.parseInt(qtyStr);
            double sellingPrice = Double.parseDouble(sellStr);
            double size = Double.parseDouble(sizeStr);

            if (price <= 0 || quantity <= 0) {
                Toast.makeText(this, "Price and Quantity must be > 0", Toast.LENGTH_SHORT).show();
                return;
            }

            Product product = new Product();
            product.setProductName(name);
            product.setPrice(price);
            product.setQuantity(quantity);
            product.setSellingPrice((int) sellingPrice);
            product.setSize(size);
            product.setColor(etColor.getText().toString().trim());
            product.setDescription(etDescription.getText().toString().trim());
            product.setImageUrl(imagePath);

            if (brandList.isEmpty() || categoryList.isEmpty()) {
                Toast.makeText(this, "Please wait for brand/category to load", Toast.LENGTH_SHORT).show();
                return;
            }

            int brandIndex = spinnerBrand.getSelectedItemPosition();
            int categoryIndex = spinnerCategory.getSelectedItemPosition();

            if (brandIndex >= 0 && categoryIndex >= 0) {
                product.setBrandId(brandList.get(brandIndex).getId());
                product.setCategoryId(categoryList.get(categoryIndex).getId());

                // Lưu product vào database trong background thread
                new Thread(() -> {
                    db.productDao().insertProduct(product);
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Product saved successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }).start();
            } else {
                Toast.makeText(this, "Please select brand and category", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show();
        }
    }
}