package com.example.shoes_project.ui.admin;

import android.net.Uri; // Import Uri
import android.os.Bundle;
import android.util.Log; // Import Log
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.shoes_project.R;
import com.example.shoes_project.model.Category;

public class CategoryDetailActivity extends AppCompatActivity {

    public static final String EXTRA_CATEGORY = "extra_category";

    private TextView detailName, detailDescription, detailStatus, detailCreatedAt, detailUpdatedAt;
    private ImageView detailImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_detail);

        // Initialize UI components
        detailName = findViewById(R.id.detail_category_name);
        detailDescription = findViewById(R.id.detail_category_description);
        detailStatus = findViewById(R.id.detail_category_status);
        detailCreatedAt = findViewById(R.id.detail_category_created_at);
        detailUpdatedAt = findViewById(R.id.detail_category_updated_at);
        detailImage = findViewById(R.id.detail_category_image);

        // Get the Category object from the Intent
        Category category = (Category) getIntent().getSerializableExtra(EXTRA_CATEGORY);

        if (category != null) {
            // Log for debugging: Check if category data is received
            Log.d("CategoryDetailActivity", "Category received: " + category.getName());
            Log.d("CategoryDetailActivity", "Image URL: " + category.getImageUrl());

            // Populate UI with category data
            detailName.setText(category.getName());
            detailDescription.setText(category.getDescription());
            detailStatus.setText("Trạng thái: " + (category.isActive() ? "Hoạt động" : "Ngừng hoạt động"));
            detailCreatedAt.setText("Ngày tạo: " + (category.getCreatedAt() != null ? category.getCreatedAt() : "N/A"));
            detailUpdatedAt.setText("Ngày cập nhật: " + (category.getUpdatedAt() != null ? category.getUpdatedAt() : "N/A"));

            // Load image using Glide
            if (category.getImageUrl() != null && !category.getImageUrl().isEmpty()) {
                Glide.with(this)
                        .load(Uri.parse(category.getImageUrl())) // Dùng Uri.parse() cho nhất quán và rõ ràng
                        .placeholder(R.drawable.a1) // Thay đổi placeholder thành ảnh bạn muốn (VD: a1)
                        .error(R.drawable.a2)     // Thay đổi error image thành ảnh báo lỗi (VD: a2)
                        .into(detailImage);
            } else {
                Log.d("CategoryDetailActivity", "Image URL is empty or null. Loading default image.");
                detailImage.setImageResource(R.drawable.a4); // Sử dụng ảnh mặc định rõ ràng hơn (VD: a4)
            }
        } else {
            Toast.makeText(this, "Không tìm thấy thông tin danh mục.", Toast.LENGTH_SHORT).show();
            Log.e("CategoryDetailActivity", "No Category object received via Intent.");
            finish();
        }

        // Optional: Add a back button to the toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Chi tiết danh mục");
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // Handle back button in toolbar
        return true;
    }
}