package com.example.shoes_project.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.shoes_project.data.dao.OrderDao;
import com.example.shoes_project.data.dao.OrderDetailDao;
import com.example.shoes_project.model.Order;
import com.example.shoes_project.data.User;
import com.example.shoes_project.model.OrderDetail;

@Database(
        entities = {Order.class, User.class, OrderDetail.class},
        version = 2,               // ⬆️ Tăng version mỗi khi đổi schema
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    /* ---------- DAO ---------- */
    public abstract OrderDao orderDao();
    public abstract UserDao    userDao();
    public abstract OrderDetailDao orderDetailDao();

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