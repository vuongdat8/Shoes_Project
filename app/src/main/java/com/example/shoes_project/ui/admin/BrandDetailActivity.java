package com.example.shoes_project.ui.admin;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.shoes_project.R;
import com.example.shoes_project.model.Brand;

public class BrandDetailActivity extends AppCompatActivity {

    public static final String EXTRA_BRAND = "extra_brand";

    private TextView detailName, detailDescription, detailStatus, detailCreatedAt, detailUpdatedAt;
    private ImageView detailImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brand_detail); // Bạn cần tạo layout này

        // Initialize UI components
        detailName = findViewById(R.id.detail_brand_name);
        detailDescription = findViewById(R.id.detail_brand_description);
        detailStatus = findViewById(R.id.detail_brand_status);
        detailCreatedAt = findViewById(R.id.detail_brand_created_at);
        detailUpdatedAt = findViewById(R.id.detail_brand_updated_at);
        detailImage = findViewById(R.id.detail_brand_image);

        // Get the Brand object from the Intent
        Brand brand = (Brand) getIntent().getSerializableExtra(EXTRA_BRAND);

        if (brand != null) {
            Log.d("BrandDetailActivity", "Brand received: " + brand.getName());
            Log.d("BrandDetailActivity", "Image URL: " + brand.getImageUrl());

            detailName.setText(brand.getName());
            detailDescription.setText(brand.getDescription());
            detailStatus.setText("Trạng thái: " + (brand.isActive() ? "Hoạt động" : "Ngừng hoạt động"));
            detailCreatedAt.setText("Ngày tạo: " + (brand.getCreatedAt() != null ? brand.getCreatedAt() : "N/A"));
            detailUpdatedAt.setText("Ngày cập nhật: " + (brand.getUpdatedAt() != null ? brand.getUpdatedAt() : "N/A"));

            if (brand.getImageUrl() != null && !brand.getImageUrl().isEmpty()) {
                Glide.with(this)
                        .load(Uri.parse(brand.getImageUrl()))
                        .placeholder(R.drawable.a1)
                        .error(R.drawable.a2)
                        .into(detailImage);
            } else {
                Log.d("BrandDetailActivity", "Image URL is empty or null. Loading default image.");
                detailImage.setImageResource(R.drawable.a4);
            }
        } else {
            Toast.makeText(this, "Không tìm thấy thông tin thương hiệu.", Toast.LENGTH_SHORT).show();
            Log.e("BrandDetailActivity", "No Brand object received via Intent.");
            finish();
        }

        // Optional: Add a back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Chi tiết thương hiệu");
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
