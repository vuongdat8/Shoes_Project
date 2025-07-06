package com.example.shoes_project.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.shoes_project.R;
import com.example.shoes_project.data.AppDatabase;
import com.google.android.material.button.MaterialButton;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Đặt lại mật khẩu mới sau khi người dùng nhập đúng OTP.
 * Nhận "email" qua Intent, cho phép nhập mới/ confirm, update vào Room DB.
 */
public class NewPasswordActivity extends AppCompatActivity {

    private EditText etNewPass, etConfirm;
    private MaterialButton btnSave;
    private TextView tvBackLogin;

    private String email; // nhận từ OtpActivity

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newpassword_activity); // layout bạn vừa tạo

        email = getIntent().getStringExtra("email");
        if (email == null) {
            Toast.makeText(this, "Missing email", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        mapViews();
        db = AppDatabase.getInstance(this);
        setListeners();
    }

    private void mapViews() {
        etNewPass      = findViewById(R.id.et_new_password);
        etConfirm      = findViewById(R.id.et_confirm_password);
        btnSave        = findViewById(R.id.btn_save_password);
        tvBackLogin    = findViewById(R.id.tv_back_to_login);
    }

    private void setListeners() {
        btnSave.setOnClickListener(v -> attemptChange());

        tvBackLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
        });
    }

    /** Validate & update password in DB */
    private void attemptChange() {
        String pass = etNewPass.getText().toString();
        String cf   = etConfirm.getText().toString();

        if (TextUtils.isEmpty(pass))        { etNewPass.setError("Nhập mật khẩu mới"); return; }
        if (pass.length() < 6)              { etNewPass.setError("Tối thiểu 6 ký tự"); return; }
        if (!pass.equals(cf))               { etConfirm.setError("Mật khẩu không khớp"); return; }

        executor.execute(() -> {
            // Cách 1: dùng hàm updatePassword(email, pass) của DAO nếu có
            int rows = db.userDao().updatePassword(email, pass);
            // Cách 2: nếu DAO chưa có, fetch & update
            // User u = db.userDao().getUserByEmail(email);
            // if (u != null) { u.setPassword(pass); db.userDao().update(u); rows = 1; }

            runOnUiThread(() -> {
                if (rows > 0) {
                    Toast.makeText(this,
                            "Đổi mật khẩu thành công! Hãy đăng nhập lại.",
                            Toast.LENGTH_LONG).show();
                    startActivity(new Intent(this, LoginActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    finish();
                } else {
                    Toast.makeText(this,
                            "Đổi mật khẩu thất bại (không tìm thấy email)",
                            Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
