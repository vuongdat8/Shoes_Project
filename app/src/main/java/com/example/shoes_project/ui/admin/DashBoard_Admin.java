package com.example.shoes_project.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.shoes_project.R;
import com.example.shoes_project.ui.AddEditProductActivity;
import com.example.shoes_project.ui.HomeActivity;
import com.example.shoes_project.ui.admin.DashboardViewModel;
import com.google.android.material.button.MaterialButton;

public class DashBoard_Admin extends AppCompatActivity {

    // Khai báo các thành phần UI
    private TextView textProductCount, textBrandCount;
    private MaterialButton buttonManageProducts, buttonManageBrands;

    // Khai báo ViewModel
    private DashboardViewModel dashboardViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Sử dụng layout đã tạo
        setContentView(R.layout.activity_dash_board_admin);

        // Khởi tạo ViewModel
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        // Gọi các hàm để thiết lập UI và logic
        initUI();
        setupObservers();
        setupClickListeners();
    }

    /**
     * Hàm này dùng để ánh xạ các view từ file XML.
     */
    private void initUI() {
        textProductCount = findViewById(R.id.text_product_count);
        textBrandCount = findViewById(R.id.text_brand_count);
        buttonManageProducts = findViewById(R.id.button_manage_products);
        buttonManageBrands = findViewById(R.id.button_manage_brands);
    }

    /**
     * Hàm này lắng nghe (observe) sự thay đổi dữ liệu từ ViewModel.
     * Khi có dữ liệu mới, giao diện sẽ tự động được cập nhật.
     */
    private void setupObservers() {
        // Quan sát số lượng sản phẩm
        dashboardViewModel.getProductCount().observe(this, count -> {
            if (count != null) {
                textProductCount.setText(String.valueOf(count));
            } else {
                textProductCount.setText("0");
            }
        });

        // Quan sát số lượng hãng
        dashboardViewModel.getBrandCount().observe(this, count -> {
            if (count != null) {
                textBrandCount.setText(String.valueOf(count));
            } else {
                textBrandCount.setText("0");
            }
        });
    }

    /**
     * Hàm này thiết lập sự kiện click cho các nút.
     */
    private void setupClickListeners() {
        // Sự kiện cho nút "Quản lý" sản phẩm
        buttonManageProducts.setOnClickListener(v -> {
            // Chuyển đến màn hình quản lý sản phẩm
            // Nhớ thay Product_Admin_Activity.class bằng tên Activity quản lý sản phẩm của bạn
            Intent intent = new Intent(DashBoard_Admin.this, HomeActivity.class);
            startActivity(intent);
        });

        // Sự kiện cho nút "Quản lý" hãng
        buttonManageBrands.setOnClickListener(v -> {
            // Chuyển đến màn hình quản lý hãng
            // Nhớ thay Brand_Admin_Activity.class bằng tên Activity quản lý hãng của bạn
            Intent intent = new Intent(DashBoard_Admin.this, Brand_Admin_Activity.class);
            startActivity(intent);
        });
    }
}