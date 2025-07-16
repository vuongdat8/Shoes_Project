package com.example.shoes_project.data;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.shoes_project.model.Brand;
import com.example.shoes_project.model.CartItem;
import com.example.shoes_project.model.Product;
import com.example.shoes_project.model.User;
import com.example.shoes_project.model.Category;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
        entities = {Product.class, User.class, Category.class, Brand.class, CartItem.class},
        version = 2,               // ⬆️ Tăng version mỗi khi đổi schema (đảm bảo là 2 hoặc cao hơn)
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    /* ---------- DAO ---------- */
    public abstract ProductDao productDao();
    public abstract UserDao    userDao();
    public abstract CategoryDAO categoryDao();
    public abstract BrandDao brandDao();
    public abstract CartItemDao cartItemDao();


    /* ---------- Singleton ---------- */
    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4; // Số luồng cho ExecutorService
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "app_db"
                            )
                            .fallbackToDestructiveMigration()  // ⚠ Xoá DB cũ khi schema đổi
                            .addCallback(sRoomDatabaseCallback) // THÊM DÒNG NÀY
                            .allowMainThreadQueries()          // Chỉ dùng khi testing (có thể bỏ đi nếu dùng ExecutorService)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // Callback để chèn dữ liệu khi DB được tạo
    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
//
//            // Chèn dữ liệu trong một luồng nền
//            databaseWriteExecutor.execute(() -> {
//                ProductDao dao = INSTANCE.productDao();
//                Log.d("DB_INIT", "Product count: " + dao.getProductCountDirect());

//                if (dao.getProductCountDirect() == 0) {
//                    dao.insertProduct(new Product(
//                            "Nike Air Force 1", "Nike", "Sneakers",
//                            2000000, 10, 2200000,
//                            "R.drawable.a1",  // ✅ truyền tên file ảnh (không có đuôi .webp)
//                            42,
//                            "Classic white sneaker",
//                            "White"
//                    ));
//                    dao.insertProduct(new Product(
//                            "Adidas Ultraboost", "Adidas", "Running",
//                            2500000, 15, 2700000,
//                            "https://example.com/ultraboost.jpg", 43, "Comfortable running shoe", "Black"));
//
//                    dao.insertProduct(new Product(
//                            "Converse Chuck 70", "Converse", "Casual",
//                            1500000, 20, 1600000,
//                            "https://example.com/chuck70.jpg", 41, "High-top classic design", "Red"));
//
//                    dao.insertProduct(new Product(
//                            "Vans Old Skool", "Vans", "Skateboarding",
//                            1200000, 12, 1300000,
//                            "https://example.com/oldskool.jpg", 42.5, "Iconic skateboarding shoe", "Black"));
//
//                    dao.insertProduct(new Product(
//                            "Puma RS-X", "Puma", "Lifestyle",
//                            1800000, 8, 1900000,
//                            "https://example.com/rsx.jpg", 44, "Retro design with modern tech", "Green"));
//
//                    // ⬇️ THÊM CỦA BẠN
//                    dao.insertProduct(new Product(
//                            "Giày Thể Thao Nam", "Nike", "Thể thao",
//                            1500000, 50, 1700000,
//                            "https://example.com/shoes1.jpg", 42.0,
//                            "Giày chạy bộ nhẹ, bền", "Đen"
//                    ));
//
//                    dao.insertProduct(new Product(
//                            "Giày Sneaker Nữ", "Adidas", "Thời trang",
//                            1300000, 30, 1500000,
//                            "https://example.com/shoes2.jpg", 38.0,
//                            "Thiết kế trẻ trung, năng động", "Trắng"
//                    ));

                    // 👟 Thêm sản phẩm khác nếu muốn ở đây...
//                }
//            });
        };
    };


}