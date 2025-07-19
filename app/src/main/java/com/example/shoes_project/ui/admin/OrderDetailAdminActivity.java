package com.example.shoes_project.ui.admin;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.shoes_project.R;
import com.example.shoes_project.*;
import com.example.shoes_project.data.OrderRepository;

public class OrderDetailAdminActivity extends AppCompatActivity {

//    private TextView tvOrderId, tvCustomerName, tvPhone, tvTotal, tvStatus;
////    private Button btnCall;
//    private OrderRepository orderRepository;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_order_detail_admin);
//
//        tvOrderId = findViewById(R.id.tv_order_id);
//        tvCustomerName = findViewById(R.id.tv_customer_name);
//        tvPhone = findViewById(R.id.tv_phone);
//        tvTotal = findViewById(R.id.tv_total);
//        tvStatus = findViewById(R.id.tv_status);
////        btnCall = findViewById(R.id.btn_call_customer);
//
//        orderRepository = new OrderRepository(this);
//        int orderId = getIntent().getIntExtra("orderId", -1);
//
//        if (orderId != -1) {
//            orderRepository.getOrderWithDetailsById(orderId, order -> {
//                if (order != null) {
//                    showOrder(order);
//                } else {
//                    Toast.makeText(this, "Không tìm thấy đơn hàng!", Toast.LENGTH_SHORT).show();
//                    finish();
//                }
//            });
//        }
//    }
//
//    private void showOrder(OrderWithDetails order) {
//        findViewById(R.id.btn_back).setOnClickListener(v -> onBackPressed());
//
//        tvOrderId.setText("Mã đơn: #" + order.getOrderId());
//        tvCustomerName.setText("Khách: " + order.getCustomerName());
//        tvPhone.setText("SĐT: " + order.getCustomerPhone());
//        tvTotal.setText("Tổng: " + String.format("%.0f₫", order.getTotalAmount()));
//        tvStatus.setText("Trạng thái: " + order.getOrderStatus());
//
////        btnCall.setOnClickListener(v -> {
////            String phone = order.getCustomerPhone();
////            if (phone != null && !phone.isEmpty()) {
////                Intent callIntent = new Intent(Intent.ACTION_DIAL);
////                callIntent.setData(Uri.parse("tel:" + phone));
////                startActivity(callIntent);
////            } else {
////                Toast.makeText(this, "Không có số điện thoại!", Toast.LENGTH_SHORT).show();
////            }
////        });
//    }
}
