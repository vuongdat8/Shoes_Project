package com.example.shoes_project.ui.admin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton; // Import ImageButton
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.shoes_project.R;
import com.example.shoes_project.model.Brand;
import com.example.shoes_project.ui.admin.Home_Admin_Activity; // Assuming your main/home activity is named MainActivity in this package

public class BrandDetailActivity extends AppCompatActivity {

    public static final String EXTRA_BRAND = "extra_brand";

    private TextView detailName, detailDescription, detailStatus, detailCreatedAt, detailUpdatedAt;
    private ImageView detailImage;
    private Button btnEditBrand;
    private ImageButton btnBack; // Declare the back button
    private ImageButton btnHome; // Declare the home button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brand_detail);

        // Initialize UI components
        detailName = findViewById(R.id.detail_brand_name);
        detailDescription = findViewById(R.id.detail_brand_description);
        detailStatus = findViewById(R.id.detail_brand_status);
        detailCreatedAt = findViewById(R.id.detail_brand_created_at);
        detailUpdatedAt = findViewById(R.id.detail_brand_updated_at);
        detailImage = findViewById(R.id.detail_brand_image);
        btnEditBrand = findViewById(R.id.btn_edit_brand);
        btnBack = findViewById(R.id.btn_back); // Initialize the back button
        btnHome = findViewById(R.id.btn_home); // Initialize the home button

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

        // Set OnClickListener for the "Thêm thể loại" button
        btnEditBrand.setOnClickListener(v -> {
            Intent intent = new Intent(BrandDetailActivity.this, Category_Admin_Activity.class);
            startActivity(intent);
        });

        // Set OnClickListener for the back button
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(BrandDetailActivity.this, Home_Admin_Activity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();        });

        // Set OnClickListener for the home button
        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(BrandDetailActivity.this, Home_Admin_Activity.class);
            // Optional: Clear the activity stack so the user doesn't go back to BrandDetailActivity when pressing back from MainActivity
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); // Finish BrandDetailActivity so it's not in the stack
        });

        // Optional: Remove the default action bar back button if you're using your custom one
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false); // Disable default back button
            getSupportActionBar().setTitle("Chi tiết thương hiệu");
        }
    }

    // You can remove this override if you're using your custom back button (btnBack)
    // @Override
    // public boolean onSupportNavigateUp() {
    //     finish();
    //     return true;
    // }
}