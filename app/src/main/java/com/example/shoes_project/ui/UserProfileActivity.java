package com.example.shoes_project.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.shoes_project.R;
import com.example.shoes_project.data.AppDatabase;
import com.example.shoes_project.model.User;
import com.google.android.material.button.MaterialButton;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//public class UserProfileActivity extends AppCompatActivity {
//
//    private TextView tvFullName, tvEmail;
//    private final ExecutorService executor = Executors.newSingleThreadExecutor();
//
//    private static final String PREF_AUTH = "auth";   // tên SharedPrefs session
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.userprofile_activity);
//
//        /* ---------- Ánh xạ view ---------- */
//        tvFullName = findViewById(R.id.tv_full_name);
//        tvEmail    = findViewById(R.id.tv_email);
//
//        MaterialButton btnEdit       = findViewById(R.id.btn_edit);
//        MaterialButton btnChangePwd  = findViewById(R.id.btn_change_pwd);
//        MaterialButton btnLogout     = findViewById(R.id.btn_logout);
//
//        /* ---------- Lấy email session ---------- */
//        String email = getIntent().getStringExtra("email");
//        if (email == null) {
//            email = getSharedPreferences(PREF_AUTH, MODE_PRIVATE)
//                    .getString("email", null);
//        }
//        if (email == null) {
//            Toast.makeText(this, "Không tìm thấy người dùng đang đăng nhập!", Toast.LENGTH_SHORT).show();
//            finish();
//            return;
//        }
//        final String userEmail = email;      // biến final cho lambda
//
//        /* ---------- Query DB & hiển thị ---------- */
//        AppDatabase db = AppDatabase.getInstance(this);
//        executor.execute(() -> {
//            User user = db.userDao().getUserByEmail(userEmail);
//            if (user != null) {
//                runOnUiThread(() -> {
//                    tvFullName.setText(user.getFname());
//                    tvEmail.setText(user.getEmail());
//                });
//            } else {
//                runOnUiThread(() ->
//                        Toast.makeText(this, "Không tìm thấy dữ liệu người dùng!", Toast.LENGTH_SHORT).show());
//            }
//        });
//
//        /* ---------- Xử lý 3 nút ---------- */
//        // 1. Edit Info
//        btnEdit.setOnClickListener(v ->
//                startActivity(new Intent(this, EditUserProfileActivity.class)));
//
//        // 2. Change Password
//        btnChangePwd.setOnClickListener(v ->
//                startActivity(new Intent(this, ChangePasswordActivity.class)));
//
//        // 3. Logout
//        btnLogout.setOnClickListener(v -> doLogout());
//    }
//
//    /* ---------- Hàm logout ---------- */
//    private void doLogout() {
//        /* A. Xoá session SharedPreferences */
//        SharedPreferences prefs = getSharedPreferences(PREF_AUTH, MODE_PRIVATE);
//        prefs.edit().clear().apply();
//
//        /* B. Nếu dùng Firebase Auth, signOut */
//        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
//        if (firebaseAuth.getCurrentUser() != null) {
//            firebaseAuth.signOut();
//        }
//
//        /* C. Về màn LoginActivity và xoá back‑stack */
//        Intent i = new Intent(this, LoginActivity.class);
//        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(i);
//        // không gọi finish() vì đã clear task
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        executor.shutdown();
//    }
//}
public class UserProfileActivity extends AppCompatActivity {

    // View hiển thị
    private TextView tvFullName, tvEmail;
    // View chỉnh sửa
    private EditText etFullName, etEmail;
    // Nút
    private MaterialButton btnEdit, btnChangePwd, btnLogout, btnGoHome;

    private boolean isEditing = false;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private AppDatabase db;
    private int userId;
    private static final String PREF_AUTH = "auth";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userprofile_activity);

        /* 1. Ánh xạ view */
        tvFullName = findViewById(R.id.tv_full_name);
        tvEmail    = findViewById(R.id.tv_email);
        etFullName = findViewById(R.id.et_full_name);
        etEmail    = findViewById(R.id.et_email);

        btnGoHome     = findViewById(R.id.btn_gohome);
        btnEdit       = findViewById(R.id.btn_edit);
        btnChangePwd  = findViewById(R.id.btn_change_pwd);
        btnLogout     = findViewById(R.id.btn_logout);

        /* 2. Lấy userId / email từ SharedPrefs */
        SharedPreferences sp = getSharedPreferences(PREF_AUTH, MODE_PRIVATE);
        userId = sp.getInt("userId", -1);
        String email = sp.getString("email", null);

        if (userId == -1 || email == null) { finish(); return; }

        /* 3. Hiển thị dữ liệu */
        db = AppDatabase.getInstance(this);
        executor.execute(() -> {
            User user = db.userDao().getUserById(userId);
            if (user != null) {
                runOnUiThread(() -> showUser(user));
            }
        });

        /* 4. Nút Edit / Save */
        btnEdit.setOnClickListener(v -> {
            if (isEditing) saveChanges();   // đang ở chế độ edit → Lưu
            else enterEditMode();           // đang ở chế độ xem → Chỉnh sửa
        });

        /* 5. Nút khác giữ nguyên */
        btnChangePwd.setOnClickListener(
                v -> startActivity(new Intent(this, ChangePasswordActivity.class)));
        btnLogout.setOnClickListener(v -> doLogout());


        /*6. Xử lí nút Go Home*/
        btnGoHome.setOnClickListener(v -> {
            boolean isAdmin = sp.getBoolean("role", false);   // mặc định false = customer

            Intent intent = isAdmin
                    ? new Intent(this, HomeActivity.class)            // role = 1
                    : new Intent(this, CustomerProductListActivity.class); // role = 0

            // tuỳ chọn: xoá stack cũ để tránh quay lại UserProfile
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    /* ----------------- Hiển thị user ----------------- */
    private void showUser(User user) {
        tvFullName.setText(user.getFname());
        tvEmail.setText(user.getEmail());
        etFullName.setText(user.getFname());
        etEmail.setText(user.getEmail());
    }

    /* ----------------- Chế độ chỉnh sửa ----------------- */
    private void enterEditMode() {
        isEditing = true;
        // Ẩn TextView, hiện EditText
        tvFullName.setVisibility(View.GONE);  tvEmail.setVisibility(View.GONE);
        etFullName.setVisibility(View.VISIBLE); etEmail.setVisibility(View.VISIBLE);
        // Đổi nhãn nút
        btnEdit.setText("Save");
        // Optionally ẩn nút khác
        btnChangePwd.setEnabled(false); btnLogout.setEnabled(false);
    }

    /* ----------------- Lưu thay đổi ----------------- */
    private void saveChanges() {
        String newName  = etFullName.getText().toString().trim();
        String newEmail = etEmail    .getText().toString().trim();

        if (newName.isEmpty())       { etFullName.setError("Nhập họ tên"); return; }
        if (!Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
            etEmail.setError("Email không hợp lệ"); return;
        }

        executor.execute(() -> {
            int rows = db.userDao().updateUserInfo(userId, newName, newEmail);
            runOnUiThread(() -> {
                if (rows > 0) {
                    // Cập nhật SharedPrefs nếu email đổi
                    getSharedPreferences(PREF_AUTH, MODE_PRIVATE)
                            .edit().putString("email", newEmail).apply();
                    // Hiển thị lại
                    tvFullName.setText(newName);
                    tvEmail.setText(newEmail);
                    exitEditMode();
                    Toast.makeText(this, "Đã lưu thay đổi!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Không có gì thay đổi!", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    /* ----------------- Thoát edit mode ----------------- */
    private void exitEditMode() {
        isEditing = false;
        // Ẩn EditText, hiện TextView
        tvFullName.setVisibility(View.VISIBLE);  tvEmail.setVisibility(View.VISIBLE);
        etFullName.setVisibility(View.GONE);     etEmail.setVisibility(View.GONE);
        // Đổi nhãn nút
        btnEdit.setText("Edit Info");
        // Mở lại nút khác
        btnChangePwd.setEnabled(true); btnLogout.setEnabled(true);
    }

    /* ----------------- Logout giữ nguyên như trước ----------------- */
    private void doLogout() {
        getSharedPreferences(PREF_AUTH, MODE_PRIVATE).edit().clear().apply();
        startActivity(new Intent(this, LoginActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}


