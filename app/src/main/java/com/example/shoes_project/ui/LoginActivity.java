package com.example.shoes_project.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.shoes_project.R;
import com.example.shoes_project.data.AppDatabase;
import com.example.shoes_project.model.User;
import com.example.shoes_project.data.UserDao;
import com.google.android.material.button.MaterialButton;

// -------- Firebase & Facebook ----------
import com.facebook.CallbackManager;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
// ---------------------------------------

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    /* ---------- View & DB ---------- */
    private EditText etEmail, etPassword;
    private CheckBox cbRemember;
    private UserDao  userDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    /* ---------- Firebase / Facebook ---------- */
    private FirebaseAuth    mAuth;
    private CallbackManager fbCallbackManager;

    /* ---------- SharedPreferences ---------- */
    private static final String PREF_LOGIN   = "login_prefs";   // remember‑me
    private static final String PREF_AUTH    = "auth";          // session (email, id)
    private static final String KEY_REMEMBER = "remember_email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        /* 1. Ánh xạ view -------------------------------------------------- */
        etEmail      = findViewById(R.id.et_email);
        etPassword   = findViewById(R.id.et_password);
        MaterialButton btnLogin  = findViewById(R.id.btn_login);
        cbRemember    = findViewById(R.id.cb_remember);

        TextView tvForgot   = findViewById(R.id.tv_forgot_password);
        TextView tvRegister = findViewById(R.id.tv_register);

        ImageView ivFacebook = findViewById(R.id.iv_facebook);
        ImageView ivGithub   = findViewById(R.id.iv_github);
        ImageView ivTwitter  = findViewById(R.id.iv_twitter);

        /* 2. Room DB ------------------------------------------------------ */
        userDao = AppDatabase.getInstance(this).userDao();
        executor.execute(() -> {
            if (userDao.countUsers() == 0) {
                userDao.insert(new User(
                        "Nguyen Duc Trung",
                        "ninjaanhem@email.com",
                        "123456",
                        true          // true = admin
                ));
            }
        });

        /* 3. Remember me -------------------------------------------------- */
        SharedPreferences spLogin = getSharedPreferences(PREF_LOGIN, MODE_PRIVATE);
        String savedEmail = spLogin.getString(KEY_REMEMBER, null);
        if (savedEmail != null) {
            etEmail.setText(savedEmail);
            cbRemember.setChecked(true);
        }

        /* 4. Firebase / Facebook init ------------------------------------ */
        mAuth            = FirebaseAuth.getInstance();
        fbCallbackManager = CallbackManager.Factory.create();

        if (mAuth.getCurrentUser() != null) {        // đã login MXH
            saveFirebaseSessionAndGoHome();
            return;
        }

        /* 5. LOGIN Room --------------------------------------------------- */
        btnLogin.setOnClickListener(v -> attemptLocalLogin());

        /* 6. Các nút khác ------------------------------------------------- */
        tvForgot.setOnClickListener(v ->
                startActivity(new Intent(this, ForgotPasswordActivity.class)));

        tvRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));

        /* 7. Đăng nhập MXH (Facebook / GitHub / Twitter) ----------------- */
        // ... Giống code gốc (không đổi) ...
        // Trong phần onSuccess của từng MXH -> gọi firebaseSignIn()
    }

    /* ---------------- LOGIN LOCAL (Room) ------------------------------- */
    private void attemptLocalLogin() {
        String email    = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Email không hợp lệ");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Nhập mật khẩu");
            return;
        }

        executor.execute(() -> {
            User user = userDao.login(email, password);
            runOnUiThread(() -> {
                if (user != null) {
                    /* NEW ✨ Lưu session (email + id) */
                    saveAuthSession(user);

                    handleRememberMe(user.getEmail());
                    goHome();
                } else {
                    Toast.makeText(this, "Sai email hoặc mật khẩu", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    /* ---------------- Firebase chung ----------------------------------- */
    private void firebaseSignIn(AuthCredential cred) {
        mAuth.signInWithCredential(cred)
                .addOnSuccessListener(authResult -> saveFirebaseSessionAndGoHome())
                .addOnFailureListener(e ->
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    /* ---------- Lưu session cho Firebase & chuyển màn ------------------ */
    private void saveFirebaseSessionAndGoHome() {
        if (mAuth.getCurrentUser() != null) {
            String emailFb = mAuth.getCurrentUser().getEmail();
            SharedPreferences prefs = getSharedPreferences(PREF_AUTH, MODE_PRIVATE);
            prefs.edit()
                    .putString("email", emailFb)
                    .putInt   ("userId", 0)          // 0 → chưa có trong Room
                    .apply();
        }
        goHome();
    }

    /* ---------- Lưu session khi login Room ----------------------------- */
    private void saveAuthSession(User user) {
        getSharedPreferences(PREF_AUTH, MODE_PRIVATE)
                .edit()
                .putString("email", user.getEmail())
                .putInt   ("userId", user.getId())
                .putBoolean("role",  user.isRole())   // ✨ boolean
                .apply();
    }

    /* ---------------- Remember Me (email) ------------------------------ */
    private void handleRememberMe(String email) {
        SharedPreferences sp = getSharedPreferences(PREF_LOGIN, MODE_PRIVATE);
        if (cbRemember.isChecked()) {
            sp.edit().putString(KEY_REMEMBER, email).apply();
        } else {
            sp.edit().remove(KEY_REMEMBER).apply();
        }
    }

    /* ---------------- Đi vào màn hình tuỳ role -------------------------- */
    private void goHome() {
        // 1. Đọc cờ role (isAdmin) đã lưu trong SharedPreferences
        boolean isAdmin = getSharedPreferences(PREF_AUTH, MODE_PRIVATE)
                .getBoolean("role", false);   // false mặc định = khách

        // 2. Xác định Activity đích tuỳ theo role
        Class<?> target = isAdmin
                ? HomeActivity.class                     // admin / nhân viên
                : CustomerProductListActivity.class;     // khách mua hàng

        // 3. Điều hướng sang Activity đích
        startActivity(new Intent(this, target));

        // 4. Kết thúc LoginActivity để không quay lại khi bấm Back
        finish();
    }

    /* ------------------------------------------------------------------- */
    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data) {
        super.onActivityResult(reqCode, resCode, data);
        fbCallbackManager.onActivityResult(reqCode, resCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
