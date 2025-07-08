package com.example.shoes_project.data;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.shoes_project.model.Product;
import com.example.shoes_project.model.User;

@Database(
        entities = {Product.class,  User.class},
        version = 1,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    // DAOs từ database đầu tiên
    public abstract ProductDao productDao();

    // DAO từ database thứ hai
    public abstract UserDao userDao();
    private static volatile AppDatabase INSTANCE;
    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "app_database"
                            )
                            .allowMainThreadQueries() // Chỉ nên dùng cho testing
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}



//package com.example.shoes_project.data;
//
//import android.content.Context;
//
//import androidx.annotation.NonNull;
//import androidx.room.Database;
//import androidx.room.Room;
//import androidx.room.RoomDatabase;
//import androidx.sqlite.db.SupportSQLiteDatabase;
//
//import java.util.concurrent.Executors;
//
//@Database(entities = {User.class}, version = 1, exportSchema = false)
//public abstract class AppDatabase extends RoomDatabase {
//
//    public abstract UserDao userDao();
//
//    private static volatile AppDatabase INSTANCE;
//
//    /**  👇 Chỉnh ở đây  */
//    public static AppDatabase getInstance(Context context) {
//        if (INSTANCE == null) {
//            synchronized (AppDatabase.class) {
//                if (INSTANCE == null) {
//
//                    /* 1) XÓA TOÀN BỘ FILE app_db MỖI KHI ỨNG DỤNG CHẠY  */
//                    // Nếu muốn GIỮ schema và chỉ xoá dữ liệu, comment dòng này.
//                    context.deleteDatabase("app_db");
//
//                    /* 2) TẠO LẠI DATABASE  */
//                    INSTANCE = Room.databaseBuilder(
//                                    context.getApplicationContext(),
//                                    AppDatabase.class,
//                                    "app_db")
//                            /* Nếu chỉ muốn xoá bản ghi chứ không xoá file, bỏ đoạn deleteDatabase ở trên
//                               và dùng callback dưới đây  */
//                            .addCallback(new Callback() {
//                                @Override
//                                public void onOpen(@NonNull SupportSQLiteDatabase db) {
//                                    super.onOpen(db);
//                                    // Chạy nền để tránh block UI
//                                    Executors.newSingleThreadExecutor().execute(() -> {
//                                        // Xoá tất cả bảng, giữ schema
//                                        INSTANCE.clearAllTables();
//                                    });
//                                }
//                            })
//                            .build();
//                }
//            }
//        }
//        return INSTANCE;
//    }
//}
