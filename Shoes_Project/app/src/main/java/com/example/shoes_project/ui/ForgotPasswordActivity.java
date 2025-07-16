package com.example.shoes_project.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.shoes_project.R;
import com.example.shoes_project.data.AppDatabase;
import com.example.shoes_project.data.UserDao;
import com.google.android.material.button.MaterialButton;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText etEmail;
    private UserDao  userDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgotpassword_activity);

        /* view binding đơn giản */
        etEmail                 = findViewById(R.id.et_email);
        MaterialButton btnReset = findViewById(R.id.btn_reset);
        TextView tvLogin        = findViewById(R.id.tv_login);

        /* Room DAO */
        userDao = AppDatabase.getInstance(this).userDao();

        /* ====== RESET PASSWORD ====== */
        btnReset.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.setError("Email không hợp lệ");
                return;
            }

            executor.execute(() -> {
                boolean exists = userDao.getUserByEmail(email) != null;

                runOnUiThread(() -> {
                    if (!exists) {
                        Toast.makeText(this,
                                "Email chưa đăng ký!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    /* 1. sinh OTP */
                    String otp = String.format("%06d",
                            new Random().nextInt(1_000_000));

                    /* 2. gửi email – MailHelper tự chạy nền */
                    MailHelper.sendOtp(email, otp, this);

                    /* 3. sang màn hình nhập OTP */
                    Intent i = new Intent(this, OtpActivity.class);
                    i.putExtra("email", email);
                    i.putExtra("otp",   otp);
                    startActivity(i);
                    finish();
                });
            });
        });

        /* quay lại đăng nhập */
        tvLogin.setOnClickListener(
                v -> startActivity(new Intent(this, LoginActivity.class)));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
