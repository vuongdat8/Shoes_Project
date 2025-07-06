package com.example.shoes_project.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.shoes_project.R;
import com.example.shoes_project.data.AppDatabase;
import com.example.shoes_project.data.User;
import com.google.android.material.button.MaterialButton;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Activity xác thực OTP khi đăng ký.
 *
 * Lỗi NullPointerException trước đây do đặt nhầm tên layout hoặc ID không khớp.
 * File này đã được sửa:
 *   1. setContentView() trỏ đúng tới R.layout.otp_register_activity.
 *   2. Toàn bộ ID EditText khớp với layout.
 *   3. Thêm kiểm tra null ở mapViews() để log thiếu view khi dev quên.
 *   4. Lưu cả họ tên vào User (nếu entity hỗ trợ).
 */
public class OtpRegisterActivity extends AppCompatActivity {

    private static final int OTP_LENGTH = 6;

    // Views
    private final EditText[] etOtp = new EditText[OTP_LENGTH];
    private MaterialButton btnVerify;
    private TextView tvResend, tvLogin;

    // DB + Thread
    private AppDatabase db;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    // Data from RegisterActivity
    private String fullName, email, password, correctOtp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.otpregister_activity);

        getIntentData();
        mapViews();
        initDatabase();
        setupOtpInputs();
        setListeners();
    }

    /* ------------ Init helpers ------------ */
    private void getIntentData() {
        Intent i = getIntent();
        fullName   = i.getStringExtra("fullName");
        email      = i.getStringExtra("email");
        password   = i.getStringExtra("password");
        correctOtp = i.getStringExtra("otp");
    }

    private void mapViews() {
        etOtp[0] = findViewById(R.id.et_otp_1);
        etOtp[1] = findViewById(R.id.et_otp_2);
        etOtp[2] = findViewById(R.id.et_otp_3);
        etOtp[3] = findViewById(R.id.et_otp_4);
        etOtp[4] = findViewById(R.id.et_otp_5);
        etOtp[5] = findViewById(R.id.et_otp_6);

        // Quickly log missing IDs during dev time
        for (int i = 0; i < OTP_LENGTH; i++) {
            if (etOtp[i] == null) {
                throw new IllegalStateException("Missing EditText with id et_otp_" + (i + 1));
            }
        }

        btnVerify = findViewById(R.id.btn_verify);
        tvResend  = findViewById(R.id.tv_resend);
        tvLogin   = findViewById(R.id.tv_login);
    }

    private void initDatabase() {
        db = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "app_db")
                .fallbackToDestructiveMigration()
                .build();
    }

    /* ------------ OTP logic ------------ */
    /** Auto‑move cursor forward/backward */
    private void setupOtpInputs() {
        for (int i = 0; i < OTP_LENGTH; i++) {
            final int index = i;
            etOtp[i].addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 1 && index < OTP_LENGTH - 1) {
                        etOtp[index + 1].requestFocus();
                    } else if (s.length() == 0 && index > 0) {
                        etOtp[index - 1].requestFocus();
                    }
                }
            });
        }
    }

    private void setListeners() {
        btnVerify.setOnClickListener(v -> verifyOtp());

        tvResend.setOnClickListener(v -> resendOtp());

        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
        });
    }

    /** Build input string & compare with correctOtp */
    private void verifyOtp() {
        StringBuilder sb = new StringBuilder();
        for (EditText e : etOtp) {
            String digit = e.getText().toString().trim();
            if (digit.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đủ 6 số", Toast.LENGTH_SHORT).show();
                return;
            }
            sb.append(digit);
        }
        String inputOtp = sb.toString();

        if (inputOtp.equals(correctOtp)) {
            insertUserToDb();
        } else {
            Toast.makeText(this, "Mã OTP không đúng", Toast.LENGTH_SHORT).show();
        }
    }

    /** Insert user then navigate to Login */
    private void insertUserToDb() {
        executor.execute(() -> {
            // Giả sử User có constructor (email, password, fullName)
            db.userDao().insert(new User(fullName, email, password, false));

            runOnUiThread(() -> {
                Toast.makeText(this, "Đăng ký thành công! Vui lòng đăng nhập.", Toast.LENGTH_LONG).show();
                startActivity(new Intent(this, LoginActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
            });
        });
    }

    /* ------------ Resend OTP ------------ */
    private void resendOtp() {
        correctOtp = String.format("%06d", new Random().nextInt(1_000_000));
        MailHelper.sendOtp(email, correctOtp, this);
        clearOtpFields();
        Toast.makeText(this, "Đã gửi lại mã xác nhận", Toast.LENGTH_SHORT).show();
    }

    private void clearOtpFields() {
        for (EditText e : etOtp) e.setText("");
        etOtp[0].requestFocus();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
