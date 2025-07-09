package com.example.shoes_project.ui.admin;

import android.content.Intent;
import android.net.Uri; // Import Uri
import android.os.Bundle;
import android.util.Log; // Import Log
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.shoes_project.R;
import com.example.shoes_project.model.Brand;

import com.example.shoes_project.R;
public class BrandDetailActivity extends AppCompatActivity {

    public static final String EXTRA_BRAND = "extra_brand";

    private TextView detailName, detailDescription, detailStatus, detailCreatedAt, detailUpdatedAt;
    private ImageView detailImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brand_detail); // Đảm bảo layout này đã được sửa từ category_detail_activity

        // Khởi tạo các View từ layout
        detailName = findViewById(R.id.detail_brand_name);
        detailDescription = findViewById(R.id.detail_brand_description);
        detailStatus = findViewById(R.id.detail_brand_status);
        detailCreatedAt = findViewById(R.id.detail_brand_created_at);
        detailUpdatedAt = findViewById(R.id.detail_brand_updated_at);
        detailImage = findViewById(R.id.detail_brand_image);

        // Nhận đối tượng Brand từ Intent
        Intent intent = getIntent();
        Brand brand = (Brand) intent.getSerializableExtra(EXTRA_BRAND);

        if (brand != null) {
            // DEBUG log thông tin nhận được
            Log.d("BrandDetailActivity", "Brand received: " + brand.getName());
            Log.d("BrandDetailActivity", "Image URL: " + brand.getImageUrl());

            // Gán dữ liệu vào giao diện
            detailName.setText("Tên thương hiệu: " + brand.getName());
            detailDescription.setText("Mô tả: " + brand.getDescription());
            detailStatus.setText("Trạng thái: " + (brand.isActive() ? "Hoạt động" : "Ngừng hoạt động"));
            detailCreatedAt.setText("Ngày tạo: " + (brand.getCreatedAt() != null ? brand.getCreatedAt() : "N/A"));
            detailUpdatedAt.setText("Ngày cập nhật: " + (brand.getUpdatedAt() != null ? brand.getUpdatedAt() : "N/A"));

            // Load ảnh bằng Glide nếu có ảnh URL
            if (brand.getImageUrl() != null && !brand.getImageUrl().isEmpty()) {
                Glide.with(this)
                        .load(Uri.parse(brand.getImageUrl()))
                        .placeholder(R.drawable.a1) // ảnh đang tải
                        .error(R.drawable.a2)       // ảnh lỗi
                        .into(detailImage);
            } else {
                Log.d("BrandDetailActivity", "Image URL is empty or null. Loading default image.");
                detailImage.setImageResource(R.drawable.a4); // ảnh mặc định
            }

        } else {
            // Nếu không có Brand nào được gửi qua Intent
            Toast.makeText(this, "Không tìm thấy thông tin thương hiệu.", Toast.LENGTH_SHORT).show();
            Log.e("BrandDetailActivity", "No Brand object received via Intent.");
            finish();
        }

        // Tuỳ chọn: hiển thị nút back trên ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Chi tiết thương hiệu");
        }
    }

    // Xử lý nút back trên ActionBar
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}