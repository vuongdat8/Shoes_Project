package com.example.shoes_project.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.shoes_project.model.Brand;
import com.example.shoes_project.model.Product;
import com.example.shoes_project.model.User;
import com.example.shoes_project.model.Category;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
        entities = {Product.class, User.class, Category.class, Brand.class},
        version = 2,               // ⬆️ Tăng version mỗi khi đổi schema (đảm bảo là 2 hoặc cao hơn)
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    /* ---------- DAO ---------- */
    public abstract ProductDao productDao();
    public abstract UserDao    userDao();
    public abstract CategoryDAO categoryDao();
    public abstract BrandDao brandDao();

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
                            .fallbackToDestructiveMigration()  // ⚠ Xoá DB cũ khi schema đổi// THÊM DÒNG NÀY
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // Callback để chèn dữ liệu khi DB được tạo

}