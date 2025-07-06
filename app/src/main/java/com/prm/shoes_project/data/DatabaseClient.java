package com.prm.shoes_project.data;

import android.content.Context;
import android.os.Handler;

import androidx.room.Room;

import com.prm.shoes_project.data.db.AppDatabase;

import java.util.concurrent.ExecutorService;

public class DatabaseClient {
    private static DatabaseClient instance;
    private AppDatabase db;
    private ExecutorService executorService;
    private Handler mainHandler;

    private DatabaseClient(Context context) {
        db = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, "app_database")
                .fallbackToDestructiveMigration() // This line allows the database to be recreated on schema changes
                .build();
        executorService = java.util.concurrent.Executors.newSingleThreadExecutor();
        mainHandler = new Handler(android.os.Looper.getMainLooper());
    }

    public static synchronized DatabaseClient getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseClient(context);
        }
        return instance;
    }

    public AppDatabase getAppDatabase() {
        return db;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public Handler getMainHandler() {
        return mainHandler;
    }
}
