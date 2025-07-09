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
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shoes_project.R;
import com.example.shoes_project.adapter.CategoryAdapter;
import com.example.shoes_project.data.AppDatabase;
import com.example.shoes_project.model.Category;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Category_Admin_Activity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    private AppDatabase database;
    private Button addCategoryButton;

    // To store the selected image URI temporarily for the dialog
    private Uri selectedImageUri;
    // Reference to the ImageView inside the dialog to display the chosen image
    private ImageView dialogImageView;

    // Define a DateTimeFormatter for your string representation
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    // ActivityResultLauncher for selecting image from gallery
    private final ActivityResultLauncher<String> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(), // Contract to get content (e.g., "image/*")
            uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    // Display the selected image in the ImageView within the dialog
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

        recyclerView = findViewById(R.id.recycler_view_category);
        addCategoryButton = findViewById(R.id.button_add_category);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Database
        database = AppDatabase.getInstance(this);

        // Initialize Adapter with empty list and set actions for edit and delete
        adapter = new CategoryAdapter(new ArrayList<>(), new CategoryAdapter.CategoryActionListener() {
            @Override
            public void onEditCategory(Category category) {
                showEditCategoryDialog(category);
            }

            @Override
            public void onDeleteCategory(Category category) {
                // Show custom confirmation dialog before deleting
                showDeleteConfirmationDialog(category);
            }
        });

        recyclerView.setAdapter(adapter);

        // Observe live data for Category list
        database.categoryDao().getAllCategories().observe(this, new Observer<List<Category>>() {
            @Override
            public void onChanged(List<Category> categories) {
                adapter.setCategories(categories);  // Update data from DB to adapter
            }
        });

        // Add new Category on Button click
        addCategoryButton.setOnClickListener(v -> showAddCategoryDialog());
    }

    // Show Add Category Dialog
    private void showAddCategoryDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_category, null);
        EditText nameEditText = dialogView.findViewById(R.id.editTextCategoryName);
        EditText descriptionEditText = dialogView.findViewById(R.id.editTextCategoryDescription);
        // Corrected: Removed reference to editTextCategoryImageUrl
        // EditText imageUrlEditText = dialogView.findViewById(R.id.editTextCategoryImageUrl); // THIS WAS THE ERROR

        // Corrected: Get references to the new ImageView and Button for image selection
        dialogImageView = dialogView.findViewById(R.id.imageViewSelectedCategory);
        Button selectImageButton = dialogView.findViewById(R.id.buttonSelectImage);
        SwitchMaterial activeSwitch = dialogView.findViewById(R.id.switchCategoryActive);

        // Reset selectedImageUri and set placeholder image for a new entry
        selectedImageUri = null;
        dialogImageView.setImageResource(R.drawable.a1);

        // Set listener for the "Select Image" button
        selectImageButton.setOnClickListener(v -> pickImageLauncher.launch("image/*"));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thêm danh mục mới")
                .setView(dialogView)
                .setPositiveButton("Thêm", (dialog, which) -> {
                    String name = nameEditText.getText().toString().trim();
                    String description = descriptionEditText.getText().toString().trim();
                    boolean isActive = activeSwitch.isChecked();

                    // Check if an image has been selected
                    if (!name.isEmpty() && !description.isEmpty() && selectedImageUri != null) {
                        // Store the URI string. In a production app, you might copy this URI's content
                        // to app-specific storage or upload it to a server.
                        String imageUrl = selectedImageUri.toString();

                        String currentTime = LocalDateTime.now().format(formatter);

                        Category newCategory = new Category(name, description, imageUrl, isActive);
                        newCategory.setCreatedAt(currentTime); // Set creation timestamp
                        newCategory.setUpdatedAt(currentTime); // Set update timestamp initially

                        new Thread(() -> {
                            database.categoryDao().insert(newCategory);
                            runOnUiThread(() -> Toast.makeText(Category_Admin_Activity.this, "Danh mục đã được thêm thành công!", Toast.LENGTH_SHORT).show());
                        }).start();
                    } else {
                        Toast.makeText(Category_Admin_Activity.this, "Vui lòng điền đầy đủ Tên, Mô tả và chọn ảnh!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    // Show Edit Category Dialog
    private void showEditCategoryDialog(Category category) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_category, null);
        EditText nameEditText = dialogView.findViewById(R.id.editTextCategoryName);
        EditText descriptionEditText = dialogView.findViewById(R.id.editTextCategoryDescription);
        // Corrected: Removed reference to editTextCategoryImageUrl
        // EditText imageUrlEditText = dialogView.findViewById(R.id.editTextCategoryImageUrl); // THIS WAS THE ERROR

        // Corrected: Get references to the new ImageView and Button for image selection
        dialogImageView = dialogView.findViewById(R.id.imageViewSelectedCategory);
        Button selectImageButton = dialogView.findViewById(R.id.buttonSelectImage);
        SwitchMaterial activeSwitch = dialogView.findViewById(R.id.switchCategoryActive);

        // Populate existing category data
        nameEditText.setText(category.getName());
        descriptionEditText.setText(category.getDescription());
        activeSwitch.setChecked(category.isActive());

        // Load existing image if available, otherwise show placeholder
        if (category.getImageUrl() != null && !category.getImageUrl().isEmpty()) {
            selectedImageUri = Uri.parse(category.getImageUrl()); // Set current image URI
            Glide.with(this).load(selectedImageUri).into(dialogImageView);
        } else {
            dialogImageView.setImageResource(R.drawable.a2);
            selectedImageUri = null; // No existing image
        }

        // Set listener for the "Select Image" button
        selectImageButton.setOnClickListener(v -> pickImageLauncher.launch("image/*"));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chỉnh sửa danh mục")
                .setView(dialogView)
                .setPositiveButton("Cập nhật", (dialog, which) -> {
                    String name = nameEditText.getText().toString().trim();
                    String description = descriptionEditText.getText().toString().trim();
                    boolean isActive = activeSwitch.isChecked();

                    // Check if an image has been selected (or already existed)
                    if (!name.isEmpty() && !description.isEmpty() && selectedImageUri != null) {
                        String imageUrl = selectedImageUri.toString(); // Use the currently selected/existing image URI

                        String currentTime = LocalDateTime.now().format(formatter);

                        category.setName(name);
                        category.setDescription(description);
                        category.setImageUrl(imageUrl); // Update image URL
                        category.setActive(isActive);
                        category.setUpdatedAt(currentTime); // Update only updated timestamp

                        new Thread(() -> {
                            database.categoryDao().update(category);
                            runOnUiThread(() -> Toast.makeText(Category_Admin_Activity.this, "Danh mục đã được cập nhật thành công!", Toast.LENGTH_SHORT).show());
                        }).start();
                    } else {
                        Toast.makeText(Category_Admin_Activity.this, "Vui lòng điền đầy đủ Tên, Mô tả và chọn ảnh!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    // New method to show custom delete confirmation dialog
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

    // Delete Category
    private void deleteCategory(Category category) {
        new Thread(() -> {
            database.categoryDao().delete(category);
            runOnUiThread(() -> Toast.makeText(Category_Admin_Activity.this, "Danh mục đã được xóa!", Toast.LENGTH_SHORT).show());
        }).start();
    }
}