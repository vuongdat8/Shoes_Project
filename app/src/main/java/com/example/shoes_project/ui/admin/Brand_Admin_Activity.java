package com.example.shoes_project.ui.admin;


import android.content.Intent;
import android.net.Uri;
import android.os.Build;
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
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shoes_project.R;
import com.example.shoes_project.adapter.BrandAdapter;
import com.example.shoes_project.data.AppDatabase;
import com.example.shoes_project.model.Brand;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@RequiresApi(api = Build.VERSION_CODES.O)
public class Brand_Admin_Activity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BrandAdapter adapter;
    private AppDatabase database;
    private Button addBrandButton;

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
        setContentView(R.layout.activity_brand_admin);

        recyclerView = findViewById(R.id.recycler_view_brand);
        addBrandButton = findViewById(R.id.button_add_brand);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        database = AppDatabase.getInstance(this);

        adapter = new BrandAdapter(new ArrayList<>(), new BrandAdapter.BrandActionListener() {
            @Override
            public void onEditBrand(Brand brand) {
                showEditBrandDialog(brand);
            }

            @Override
            public void onDeleteBrand(Brand brand) {
                showDeleteConfirmationDialog(brand);
            }
        });

        recyclerView.setAdapter(adapter);

        database.brandDao().getAllBrand().observe(this, new Observer<List<Brand>>() {
            @Override
            public void onChanged(List<Brand> brands) {
                adapter.setBrands(brands);
            }
        });

        addBrandButton.setOnClickListener(v -> showAddBrandDialog());
    }

    private void showAddBrandDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_brand, null);
        EditText nameEditText = dialogView.findViewById(R.id.editTextBrandName);
        EditText descriptionEditText = dialogView.findViewById(R.id.editTextBrandDescription);
        dialogImageView = dialogView.findViewById(R.id.imageViewSelectedBrand);
        Button selectImageButton = dialogView.findViewById(R.id.buttonSelectImage);
        SwitchMaterial activeSwitch = dialogView.findViewById(R.id.switchBrandActive);

        selectedImageUri = null;
        dialogImageView.setImageResource(R.drawable.a1);

        selectImageButton.setOnClickListener(v -> pickImageLauncher.launch("image/*"));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thêm thương hiệu mới")
                .setView(dialogView)
                .setPositiveButton("Thêm", (dialog, which) -> {
                    String name = nameEditText.getText().toString().trim();
                    String description = descriptionEditText.getText().toString().trim();
                    boolean isActive = activeSwitch.isChecked();

                    if (!name.isEmpty() && !description.isEmpty() && selectedImageUri != null) {
                        String imageUrl = selectedImageUri.toString();
                        String currentTime = LocalDateTime.now().format(formatter);

                        Brand newBrand = new Brand(name, description, imageUrl, isActive);                        newBrand.setCreatedAt(currentTime);
                        newBrand.setUpdatedAt(currentTime);

                        new Thread(() -> {
                            database.brandDao().insert(newBrand);
                            runOnUiThread(() -> Toast.makeText(this, "Thêm thương hiệu thành công!", Toast.LENGTH_SHORT).show());
                        }).start();
                    } else {
                        Toast.makeText(this, "Vui lòng nhập đủ thông tin và chọn ảnh!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    private void showEditBrandDialog(Brand brand) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_brand, null);
        EditText nameEditText = dialogView.findViewById(R.id.editTextBrandName);
        EditText descriptionEditText = dialogView.findViewById(R.id.editTextBrandDescription);
        dialogImageView = dialogView.findViewById(R.id.imageViewSelectedBrand);
        Button selectImageButton = dialogView.findViewById(R.id.buttonSelectImage);
        SwitchMaterial activeSwitch = dialogView.findViewById(R.id.switchBrandActive);

        nameEditText.setText(brand.getName());
        descriptionEditText.setText(brand.getDescription());
        activeSwitch.setChecked(brand.isActive());

        if (brand.getImageUrl() != null && !brand.getImageUrl().isEmpty()) {
            selectedImageUri = Uri.parse(brand.getImageUrl());
            Glide.with(this).load(selectedImageUri).into(dialogImageView);
        } else {
            dialogImageView.setImageResource(R.drawable.a2);
            selectedImageUri = null;
        }

        selectImageButton.setOnClickListener(v -> pickImageLauncher.launch("image/*"));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chỉnh sửa thương hiệu")
                .setView(dialogView)
                .setPositiveButton("Cập nhật", (dialog, which) -> {
                    String name = nameEditText.getText().toString().trim();
                    String description = descriptionEditText.getText().toString().trim();
                    boolean isActive = activeSwitch.isChecked();

                    if (!name.isEmpty() && !description.isEmpty() && selectedImageUri != null) {
                        String imageUrl = selectedImageUri.toString();
                        String currentTime = LocalDateTime.now().format(formatter);

                        brand.setName(name);
                        brand.setDescription(description);
                        brand.setImageUrl(imageUrl);
                        brand.setActive(isActive);
                        brand.setUpdatedAt(currentTime);

                        new Thread(() -> {
                            database.brandDao().update(brand);
                            runOnUiThread(() -> Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show());
                        }).start();
                    } else {
                        Toast.makeText(this, "Vui lòng nhập đủ thông tin và chọn ảnh!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    private void showDeleteConfirmationDialog(Brand brand) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_delete_brand, null);
        TextView dialogTitle = dialogView.findViewById(R.id.dialog_title);
        TextView dialogMessage = dialogView.findViewById(R.id.dialog_message);
        Button buttonCancel = dialogView.findViewById(R.id.button_cancel_delete);
        Button buttonConfirm = dialogView.findViewById(R.id.button_confirm_delete);

        dialogTitle.setText("Xác nhận xóa thương hiệu");
        dialogMessage.setText("Bạn có chắc muốn xóa thương hiệu: \"" + brand.getName() + "\" không?");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();

        buttonCancel.setOnClickListener(v -> dialog.dismiss());
        buttonConfirm.setOnClickListener(v -> {
            deleteBrand(brand);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void deleteBrand(Brand brand) {
        new Thread(() -> {
            database.brandDao().delete(brand);
            runOnUiThread(() -> Toast.makeText(this, "Xóa thành công!", Toast.LENGTH_SHORT).show());
        }).start();

    }
}
