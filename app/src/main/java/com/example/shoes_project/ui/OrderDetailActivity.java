package com.example.shoes_project.ui;


import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoes_project.R;
import com.example.shoes_project.model.OrderDetail;
import com.example.shoes_project.Adapter.OrderDetailAdapter;
import com.example.shoes_project.viewmodal.OrderDetailViewModel;
import com.google.android.gms.ads.mediation.Adapter;

import java.util.ArrayList;
import java.util.List;

public class OrderDetailActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OrderDetailAdapter orderDetailAdapter;
    private OrderDetailViewModel orderDetailViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);  // Nếu bạn đã đổi tên layout thì phải đổi lại ở đây

        recyclerView = findViewById(R.id.recycler_view_order_details);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        orderDetailAdapter = new OrderDetailAdapter(null);  // Initialize with null, will update later
        recyclerView.setAdapter(orderDetailAdapter);

        orderDetailViewModel = new ViewModelProvider(this).get(OrderDetailViewModel.class);

        // Get the orderId from intent or saved instance state
       /* int orderId = getIntent().getIntExtra("ORDER_ID", -1);

        if (orderId != -1) {
            orderDetailViewModel.getOrderDetails(orderId).observe(this, orderDetails -> {
                if (orderDetails != null) {
                    orderDetailAdapter = new OrderDetailAdapter(orderDetails);
                    recyclerView.setAdapter(orderDetailAdapter);
                } else {
                    Toast.makeText(this, "No details found", Toast.LENGTH_SHORT).show();
                }
            });*/
        int orderId = getIntent().getIntExtra("ORDER_ID", -1);
        if (orderId != -1) {
            orderDetailViewModel.getOrderDetails(orderId).observe(this, orderDetails -> {
                if (orderDetails != null && !orderDetails.isEmpty()) {
                    orderDetailAdapter = new OrderDetailAdapter(orderDetails);
                    recyclerView.setAdapter(orderDetailAdapter);
                } else {
        List<OrderDetail> dummyData = new ArrayList<>();
        dummyData.add(new OrderDetail(orderId, "Product 1", 2, 29.99));
        dummyData.add(new OrderDetail(orderId, "Product 2", 1, 19.99));
        dummyData.add(new OrderDetail(orderId, "Product 3", 5, 99.99));

        orderDetailAdapter = new OrderDetailAdapter(dummyData);
        recyclerView.setAdapter(orderDetailAdapter);

        Toast.makeText(this, "No details found, using dummy data", Toast.LENGTH_SHORT).show();
    }
});
        } else {
        Toast.makeText(this, "Invalid Order ID", Toast.LENGTH_SHORT).show();
        }
        }
    }


