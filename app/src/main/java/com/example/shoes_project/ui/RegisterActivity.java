package com.example.shoes_project.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.shoes_project.R;
import com.example.shoes_project.data.AppDatabase;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegisterActivity extends AppCompatActivity {

    // View
    private EditText etFullName, etEmail, etPassword, etConfirm;
    private CheckBox cbTerms;
    private Button   btnRegister;
    private TextView tvLogin;

    // Database + Thread
    private AppDatabase db;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        mapViews();
        initDatabase();
        setListeners();
    }

    private void mapViews() {
        etFullName  = findViewById(R.id.et_full_name);
        etEmail     = findViewById(R.id.et_email);
        etPassword  = findViewById(R.id.et_password);
        etConfirm   = findViewById(R.id.et_confirm_password);
        cbTerms     = findViewById(R.id.cb_terms);
        btnRegister = findViewById(R.id.btn_register);
        tvLogin     = findViewById(R.id.tv_login);
    }

    private void initDatabase() {
        db = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "app_db")
                .fallbackToDestructiveMigration()
                .build();
    }

    private void setListeners() {
        btnRegister.setOnClickListener(v -> attemptRegister());

        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    /** Validate form, kiểm tra email đã tồn tại, gửi OTP và chuyển Activity */
    private void attemptRegister() {
        String name    = etFullName.getText().toString().trim();
        String email   = etEmail.getText().toString().trim();
        String pass    = etPassword.getText().toString();
        String confirm = etConfirm.getText().toString();

        // Validate cơ bản
        if (TextUtils.isEmpty(name))         { etFullName.setError("Nhập họ tên"); return; }
        if (TextUtils.isEmpty(email))        { etEmail.setError("Nhập email"); return; }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        { etEmail.setError("Email không hợp lệ"); return; }
        if (pass.length() < 6)               { etPassword.setError("Tối thiểu 6 ký tự"); return; }
        if (!pass.equals(confirm))           { etConfirm.setError("Mật khẩu không khớp"); return; }
        if (!cbTerms.isChecked())            { Toast.makeText(this,"Bạn cần chấp nhận điều khoản",Toast.LENGTH_SHORT).show(); return; }

        // Kiểm tra trùng Email
        executor.execute(() -> {
            boolean exists = db.userDao().getUserByEmail(email) != null;
            runOnUiThread(() -> {
                if (exists) {
                    Toast.makeText(this, "Email đã được đăng ký", Toast.LENGTH_SHORT).show();
                } else {
                    // Sinh OTP
                    String otp = String.format("%06d", new Random().nextInt(1_000_000));

                    // Gửi OTP qua Email
                    MailHelper.sendOtp(email, otp, this);

                    // Chuyển sang OtpRegisterActivity

                    Intent intent = new Intent(this, OtpRegisterActivity.class);
                    intent.putExtra("fullName", name);
                    intent.putExtra("email",    email);
                    intent.putExtra("password", pass);
                    intent.putExtra("otp",      otp);
                    startActivity(intent);

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
