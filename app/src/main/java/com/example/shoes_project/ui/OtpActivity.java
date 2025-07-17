package com.example.shoes_project.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.shoes_project.R;
import com.google.android.material.button.MaterialButton;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OtpActivity extends AppCompatActivity {

    private static final int OTP_LEN = 6;

    private final EditText[] otpBox = new EditText[OTP_LEN];
    private String email;          // nhận từ Intent
    private String correctOtp;     // OTP hiện tại
    private final ExecutorService pool = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otp_layout);

        /* 1. Intent data */
        Intent i = getIntent();
        email      = i.getStringExtra("email");
        correctOtp = i.getStringExtra("otp");

        /* 2. Ánh xạ 6 ô nhập OTP */
        otpBox[0] = findViewById(R.id.et_otp_1);
        otpBox[1] = findViewById(R.id.et_otp_2);
        otpBox[2] = findViewById(R.id.et_otp_3);
        otpBox[3] = findViewById(R.id.et_otp_4);
        otpBox[4] = findViewById(R.id.et_otp_5);
        otpBox[5] = findViewById(R.id.et_otp_6);

        /* 3. Tự chuyển focus */
        for (int k = 0; k < OTP_LEN; k++) {
            final int idx = k;
            otpBox[k].addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s,int st,int c,int a){}
                @Override public void onTextChanged(CharSequence s,int st,int b,int c){}
                @Override public void afterTextChanged(Editable s) {
                    if (s.length() == 1 && idx < OTP_LEN - 1)  otpBox[idx + 1].requestFocus();
                    if (s.length() == 0 && idx > 0)            otpBox[idx - 1].requestFocus();
                }
            });
        }

        /* 4. Buttons */
        MaterialButton btnVerify = findViewById(R.id.btn_verify);
        TextView tvResend        = findViewById(R.id.tv_resend);
        TextView tvLogin         = findViewById(R.id.tv_login);

        btnVerify.setOnClickListener(v -> verifyOtp());
        tvResend.setOnClickListener(v -> resendOtp());
        tvLogin.setOnClickListener(v ->
                startActivity(new Intent(this, LoginActivity.class)));
    }

    /** Ghép 6 ký tự & so sánh */
    private void verifyOtp() {
        StringBuilder sb = new StringBuilder();
        for (EditText e : otpBox) {
            String d = e.getText().toString().trim();
            if (d.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đủ 6 số", Toast.LENGTH_SHORT).show();
                return;
            }
            sb.append(d);
        }
        if (sb.toString().equals(correctOtp)) {
            /* Thành công – sang đặt mật khẩu mới */
            Intent n = new Intent(this, NewPasswordActivity.class);
            n.putExtra("email", email);
            startActivity(n);
            finish();
        } else {
            Toast.makeText(this, "Mã OTP không đúng", Toast.LENGTH_SHORT).show();
        }
    }

    /** Sinh OTP mới, gửi mail, làm sạch ô nhập */
    private void resendOtp() {
        correctOtp = String.format("%06d", new Random().nextInt(1_000_000));
        MailHelper.sendOtp(email, correctOtp, this);
        for (EditText e : otpBox) e.setText("");
        otpBox[0].requestFocus();
        Toast.makeText(this, "Đã gửi lại mã xác nhận", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pool.shutdown();
    }
}
