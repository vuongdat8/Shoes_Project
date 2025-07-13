package com.example.shoes_project.ui.admin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button; // Import Button
import android.widget.ImageButton; // Import ImageButton
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.shoes_project.R;
import com.example.shoes_project.model.Category;
import com.example.shoes_project.ui.AddEditProductActivity;
import com.example.shoes_project.ui.HomeActivity;
import com.example.shoes_project.ui.admin.Home_Admin_Activity; // Đảm bảo đường dẫn này đúng với Activity trang chủ của admin
import com.example.shoes_project.ui.admin.Category_Admin_Activity; // Giả sử đây là Activity quản lý danh mục (ví dụ: nơi bạn thêm thể loại)
import com.example.shoes_project.ui.admin.Product_Admin_Activity; // Giả sử đây là Activity để chỉnh sửa chi tiết danh mục

public class CategoryDetailActivity extends AppCompatActivity {

    public static final String EXTRA_CATEGORY = "extra_category";

    private TextView detailName, detailDescription, detailStatus, detailCreatedAt, detailUpdatedAt;
    private ImageView detailImage;
    private Button btnEditCategory; // Khai báo nút chỉnh sửa
    private ImageButton btnBackCategory; // Khai báo nút Back
    private ImageButton btnHomeCategory; // Khai báo nút Home

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

        // Khởi tạo các nút từ layout
        btnEditCategory = findViewById(R.id.btn_edit_category);
        btnBackCategory = findViewById(R.id.btn_back_category);
        btnHomeCategory = findViewById(R.id.btn_home_category);


        // Get the Category object from the Intent
        Category category = (Category) getIntent().getSerializableExtra(EXTRA_CATEGORY);

        if (category != null) {
            Log.d("CategoryDetailActivity", "Category received: " + category.getName());
            Log.d("CategoryDetailActivity", "Image URL: " + category.getImageUrl());

            detailName.setText(category.getName());
            detailDescription.setText(category.getDescription());
            detailStatus.setText("Trạng thái: " + (category.isActive() ? "Hoạt động" : "Ngừng hoạt động"));
            detailCreatedAt.setText("Ngày tạo: " + (category.getCreatedAt() != null ? category.getCreatedAt() : "N/A"));
            detailUpdatedAt.setText("Ngày cập nhật: " + (category.getUpdatedAt() != null ? category.getUpdatedAt() : "N/A"));

            if (category.getImageUrl() != null && !category.getImageUrl().isEmpty()) {
                Glide.with(this)
                        .load(Uri.parse(category.getImageUrl()))
                        .placeholder(R.drawable.a1)
                        .error(R.drawable.a2)
                        .into(detailImage);
            } else {
                Log.d("CategoryDetailActivity", "Image URL is empty or null. Loading default image.");
                detailImage.setImageResource(R.drawable.a4);
            }
        } else {
            Toast.makeText(this, "Không tìm thấy thông tin danh mục.", Toast.LENGTH_SHORT).show();
            Log.e("CategoryDetailActivity", "No Category object received via Intent.");
            finish();
        }

        // --- Thiết lập OnClickListener cho các nút ---

        // Nút "Chỉnh sửa danh mục"
        btnEditCategory.setOnClickListener(v -> {
            // Khi nhấn nút này, chuyển sang Activity chỉnh sửa danh mục
            Intent intent = new Intent(CategoryDetailActivity.this, HomeActivity.class);
            // Bạn có thể truyền đối tượng category hiện tại sang Activity chỉnh sửa
            if (category != null) {
                intent.putExtra(EXTRA_CATEGORY, category);
            }
            startActivity(intent);
        });

        // Nút Back
        btnBackCategory.setOnClickListener(v -> {
            onBackPressed(); // Phương thức này giả lập hành vi nhấn nút back của thiết bị
        });

        // Nút Home
        btnHomeCategory.setOnClickListener(v -> {
            // Khi nhấn nút này, chuyển về Activity trang chủ của admin
            Intent intent = new Intent(CategoryDetailActivity.this, Home_Admin_Activity.class);
            // Các cờ này đảm bảo rằng tất cả các Activity trên Home_Admin_Activity sẽ bị đóng
            // và Home_Admin_Activity sẽ trở thành Activity trên cùng của stack
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); // Đóng CategoryDetailActivity để không quay lại nó khi nhấn back từ Home
        });

        // Tùy chọn: Vô hiệu hóa nút back mặc định trên ActionBar nếu bạn đang dùng nút custom
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false); // Vô hiệu hóa nút back mặc định
            getSupportActionBar().setTitle("Chi tiết danh mục");
        }
    }

    // Bạn có thể xóa phương thức này nếu bạn hoàn toàn dựa vào nút `btnBackCategory` của mình
    // @Override
    // public boolean onSupportNavigateUp() {
    //     finish();
    //     return true;
    // }
}