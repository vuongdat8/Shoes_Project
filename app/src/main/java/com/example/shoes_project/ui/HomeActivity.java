package com.example.shoes_project.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.shoes_project.R;

public class HomeActivity extends AppCompatActivity {

    private String currentEmail;   // email người đang đăng nhập

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        /* Đọc email từ SharedPreferences */
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        currentEmail = prefs.getString("email", null);

        Button btnProfile = findViewById(R.id.profile);
        btnProfile.setOnClickListener(v -> openProfileScreen());
    }

    private void openProfileScreen() {
        if (currentEmail == null) {
            Toast.makeText(this, "Phiên đăng nhập đã hết, vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
            // startActivity(new Intent(this, LoginActivity.class));
            return;
        }
        Intent intent = new Intent(this, UserProfileActivity.class);
        intent.putExtra("email", currentEmail);   // gửi email sang Profile
        startActivity(intent);
    }
}
