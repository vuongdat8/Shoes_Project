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
    public abstract ProductDao productDao();
    public abstract UserDao    userDao();
    public abstract CategoryDAO categoryDao();
    public abstract BrandDao brandDao();
    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
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

            // Chèn dữ liệu trong một luồng nền
            databaseWriteExecutor.execute(() -> {
                UserDao dao = INSTANCE.userDao();

                // Kiểm tra xem có người dùng nào chưa, nếu chưa thì thêm
                // (Điều này giúp tránh thêm trùng lặp nếu bạn chạy lại ứng dụng mà không xóa DB)
                if (dao.countUsers() == 0) {
                    dao.insert(new User("Tuấn", "tuan@example.com", "123456", false)); // false = khách hàng
                    dao.insert(new User("Huy", "huy@example.com", "123456", false));
                    dao.insert(new User("Đạt", "dat@example.com", "123456", false));
                    dao.insert(new User("Lộc", "loc@example.com", "123456", false));
                    dao.insert(new User("Admin", "admin@example.com", "admin123", true)); // Ví dụ thêm 1 admin
                }
            });
        }
    };
}