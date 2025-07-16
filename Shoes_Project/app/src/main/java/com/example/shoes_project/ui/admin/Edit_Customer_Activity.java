package com.example.shoes_project.ui.admin;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.shoes_project.R;
import com.example.shoes_project.data.AppDatabase;
import com.example.shoes_project.data.UserDao;
import com.example.shoes_project.model.User;
import com.google.android.material.textfield.TextInputEditText;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Edit_Customer_Activity extends AppCompatActivity {

    private TextInputEditText etFullName, etEmail, etPassword;
    private CheckBox cbIsAdmin;
    private Button btnSave, btnCancel;
    private UserDao userDao;
    private ExecutorService executorService;

    private int userId = -1; // -1 nếu là thêm mới, id thật nếu là chỉnh sửa

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_customer);

        initViews();
        userDao = AppDatabase.getInstance(this).userDao();
        executorService = Executors.newSingleThreadExecutor();

        // Kiểm tra nếu là chỉnh sửa khách hàng
        if (getIntent().hasExtra("user_id")) {
            userId = getIntent().getIntExtra("user_id", -1);
            loadUserData(userId);
        }

        btnSave.setOnClickListener(v -> saveCustomer());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void initViews() {
        etFullName = findViewById(R.id.edit_text_full_name);
        etEmail = findViewById(R.id.edit_text_email);
        etPassword = findViewById(R.id.edit_text_password);
        cbIsAdmin = findViewById(R.id.checkbox_is_admin);
        btnSave = findViewById(R.id.button_save_customer);
        btnCancel = findViewById(R.id.button_cancel);
    }

    private void loadUserData(int id) {
        executorService.execute(() -> {
            User user = userDao.getUserById(id);
            if (user != null) {
                runOnUiThread(() -> {
                    etFullName.setText(user.getFname());
                    etEmail.setText(user.getEmail());
                    // Không hiển thị mật khẩu hiện tại vì lý do bảo mật
                    cbIsAdmin.setChecked(user.isRole());
                });
            } else {
                runOnUiThread(() -> Toast.makeText(Edit_Customer_Activity.this, "Không tìm thấy khách hàng", Toast.LENGTH_SHORT).show());
                finish(); // Đóng Activity nếu không tìm thấy user
            }
        });
    }

    private void saveCustomer() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        boolean isAdmin = cbIsAdmin.isChecked();

        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ họ tên và email", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate email format if needed

        executorService.execute(() -> {
            if (userId == -1) { // Thêm mới
                // Kiểm tra email đã tồn tại chưa
                User existingUser = userDao.getUserByEmail(email);
                if (existingUser != null) {
                    runOnUiThread(() -> Toast.makeText(Edit_Customer_Activity.this, "Email này đã tồn tại!", Toast.LENGTH_SHORT).show());
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    runOnUiThread(() -> Toast.makeText(Edit_Customer_Activity.this, "Vui lòng nhập mật khẩu cho người dùng mới", Toast.LENGTH_SHORT).show());
                    return;
                }

                User newUser = new User(fullName, email, password, isAdmin);
                userDao.insert(newUser);
                runOnUiThread(() -> {
                    Toast.makeText(Edit_Customer_Activity.this, "Thêm khách hàng thành công!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK); // Báo cho CustomerListActivity biết đã có thay đổi
                    finish();
                });
            } else { // Chỉnh sửa
                User userToUpdate = userDao.getUserById(userId);
                if (userToUpdate != null) {
                    userToUpdate.setFname(fullName);
                    userToUpdate.setEmail(email);
                    userToUpdate.setRole(isAdmin);

                    if (!TextUtils.isEmpty(password)) { // Chỉ cập nhật mật khẩu nếu có nhập
                        userToUpdate.setPassword(password);
                    }
                    userDao.update(userToUpdate);
                    runOnUiThread(() -> {
                        Toast.makeText(Edit_Customer_Activity.this, "Cập nhật khách hàng thành công!", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK); // Báo cho CustomerListActivity biết đã có thay đổi
                        finish();
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(Edit_Customer_Activity.this, "Không tìm thấy khách hàng để cập nhật", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}