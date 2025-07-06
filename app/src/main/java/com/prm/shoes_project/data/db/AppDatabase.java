package com.prm.shoes_project.data.db;

import android.icu.util.ULocale;
import android.os.Message;
import androidx.room.Database;
import androidx.room.FtsOptions;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.prm.shoes_project.data.db.dao.ProductDao;
import com.prm.shoes_project.data.db.entity.Product;


@Database(entities = {Product.class, FtsOptions.Order.class, Message.class, ULocale.Category.class},
        version = 4,
        exportSchema = true
)
@TypeConverters({DateConverter.class})

public abstract class AppDatabase extends RoomDatabase {

    public abstract ProductDao productDao();

}