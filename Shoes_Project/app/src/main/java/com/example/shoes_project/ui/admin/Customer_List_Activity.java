package com.example.shoes_project.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoes_project.R;
import com.example.shoes_project.adapter.CustomerAdapter;
import com.example.shoes_project.data.AppDatabase;
import com.example.shoes_project.data.UserDao;
import com.example.shoes_project.model.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Customer_List_Activity extends AppCompatActivity implements CustomerAdapter.OnCustomerActionListener {

    private RecyclerView recyclerView;
    private CustomerAdapter customerAdapter;
    private UserDao userDao;
    private ExecutorService executorService;
    private FloatingActionButton fabAddCustomer;

    public static final int REQUEST_CODE_EDIT_CUSTOMER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_list);

        recyclerView = findViewById(R.id.recycler_view_customers);
        fabAddCustomer = findViewById(R.id.fab_add_customer);

        userDao = AppDatabase.getInstance(this).userDao();
        executorService = Executors.newSingleThreadExecutor();

        setupRecyclerView();
        loadCustomers();

        fabAddCustomer.setOnClickListener(v -> {
            Intent intent = new Intent(Customer_List_Activity.this, Edit_Customer_Activity.class);
            startActivityForResult(intent, REQUEST_CODE_EDIT_CUSTOMER);
        });
    }

    private void setupRecyclerView() {
        customerAdapter = new CustomerAdapter(null, this); // Khởi tạo với list rỗng/null
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(customerAdapter);
    }

    private void loadCustomers() {
        // Lấy tất cả người dùng từ DB, sau này bạn có thể lọc theo role nếu cần
        userDao.getAllUsers().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                customerAdapter.setUsers(users);
            }
        });
    }

    @Override
    public void onEditClick(User user) {
        Intent intent = new Intent(Customer_List_Activity.this, Edit_Customer_Activity.class);
        intent.putExtra("user_id", user.getId());
        startActivityForResult(intent, REQUEST_CODE_EDIT_CUSTOMER);
    }

    @Override
    public void onDeleteClick(User user) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa khách hàng " + user.getFname() + " không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    executorService.execute(() -> {
                        userDao.delete(user);
                        runOnUiThread(() -> Toast.makeText(Customer_List_Activity.this, "Đã xóa khách hàng", Toast.LENGTH_SHORT).show());
                    });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_EDIT_CUSTOMER && resultCode == RESULT_OK) {
            // Khi EditCustomerActivity trả về RESULT_OK, làm mới danh sách
            Toast.makeText(this, "Thông tin khách hàng đã được cập nhật!", Toast.LENGTH_SHORT).show();
            // loadCustomers() sẽ tự động cập nhật qua LiveData
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}