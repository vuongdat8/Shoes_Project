package com.example.shoes_project.ui;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.shoes_project.R;
import com.example.shoes_project.model.Order;

import java.text.NumberFormat;

public class OrderConfirmationActivity extends AppCompatActivity {
    private TextView tvOrderId, tvCustomerName, tvPhone, tvAddress, tvTotal, tvPaymentMethod;
    private ImageView imgQRCode;
    private Button btnBackHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirmation);

        initViews();

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("order")) {
            Order order = (Order) intent.getSerializableExtra("order");

            if (order != null) {
                displayOrderInfo(order);
            }
        }

        btnBackHome.setOnClickListener(v -> {
            Intent homeIntent = new Intent(this, CustomerProductListActivity.class);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(homeIntent);
            finish();
        });
    }

    private void initViews() {
        findViewById(R.id.btn_back).setOnClickListener(v -> onBackPressed());

        tvOrderId = findViewById(R.id.tv_order_id);
        tvCustomerName = findViewById(R.id.tv_customer_name);
        tvPhone = findViewById(R.id.tv_customer_phone);
        tvAddress = findViewById(R.id.tv_customer_address);
        tvTotal = findViewById(R.id.tv_total_amount);
        tvPaymentMethod = findViewById(R.id.tv_payment_method);
        imgQRCode = findViewById(R.id.img_qr_code);
        btnBackHome = findViewById(R.id.btn_back_home);
    }

    private void displayOrderInfo(Order order) {
        tvOrderId.setText("Mã đơn hàng: " + order.getOrderId());
        tvCustomerName.setText("Khách hàng: " + order.getCustomerName());
        tvPhone.setText("SĐT: " + order.getCustomerPhone());
        tvAddress.setText("Địa chỉ: " + order.getCustomerAddress());
        tvTotal.setText("Tổng tiền: " + NumberFormat.getInstance().format(order.getTotalAmount()) + "₫");
        tvPaymentMethod.setText("Phương thức: " + order.getPaymentMethod());

        String method = order.getPaymentMethod() != null ? order.getPaymentMethod().toLowerCase() : "";

        if (method.contains("chuyển khoản") || method.contains("qr")) {
            imgQRCode.setVisibility(View.VISIBLE);
            imgQRCode.setImageResource(R.drawable.qr_code);
        } else {
            imgQRCode.setVisibility(View.GONE);
            new Handler().postDelayed(() -> btnBackHome.performClick(), 10000); // 3 giây
        }
    }

}

