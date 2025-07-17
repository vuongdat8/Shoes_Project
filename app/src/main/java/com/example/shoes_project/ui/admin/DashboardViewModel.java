package com.example.shoes_project.ui.admin; // Bạn có thể đặt trong package viewmodel hoặc ui.admin

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.shoes_project.data.AppDatabase;

public class DashboardViewModel extends AndroidViewModel {

    private final LiveData<Integer> productCount;
    private final LiveData<Integer> brandCount;

    public DashboardViewModel(@NonNull Application application) {
        super(application);
        // Lấy database instance
        AppDatabase database = AppDatabase.getInstance(application);

        // Khởi tạo LiveData bằng cách gọi các phương thức DAO
        productCount = database.productDao().getProductCount();
        brandCount = database.brandDao().getBrandCount();
    }

    // Các phương thức public để Activity có thể truy cập và quan sát LiveData
    public LiveData<Integer> getProductCount() {
        return productCount;
    }

    public LiveData<Integer> getBrandCount() {
        return brandCount;
    }
}