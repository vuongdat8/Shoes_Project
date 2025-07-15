package com.example.shoes_project.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

import com.example.shoes_project.R;
import com.example.shoes_project.ui.HomeActivity;
import com.example.shoes_project.ui.UserProfileActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class Home_Admin_Activity extends AppCompatActivity {

    private MaterialButton btnProfile;
    private MaterialCardView cardImage1, cardImage2, cardImage3, cardImage4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity_first);  // Gắn layout vào activity

        // Ánh xạ các phần tử trong XML
        btnProfile = findViewById(R.id.btn_profile);
        cardImage1 = findViewById(R.id.card_image1);
        cardImage2 = findViewById(R.id.card_image2);
        cardImage3 = findViewById(R.id.card_image3);
        cardImage4 = findViewById(R.id.card_image4);


        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mở màn hình Profile khi nút Profile được nhấn
                Intent intent = new Intent(Home_Admin_Activity.this, UserProfileActivity.class);
                startActivity(intent);
            }
        });

        // Xử lý sự kiện click cho các card (ví dụ: Card Image 1)
        cardImage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mở màn hình ProductActivity khi nhấn vào card image 1
                Intent intent = new Intent(Home_Admin_Activity.this, Brand_Admin_Activity.class);
                startActivity(intent);
            }
        });

        // Xử lý sự kiện click cho các card (Card Image 2)
        cardImage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mở màn hình CategoryActivity khi nhấn vào card image 2
                Intent intent = new Intent(Home_Admin_Activity.this, Order_Admin_Activity.class);
                startActivity(intent);
            }
        });

       //  Xử lý sự kiện click cho các card (Card Image 3)
        cardImage3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mở màn hình BrandActivity khi nhấn vào card image 3
                Intent intent = new Intent(Home_Admin_Activity.this, Customer_List_Activity.class);
                startActivity(intent);
            }
        });

        // Xử lý sự kiện click cho các card (Card Image 4)
        cardImage4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mở màn hình CategoryActivity khi nhấn vào card image 2
                Intent intent = new Intent(Home_Admin_Activity.this, DashBoard_Admin.class);
                startActivity(intent);
            }
        });
    }
}
