package com.example.shoes_project.ui.admin;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shoes_project.R;
import com.example.shoes_project.adapter.CategoryAdapter;
import com.example.shoes_project.data.AppDatabase;
import com.example.shoes_project.model.Brand; // <<<<<<< THÊM: Import model Brand
import com.example.shoes_project.model.Category;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Category_Admin_Activity extends AppCompatActivity {

    public static final String EXTRA_BRAND_ID = "extra_brand_id";

    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    private AppDatabase database;
    private Button addCategoryButton;
    private TextView brandNameHeaderTextView; // <<<<<<< THÊM: Biến cho TextView header

    private int currentBrandId = -1;

    private Uri selectedImageUri;
    private ImageView dialogImageView;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    private final ActivityResultLauncher<String> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    if (dialogImageView != null) {
                        Glide.with(this).load(selectedImageUri).into(dialogImageView);
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_admin);

        // Lấy brandId từ Intent
        currentBrandId = getIntent().getIntExtra(EXTRA_BRAND_ID, -1);
        if (currentBrandId == -1) {
            Toast.makeText(this, "Không tìm thấy thông tin hãng!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Khởi tạo các view
        recyclerView = findViewById(R.id.recycler_view_category);
        addCategoryButton = findViewById(R.id.button_add_category);
        brandNameHeaderTextView = findViewById(R.id.textView_brand_name_header); // <<<<<<< THÊM: Ánh xạ TextView

        // Cấu hình RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        database = AppDatabase.getInstance(this);

        adapter = new CategoryAdapter(new ArrayList<>(), new CategoryAdapter.CategoryActionListener() {
            @Override
            public void onEditCategory(Category category) {
                showEditCategoryDialog(category);
            }

            @Override
            public void onDeleteCategory(Category category) {
                showDeleteConfirmationDialog(category);
            }
        });

        recyclerView.setAdapter(adapter);

        // Tải dữ liệu
        loadBrandName(); // <<<<<<< THÊM: Gọi hàm tải tên Brand
        loadCategoriesForBrand();

        addCategoryButton.setOnClickListener(v -> showAddCategoryDialog());
    }

    /**
     * Tải tên của Brand hiện tại và hiển thị nó trên header.
     */
    private void loadBrandName() { // <<<<<<< THÊM: Phương thức mới
        new Thread(() -> {
            // Giả sử bạn có brandDao() trong AppDatabase và getBrandById() trong BrandDAO
            Brand brand = database.brandDao().getBrandById(currentBrandId);
            if (brand != null) {
                runOnUiThread(() -> {
                    brandNameHeaderTextView.setText("Quản lý danh mục cho hãng: " + brand.getName());
                });
            }
        }).start();
    }

    /**
     * Tải danh sách các Category thuộc về Brand hiện tại.
     */
    private void loadCategoriesForBrand() {
        new Thread(() -> {
            List<Category> categories = database.categoryDao().getCategoriesForBrand(currentBrandId);
            runOnUiThread(() -> {
                adapter.setCategories(categories);
            });
        }).start();
    }

    private void showAddCategoryDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_category, null);
        EditText nameEditText = dialogView.findViewById(R.id.editTextCategoryName);
        EditText descriptionEditText = dialogView.findViewById(R.id.editTextCategoryDescription);
        dialogImageView = dialogView.findViewById(R.id.imageViewSelectedCategory);
        Button selectImageButton = dialogView.findViewById(R.id.buttonSelectImage);
        SwitchMaterial activeSwitch = dialogView.findViewById(R.id.switchCategoryActive);

        selectedImageUri = null;
        dialogImageView.setImageResource(R.drawable.a1);
        selectImageButton.setOnClickListener(v -> pickImageLauncher.launch("image/*"));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thêm danh mục mới")
                .setView(dialogView)
                .setPositiveButton("Thêm", (dialog, which) -> {
                    String name = nameEditText.getText().toString().trim();
                    String description = descriptionEditText.getText().toString().trim();
                    boolean isActive = activeSwitch.isChecked();

                    if (!name.isEmpty() && !description.isEmpty() && selectedImageUri != null) {
                        String imageUrl = selectedImageUri.toString();
                        String currentTime = LocalDateTime.now().format(formatter);

                        Category newCategory = new Category(currentBrandId, name, description, imageUrl, isActive);
                        newCategory.setCreatedAt(currentTime);
                        newCategory.setUpdatedAt(currentTime);

                        new Thread(() -> {
                            database.categoryDao().insert(newCategory);
                            // Sau khi insert, tải lại danh sách trên UI thread
                            runOnUiThread(this::loadCategoriesForBrand);
                            runOnUiThread(() -> Toast.makeText(Category_Admin_Activity.this, "Danh mục đã được thêm!", Toast.LENGTH_SHORT).show());
                        }).start();
                    } else {
                        Toast.makeText(Category_Admin_Activity.this, "Vui lòng điền đủ thông tin và chọn ảnh!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void showEditCategoryDialog(Category category) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_category, null);
        EditText nameEditText = dialogView.findViewById(R.id.editTextCategoryName);
        EditText descriptionEditText = dialogView.findViewById(R.id.editTextCategoryDescription);
        dialogImageView = dialogView.findViewById(R.id.imageViewSelectedCategory);
        Button selectImageButton = dialogView.findViewById(R.id.buttonSelectImage);
        SwitchMaterial activeSwitch = dialogView.findViewById(R.id.switchCategoryActive);

        nameEditText.setText(category.getName());
        descriptionEditText.setText(category.getDescription());
        activeSwitch.setChecked(category.isActive());

        if (category.getImageUrl() != null && !category.getImageUrl().isEmpty()) {
            selectedImageUri = Uri.parse(category.getImageUrl());
            Glide.with(this).load(selectedImageUri).into(dialogImageView);
        } else {
            dialogImageView.setImageResource(R.drawable.a2);
            selectedImageUri = null;
        }

        selectImageButton.setOnClickListener(v -> pickImageLauncher.launch("image/*"));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chỉnh sửa danh mục")
                .setView(dialogView)
                .setPositiveButton("Cập nhật", (dialog, which) -> {
                    String name = nameEditText.getText().toString().trim();
                    String description = descriptionEditText.getText().toString().trim();
                    boolean isActive = activeSwitch.isChecked();

                    if (!name.isEmpty() && !description.isEmpty() && selectedImageUri != null) {
                        String imageUrl = selectedImageUri.toString();
                        String currentTime = LocalDateTime.now().format(formatter);

                        category.setName(name);
                        category.setDescription(description);
                        category.setImageUrl(imageUrl);
                        category.setActive(isActive);
                        category.setUpdatedAt(currentTime);

                        new Thread(() -> {
                            database.categoryDao().update(category);
                            runOnUiThread(this::loadCategoriesForBrand);
                            runOnUiThread(() -> Toast.makeText(Category_Admin_Activity.this, "Danh mục đã được cập nhật!", Toast.LENGTH_SHORT).show());
                        }).start();
                    } else {
                        Toast.makeText(Category_Admin_Activity.this, "Vui lòng điền đủ thông tin và chọn ảnh!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void deleteCategory(Category category) {
        new Thread(() -> {
            database.categoryDao().delete(category);
            runOnUiThread(this::loadCategoriesForBrand);
            runOnUiThread(() -> Toast.makeText(Category_Admin_Activity.this, "Danh mục đã được xóa!", Toast.LENGTH_SHORT).show());
        }).start();
    }

    private void showDeleteConfirmationDialog(Category category) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_delete_confirmation, null);
        TextView dialogTitle = dialogView.findViewById(R.id.dialog_title);
        TextView dialogMessage = dialogView.findViewById(R.id.dialog_message);
        Button buttonCancel = dialogView.findViewById(R.id.button_cancel_delete);
        Button buttonConfirm = dialogView.findViewById(R.id.button_confirm_delete);
        dialogTitle.setText("Xác nhận xóa danh mục");
        dialogMessage.setText("Bạn có chắc chắn muốn xóa danh mục: \"" + category.getName() + "\" không?");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();
        buttonCancel.setOnClickListener(v -> dialog.dismiss());
        buttonConfirm.setOnClickListener(v -> {
            deleteCategory(category);
            dialog.dismiss();
        });
        dialog.show();
    }
}