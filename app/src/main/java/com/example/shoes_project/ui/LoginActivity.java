//package com.example.shoes_project.ui;
//
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.util.Patterns;
//import android.widget.CheckBox;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.shoes_project.R;
//import com.example.shoes_project.data.AppDatabase;
//import com.example.shoes_project.model.User;
//import com.example.shoes_project.data.UserDao;
//import com.google.android.material.button.MaterialButton;
//
//// -------- Firebase & Facebook ----------
//import com.facebook.CallbackManager;
//
//import com.google.firebase.auth.AuthCredential;
//import com.google.firebase.auth.FirebaseAuth;
//// ---------------------------------------
//
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//public class LoginActivity extends AppCompatActivity {
//
//    /* ---------- View & DB ---------- */
//    private EditText etEmail, etPassword;
//    private CheckBox cbRemember;
//    private UserDao  userDao;
//    private final ExecutorService executor = Executors.newSingleThreadExecutor();
//
//    /* ---------- Firebase / Facebook ---------- */
//    private FirebaseAuth    mAuth;
//    private CallbackManager fbCallbackManager;
//
//    /* ---------- SharedPreferences ---------- */
//    private static final String PREF_LOGIN   = "login_prefs";   // remember‑me
//    private static final String PREF_AUTH    = "auth";          // session (email, id)
//    private static final String KEY_REMEMBER = "remember_email";
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.login_activity);
//
//        /* 1. Ánh xạ view -------------------------------------------------- */
//        etEmail      = findViewById(R.id.et_email);
//        etPassword   = findViewById(R.id.et_password);
//        MaterialButton btnLogin  = findViewById(R.id.btn_login);
//        cbRemember    = findViewById(R.id.cb_remember);
//
//        TextView tvForgot   = findViewById(R.id.tv_forgot_password);
//        TextView tvRegister = findViewById(R.id.tv_register);
//
//        ImageView ivFacebook = findViewById(R.id.iv_facebook);
//        ImageView ivGithub   = findViewById(R.id.iv_github);
//        ImageView ivTwitter  = findViewById(R.id.iv_twitter);
//
//        /* 2. Room DB ------------------------------------------------------ */
//        userDao = AppDatabase.getInstance(this).userDao();
//        executor.execute(() -> {
//            if (userDao.countUsers() == 0) {
//                userDao.insert(new User(
//                        "Nguyen Duc Trung",
//                        "ninjaanhem@email.com",
//                        "123456",
//                        true          // true = admin
//                ));
//            }
//        });
//
//        /* 3. Remember me -------------------------------------------------- */
//        SharedPreferences spLogin = getSharedPreferences(PREF_LOGIN, MODE_PRIVATE);
//        String savedEmail = spLogin.getString(KEY_REMEMBER, null);
//        if (savedEmail != null) {
//            etEmail.setText(savedEmail);
//            cbRemember.setChecked(true);
//        }
//
//        /* 4. Firebase / Facebook init ------------------------------------ */
//        mAuth            = FirebaseAuth.getInstance();
//        fbCallbackManager = CallbackManager.Factory.create();
//
//        if (mAuth.getCurrentUser() != null) {        // đã login MXH
//            saveFirebaseSessionAndGoHome();
//            return;
//        }
//
//        /* 5. LOGIN Room --------------------------------------------------- */
//        btnLogin.setOnClickListener(v -> attemptLocalLogin());
//
//        /* 6. Các nút khác ------------------------------------------------- */
//        tvForgot.setOnClickListener(v ->
//                startActivity(new Intent(this, ForgotPasswordActivity.class)));
//
//        tvRegister.setOnClickListener(v ->
//                startActivity(new Intent(this, RegisterActivity.class)));
//
//        /* 7. Đăng nhập MXH (Facebook / GitHub / Twitter) ----------------- */
//        // ... Giống code gốc (không đổi) ...
//        // Trong phần onSuccess của từng MXH -> gọi firebaseSignIn()
//    }
//
//    /* ---------------- LOGIN LOCAL (Room) ------------------------------- */
//    private void attemptLocalLogin() {
//        String email    = etEmail.getText().toString().trim();
//        String password = etPassword.getText().toString().trim();
//
//        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//            etEmail.setError("Email không hợp lệ");
//            return;
//        }
//        if (TextUtils.isEmpty(password)) {
//            etPassword.setError("Nhập mật khẩu");
//            return;
//        }
//
//        executor.execute(() -> {
//            User user = userDao.login(email, password);
//            runOnUiThread(() -> {
//                if (user != null) {
//                    /* NEW ✨ Lưu session (email + id) */
//                    saveAuthSession(user);
//
//                    handleRememberMe(user.getEmail());
//                    goHome();
//                } else {
//                    Toast.makeText(this, "Sai email hoặc mật khẩu", Toast.LENGTH_SHORT).show();
//                }
//            });
//        });
//    }
//
//    /* ---------------- Firebase chung ----------------------------------- */
//    private void firebaseSignIn(AuthCredential cred) {
//        mAuth.signInWithCredential(cred)
//                .addOnSuccessListener(authResult -> saveFirebaseSessionAndGoHome())
//                .addOnFailureListener(e ->
//                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
//    }
//
//    /* ---------- Lưu session cho Firebase & chuyển màn ------------------ */
//    private void saveFirebaseSessionAndGoHome() {
//        if (mAuth.getCurrentUser() != null) {
//            String emailFb = mAuth.getCurrentUser().getEmail();
//            SharedPreferences prefs = getSharedPreferences(PREF_AUTH, MODE_PRIVATE);
//            prefs.edit()
//                    .putString("email", emailFb)
//                    .putInt   ("userId", 0)          // 0 → chưa có trong Room
//                    .apply();
//        }
//        goHome();
//    }
//
//    /* ---------- Lưu session khi login Room ----------------------------- */
//    private void saveAuthSession(User user) {
//        getSharedPreferences(PREF_AUTH, MODE_PRIVATE)
//                .edit()
//                .putString("email", user.getEmail())
//                .putInt   ("userId", user.getId())
//                .putBoolean("role",  user.isRole())   // ✨ boolean
//                .apply();
//    }
//
//    /* ---------------- Remember Me (email) ------------------------------ */
//    private void handleRememberMe(String email) {
//        SharedPreferences sp = getSharedPreferences(PREF_LOGIN, MODE_PRIVATE);
//        if (cbRemember.isChecked()) {
//            sp.edit().putString(KEY_REMEMBER, email).apply();
//        } else {
//            sp.edit().remove(KEY_REMEMBER).apply();
//        }
//    }
//
//    /* ---------------- Đi vào màn hình tuỳ role -------------------------- */
//    private void goHome() {
//        // 1. Đọc cờ role (isAdmin) đã lưu trong SharedPreferences
//        boolean isAdmin = getSharedPreferences(PREF_AUTH, MODE_PRIVATE)
//                .getBoolean("role", false);   // false mặc định = khách
//
//        // 2. Xác định Activity đích tuỳ theo role
//        Class<?> target = isAdmin
//                ? HomeActivity.class                     // admin / nhân viên
//                : CustomerProductListActivity.class;     // khách mua hàng
//
//        // 3. Điều hướng sang Activity đích
//        startActivity(new Intent(this, target));
//
//        // 4. Kết thúc LoginActivity để không quay lại khi bấm Back
//        finish();
//    }
//
//    /* ------------------------------------------------------------------- */
//    @Override
//    protected void onActivityResult(int reqCode, int resCode, Intent data) {
//        super.onActivityResult(reqCode, resCode, data);
//        fbCallbackManager.onActivityResult(reqCode, resCode, data);
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        executor.shutdown();
//    }
//}
package com.example.shoes_project.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.shoes_project.R;
import com.example.shoes_project.data.AppDatabase;
import com.example.shoes_project.data.UserDao;
import com.example.shoes_project.model.User;

import com.example.shoes_project.ui.admin.Home_Admin_Activity;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    /* ------------------- constants & TAG ---------------------- */
    private static final String TAG = "LOGIN";
    private static final String PREF_LOGIN = "login_prefs";   // remember‑me
    private static final String PREF_AUTH = "auth";           // session (email, id)
    private static final String KEY_REMEMBER = "remember_email";

    /* ---------- View & DB ---------- */
    private EditText etEmail, etPassword;
    private CheckBox cbRemember;
    private UserDao userDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    /* ---------- Firebase / Facebook ---------- */
    private FirebaseAuth mAuth;
    private CallbackManager fbCallbackManager;

    /* ---------- Google ---------- */
    private GoogleSignInClient googleClient;

    /* ---------- Activity Result launcher ---------- */
    private final ActivityResultLauncher<Intent> googleLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            Log.d(TAG, "Google launcher result OK");
                            handleGoogleIntent(result.getData());
                        } else {
                            Log.w(TAG, "Google sign‑in cancelled or failed");
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* 0. Facebook SDK (nếu cần) ----------------------------- */
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(getApplication());

        setContentView(R.layout.login_activity);
        Log.d(TAG, "onCreate");

        /* 1. Map view -------------------------------------------------- */
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        MaterialButton btnLogin = findViewById(R.id.btn_login);
        cbRemember = findViewById(R.id.cb_remember);

        TextView tvForgot = findViewById(R.id.tv_forgot_password);
        TextView tvRegister = findViewById(R.id.tv_register);

        ImageView ivFacebook = findViewById(R.id.iv_facebook);
        ImageView ivGoogle = findViewById(R.id.iv_google);

        /* 2. Room DB -------------------------------------------------- */
        userDao = AppDatabase.getInstance(this).userDao();
        executor.execute(() -> {
            if (userDao.countUsers() == 0) {
                userDao.insert(new User(
                        "Nguyen Duc Trung",
                        "ninjaanhem@email.com",
                        "123456",
                        true // admin mẫu
                ));
            }
        });

        /* 3. Remember me ---------------------------------------------- */
        SharedPreferences spLogin = getSharedPreferences(PREF_LOGIN, MODE_PRIVATE);
        String savedEmail = spLogin.getString(KEY_REMEMBER, null);
        if (savedEmail != null) {
            etEmail.setText(savedEmail);
            cbRemember.setChecked(true);
        }

        /* 4. Firebase & Callback -------------------------------------- */
        mAuth = FirebaseAuth.getInstance();
        fbCallbackManager = CallbackManager.Factory.create();

        /* 4b. Google init --------------------------------------------- */
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleClient = GoogleSignIn.getClient(this, gso);

        /* 4c. Nếu đã đăng nhập MXH thì đi thẳng Home ------------------ */
        if (mAuth.getCurrentUser() != null) {
            Log.d(TAG, "currentUser != null → vào thẳng Home");
            saveFirebaseSession();
            goHome();
            return;
        }

        /* 5. LOGIN Room ------------------------------------------------ */
        btnLogin.setOnClickListener(v -> attemptLocalLogin());

        /* 6. Các nút khác --------------------------------------------- */
        tvForgot.setOnClickListener(v ->
                startActivity(new Intent(this, ForgotPasswordActivity.class)));

        tvRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));

        /* 7. Đăng nhập MXH -------------------------------------------- */
        ivFacebook.setOnClickListener(v -> loginWithFacebook());
        ivGoogle.setOnClickListener(v -> loginWithGoogle());
    }

    /* ---------------- LOGIN LOCAL (Room) --------------------------- */
    private void attemptLocalLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        Log.d(TAG, "attemptLocalLogin: email=" + email);

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
                    Log.d(TAG, "Room login SUCCESS → id=" + user.getId());
                    saveAuthSession(user);
                    handleRememberMe(user.getEmail());
                    goHome();
                } else {
                    Log.w(TAG, "Room login FAILED");
                    Toast.makeText(this, "Sai email hoặc mật khẩu", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    /* ---------------- LOGIN FACEBOOK ------------------------------ */
    private void loginWithFacebook() {
        Log.d(TAG, "loginWithFacebook()");
        LoginManager.getInstance().logInWithReadPermissions(
                this,
                Arrays.asList("email", "public_profile")
        );
        LoginManager.getInstance().registerCallback(
                fbCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult result) {
                        Log.d(TAG, "Facebook onSuccess");
                        AuthCredential cred = FacebookAuthProvider.getCredential(
                                result.getAccessToken().getToken());
                        firebaseSignIn(cred);
                    }
                    @Override
                    public void onCancel() {
                        Log.w(TAG, "Facebook onCancel");
                    }
                    @Override
                    public void onError(FacebookException e) {
                        Log.e(TAG, "Facebook onError", e);
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    /* ---------------- LOGIN GOOGLE -------------------------------- */
    private void loginWithGoogle() {
        Log.d(TAG, "loginWithGoogle()");
        googleLauncher.launch(googleClient.getSignInIntent());
    }

    private void handleGoogleIntent(Intent data) {
        Log.d(TAG, "handleGoogleIntent()");
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            Log.d(TAG, "Google account=" + (account != null ? account.getEmail() : "null"));
            if (account != null && account.getIdToken() != null) {
                AuthCredential cred = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                firebaseSignIn(cred);
            } else {
                throw new IllegalStateException("ID token null");
            }
        } catch (Exception e) {
            Log.e(TAG, "Google sign‑in error", e);
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /* ---------------- Firebase chung ----------------------------- */
    private void firebaseSignIn(AuthCredential cred) {
        Log.d(TAG, "firebaseSignIn()");
        mAuth.signInWithCredential(cred)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Firebase sign‑in SUCCESS");
                        saveFirebaseSession();
                        goHome();
                    } else {
                        Log.e(TAG, "Firebase sign‑in FAILED", task.getException());
                        Toast.makeText(this,
                                task.getException() != null
                                        ? task.getException().getMessage()
                                        : "Firebase sign‑in failed",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /* ---------- Lưu session cho Firebase ------------------------- */
    private void saveFirebaseSession() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;
        getSharedPreferences(PREF_AUTH, MODE_PRIVATE)
                .edit()
                .putString("email", user.getEmail())
                .putInt("userId", 0)          // chưa có trong Room
                .putBoolean("role", false)     // giả định khách
                .apply();
    }

    /* ---------- Lưu session khi login Room ----------------------- */
    private void saveAuthSession(User user) {
        getSharedPreferences(PREF_AUTH, MODE_PRIVATE)
                .edit()
                .putString("email", user.getEmail())
                .putInt("userId", user.getId())
                .putBoolean("role", user.isRole())
                .apply();
    }

    /* ---------------- Remember Me (email) ------------------------ */
    private void handleRememberMe(String email) {
        SharedPreferences sp = getSharedPreferences(PREF_LOGIN, MODE_PRIVATE);
        if (cbRemember.isChecked()) {
            sp.edit().putString(KEY_REMEMBER, email).apply();
        } else {
            sp.edit().remove(KEY_REMEMBER).apply();
        }
    }

    /* ---------------- Đi vào màn hình Home ----------------------- */
    private void goHome() {
        Log.d(TAG, "goHome()");
        boolean isAdmin = getSharedPreferences(PREF_AUTH, MODE_PRIVATE)
                .getBoolean("role", false);   // false → khách
        Class<?> target = isAdmin

                ? Home_Admin_Activity.class

                : CustomerProductListActivity.class;
        startActivity(new Intent(this, target));
        finish();
    }

    /* ------------------------------------------------------------- */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}


