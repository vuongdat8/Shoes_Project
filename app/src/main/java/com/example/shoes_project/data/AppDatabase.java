package com.example.shoes_project.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.shoes_project.model.Brand;
import com.example.shoes_project.model.Product;
import com.example.shoes_project.model.User;

@Database(
        entities = {Product.class, User.class, Brand.class},
        version =2,               // ⬆️ Tăng version mỗi khi đổi schema
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    /* ---------- DAO ---------- */
    public abstract ProductDao productDao();
    public abstract UserDao    userDao();
    public abstract BrandDao brandDao() ;

    /* ---------- Singleton ---------- */
    private static volatile AppDatabase INSTANCE;

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
                            .allowMainThreadQueries()          // Chỉ dùng khi testing
                            .build();
                }
            }
        }
        return INSTANCE;
    }


}
