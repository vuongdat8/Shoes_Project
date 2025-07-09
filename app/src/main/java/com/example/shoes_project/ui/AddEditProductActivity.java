package com.example.shoes_project.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import com.example.shoes_project.R;
import com.example.shoes_project.data.AppDatabase;
import com.example.shoes_project.model.Product;
import com.bumptech.glide.Glide;
import java.io.InputStream;
import java.io.FileOutputStream;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddEditProductActivity extends AppCompatActivity {
    private EditText etProductName, etBrand, etCategory, etPrice, etQuantity,
            etSellingPrice, etSize, etDescription, etColor;
    private Button btnSave, btnCancel, btnAddImage;
    private ImageView ivProductPreview;
    private AppDatabase database;
    private Product currentProduct;
    private boolean isEditMode = false;
    private String selectedImagePath = "";
    private Uri photoURI;

    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int STORAGE_PERMISSION_CODE = 101;

    // Activity Result Launchers
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_product);

        initViews();
        initDatabase();
        initActivityLaunchers();
        checkEditMode();
        setupButtons();
    }

    private void initViews() {
        etProductName = findViewById(R.id.et_product_name);
        etBrand = findViewById(R.id.et_brand);
        etCategory = findViewById(R.id.et_category);
        etPrice = findViewById(R.id.et_price);
        etQuantity = findViewById(R.id.et_quantity);
        etSellingPrice = findViewById(R.id.et_selling_price);
        etSize = findViewById(R.id.et_size);
        etDescription = findViewById(R.id.et_description);
        etColor = findViewById(R.id.et_color);
        btnSave = findViewById(R.id.btn_save);
        btnCancel = findViewById(R.id.btn_cancel);
        btnAddImage = findViewById(R.id.btn_add_image);
        ivProductPreview = findViewById(R.id.iv_product_preview);
    }

    private void initDatabase() {
        database = AppDatabase.getInstance(this);
    }

    private void checkEditMode() {
        int productId = getIntent().getIntExtra("product_id", -1);
        if (productId != -1) {
            isEditMode = true;
            setTitle("Edit Product");
            loadProductData(productId);
        } else {
            setTitle("Add Product");
        }
    }

    private void loadProductData(int productId) {
        new Thread(() -> {
            currentProduct = database.productDao().getProductById(productId);
            runOnUiThread(() -> {
                if (currentProduct != null) {
                    etProductName.setText(currentProduct.getProductName());
                    etBrand.setText(currentProduct.getBrand());
                    etCategory.setText(currentProduct.getCategoryName());
                    etPrice.setText(String.valueOf(currentProduct.getPrice()));
                    etQuantity.setText(String.valueOf(currentProduct.getQuantity()));
                    etSellingPrice.setText(String.valueOf(currentProduct.getSellingPrice()));
                    etSize.setText(String.valueOf(currentProduct.getSize()));
                    etDescription.setText(currentProduct.getDescription());
                    etColor.setText(currentProduct.getColor());

                    // Load existing image
                    if (currentProduct.getImageUrl() != null && !currentProduct.getImageUrl().isEmpty()) {
                        selectedImagePath = currentProduct.getImageUrl();
                        loadImageIntoView(currentProduct.getImageUrl());
                    }
                }
            });
        }).start();
    }

    private void loadImageIntoView(String imagePath) {
        if (imagePath != null && !imagePath.isEmpty()) {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                // Load từ file path
                Glide.with(this)
                        .load(imageFile)
                        .placeholder(R.drawable.ic_image_placeholder)
                        .error(R.drawable.ic_image_placeholder)
                        .into(ivProductPreview);
            } else if (imagePath.startsWith("http")) {
                // Load từ URL
                Glide.with(this)
                        .load(imagePath)
                        .placeholder(R.drawable.ic_image_placeholder)
                        .error(R.drawable.ic_image_placeholder)
                        .into(ivProductPreview);
            } else {
                // Fallback to placeholder
                ivProductPreview.setImageResource(R.drawable.ic_image_placeholder);
            }
        } else {
            ivProductPreview.setImageResource(R.drawable.ic_image_placeholder);
        }
    }

    private void setupButtons() {
        btnSave.setOnClickListener(v -> saveProduct());
        btnCancel.setOnClickListener(v -> finish());
        btnAddImage.setOnClickListener(v -> showImageSourceDialog());
    }

    private void showImageSourceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image Source");
        builder.setItems(new String[]{"Camera", "Gallery"}, (dialog, which) -> {
            if (which == 0) {
                openCamera();
            } else {
                openGallery();
            }
        });
        builder.show();
    }

    private void openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        } else {
            dispatchTakePictureIntent();
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Tạo file để lưu ảnh
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(this, "Error creating image file", Toast.LENGTH_SHORT).show();
                return;
            }

            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        getPackageName() + ".fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                cameraLauncher.launch(takePictureIntent);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    // Cập nhật camera launcher
    private void initActivityLaunchers() {
        // Camera launcher
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Bundle extras = result.getData().getExtras();
                        if (extras != null) {
                            android.graphics.Bitmap imageBitmap = (android.graphics.Bitmap) extras.get("data");
                            if (imageBitmap != null) {
                                ivProductPreview.setImageBitmap(imageBitmap);
                                // Save image to internal storage and get path
                                selectedImagePath = saveImageToInternalStorage(imageBitmap);
                            }
                        }
                    }
                });

        // Gallery launcher - CẬP NHẬT QUAN TRỌNG
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            // Hiển thị ảnh ngay lập tức
                            ivProductPreview.setImageURI(imageUri);

                            // QUAN TRỌNG: Copy ảnh từ URI sang internal storage
                            copyImageFromUriToInternalStorage(imageUri);
                        }
                    }
                });
    }

    // Cải thiện permission handling
    private void openGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ chỉ cần READ_MEDIA_IMAGES
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES}, STORAGE_PERMISSION_CODE);
            } else {
                launchGallery();
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
            } else {
                launchGallery();
            }
        }
    }

    private void launchGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        galleryLauncher.launch(galleryIntent);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "Storage permission is required", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void copyImageFromUriToInternalStorage(Uri imageUri) {
        new Thread(() -> {
            try {
                // Đọc ảnh từ URI
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                if (inputStream != null) {
                    // Tạo Bitmap từ InputStream
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    inputStream.close();

                    if (bitmap != null) {
                        // Lưu Bitmap vào internal storage
                        selectedImagePath = saveImageToInternalStorage(bitmap);

                        runOnUiThread(() -> {
                            // Cập nhật ImageView với ảnh đã lưu
                            loadImageIntoView(selectedImagePath);
                        });
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error processing image", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }
    private String saveImageToInternalStorage(Bitmap bitmap) {
        try {
            // Tạo filename unique
            String filename = "product_" + System.currentTimeMillis() + ".jpg";

            // Mở FileOutputStream
            FileOutputStream fos = openFileOutput(filename, MODE_PRIVATE);

            // Nén và lưu bitmap
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();

            // Trả về absolute path
            return new File(getFilesDir(), filename).getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private void saveProduct() {
        if (validateInput()) {
            new Thread(() -> {
                if (isEditMode) {
                    updateProduct();
                } else {
                    createProduct();
                }
            }).start();
        }
    }

    private boolean validateInput() {
        if (etProductName.getText().toString().trim().isEmpty()) {
            etProductName.setError("Product name is required");
            return false;
        }
        if (etBrand.getText().toString().trim().isEmpty()) {
            etBrand.setError("Brand is required");
            return false;
        }
        if (etPrice.getText().toString().trim().isEmpty()) {
            etPrice.setError("Price is required");
            return false;
        }
        return true;
    }

    private void createProduct() {
        Product product = new Product(
                etProductName.getText().toString().trim(),
                etBrand.getText().toString().trim(),
                etCategory.getText().toString().trim(),
                Double.parseDouble(etPrice.getText().toString().trim()),
                Integer.parseInt(etQuantity.getText().toString().trim()),
                Integer.parseInt(etSellingPrice.getText().toString().trim()),
                selectedImagePath, // Use selected image path instead of URL
                Double.parseDouble(etSize.getText().toString().trim()),
                etDescription.getText().toString().trim(),
                etColor.getText().toString().trim()
        );

        database.productDao().insertProduct(product);

        runOnUiThread(() -> {
            Toast.makeText(this, "Product added successfully", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void updateProduct() {
        currentProduct.setProductName(etProductName.getText().toString().trim());
        currentProduct.setBrand(etBrand.getText().toString().trim());
        currentProduct.setCategoryName(etCategory.getText().toString().trim());
        currentProduct.setPrice(Double.parseDouble(etPrice.getText().toString().trim()));
        currentProduct.setQuantity(Integer.parseInt(etQuantity.getText().toString().trim()));
        currentProduct.setSellingPrice(Integer.parseInt(etSellingPrice.getText().toString().trim()));
        currentProduct.setImageUrl(selectedImagePath); // Use selected image path
        currentProduct.setSize(Double.parseDouble(etSize.getText().toString().trim()));
        currentProduct.setDescription(etDescription.getText().toString().trim());
        currentProduct.setColor(etColor.getText().toString().trim());

        database.productDao().updateProduct(currentProduct);

        runOnUiThread(() -> {
            Toast.makeText(this, "Product updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

}