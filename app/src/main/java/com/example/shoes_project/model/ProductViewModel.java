package com.example.shoes_project.model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.shoes_project.data.AppDatabase;

import java.util.List;

public class ProductViewModel extends AndroidViewModel {

    private final AppDatabase db;
    private final LiveData<List<Brand>> brandList;
    private final LiveData<List<Category>> categoryList;

    public ProductViewModel(@NonNull Application application) {
        super(application);
        db = AppDatabase.getInstance(application);
        brandList = db.brandDao().getAllBrand();
        categoryList = db.categoryDao().getAllCategories();
    }

    public LiveData<List<Brand>> getBrandList() {
        return brandList;
    }

    public LiveData<List<Category>> getCategoryList() {
        return categoryList;
    }
}
