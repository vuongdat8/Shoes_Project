package com.example.shoes_project.ui;

import android.os.Bundle;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoes_project.R;
import com.example.shoes_project.Adapter.OrderListAdapter;
import com.example.shoes_project.data.AppDatabase;
import com.example.shoes_project.model.Order;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OrderHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OrderListAdapter orderListAdapter;
    private AppDatabase db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_history_activity);

        recyclerView = findViewById(R.id.recyclerOrderHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = AppDatabase.getInstance(this);

        /*SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);


        if (userId != -1) {
            List<Order> orders = db.orderDao().getOrdersByUserId(userId);
            orderListAdapter = new OrderListAdapter(orders);
            recyclerView.setAdapter(orderListAdapter);
        }*/
        int userId = 1;
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            List<Order> orders = db.orderDao().getOrdersByUserId(userId);

            if (orders == null || orders.isEmpty()) {
                // Thêm đơn hàng mẫu
                db.orderDao().insert(new Order(userId, "2025-07-08", 350000));
                db.orderDao().insert(new Order(userId, "2025-07-09", 420000));
                orders = db.orderDao().getOrdersByUserId(userId);
            }

            // Gán adapter lên UI thread
            List<Order> finalOrders = orders;
            runOnUiThread(() -> {
                orderListAdapter  = new OrderListAdapter(finalOrders);
                recyclerView.setAdapter(orderListAdapter);
            });
        });

    }
}
