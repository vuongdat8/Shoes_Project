package com.example.shoes_project.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.shoes_project.R;
import com.example.shoes_project.data.AppDatabase;
import com.example.shoes_project.data.User;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Đổi mật khẩu cho người dùng đang đăng nhập (Room + optional Firebase).
 */
public class ChangePasswordActivity extends AppCompatActivity {

    /* View */
    private EditText etCurrent, etNew, etConfirm;

    /* DB & thread */
    private AppDatabase db;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    /* Session */
    private static final String PREF_AUTH = "auth";
    private String sessionEmail;  // Email của user đang đăng nhập

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.changepassword_activity);

        /* 1. Ánh xạ view */
        etCurrent = findViewById(R.id.et_current_password);
        etNew     = findViewById(R.id.et_new_password);
        etConfirm = findViewById(R.id.et_confirm_password);
        MaterialButton btnChange = findViewById(R.id.btn_change_password);

        /* 2. Lấy email session */
        sessionEmail = getSharedPreferences(PREF_AUTH, MODE_PRIVATE)
                .getString("email", null);

        if (sessionEmail == null) {
            Toast.makeText(this, "Phiên đăng nhập đã hết!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        /* 3. DB */
        db = AppDatabase.getInstance(this);

        /* 4. Sự kiện Đổi mật khẩu */
        btnChange.setOnClickListener(v -> handleChangePassword());
    }

    /* -------------------- Xử lý đổi mật khẩu --------------------- */
    private void handleChangePassword() {
        String currentPwd = etCurrent.getText().toString().trim();
        String newPwd     = etNew.getText().toString().trim();
        String confirmPwd = etConfirm.getText().toString().trim();

        /* A. Kiểm tra input */
        if (TextUtils.isEmpty(currentPwd) ||
                TextUtils.isEmpty(newPwd)     ||
                TextUtils.isEmpty(confirmPwd)) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (newPwd.length() < 6) {
            Toast.makeText(this, "Mật khẩu mới phải ≥ 6 ký tự", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!newPwd.equals(confirmPwd)) {
            Toast.makeText(this, "Xác nhận mật khẩu không khớp!", Toast.LENGTH_SHORT).show();
            return;
        }

        /* B. Truy vấn Room trên background */
        final String emailFinal = sessionEmail;
        executor.execute(() -> {
            /* 1. Kiểm tra mật khẩu hiện tại */
            User user = db.userDao().login(emailFinal, currentPwd);
            if (user == null) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Mật khẩu hiện tại không đúng!", Toast.LENGTH_SHORT).show());
                return;
            }

            /* 2. Cập nhật mật khẩu trong Room */
            int rows = db.userDao().updatePassword(emailFinal, newPwd);
            if (rows > 0) {
                runOnUiThread(() -> Toast.makeText(this,
                        "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show());
            } else {
                runOnUiThread(() -> Toast.makeText(this,
                        "Có lỗi khi cập nhật mật khẩu!", Toast.LENGTH_SHORT).show());
                return;
            }

            /* 3. (Tuỳ chọn) Đổi mật khẩu trên Firebase nếu user đang login MXH/email */
            updateFirebasePassword(currentPwd, newPwd);

            /* 4. Về màn hình Profile hoặc Login tuỳ ý */
            runOnUiThread(() -> {
                Intent i = new Intent(this, LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            });
        });
    }

    /* ------------------ (Optional) Firebase password ------------------ */
    private void updateFirebasePassword(String currentPwd, String newPwd) {
        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fbUser == null || fbUser.getEmail() == null) return;   // không dùng Firebase

        /* Firebase yêu cầu re-auth trước khi đổi password */
        EmailAuthProvider
                .getCredential(fbUser.getEmail(), currentPwd);

        fbUser.reauthenticate(EmailAuthProvider.getCredential(fbUser.getEmail(), currentPwd))
                .addOnSuccessListener(aVoid ->
                        fbUser.updatePassword(newPwd)
                                .addOnSuccessListener(a2 ->
                                        Toast.makeText(this,
                                                "Đã cập nhật mật khẩu Firebase!", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e ->
                                        Toast.makeText(this,
                                                "Firebase: " + e.getMessage(), Toast.LENGTH_SHORT).show()))
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                "Firebase re-auth: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
