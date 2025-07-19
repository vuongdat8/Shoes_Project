package com.example.shoes_project.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.shoes_project.model.Category;

import java.util.List;

@Dao
public interface CategoryDAO {

    @Insert
    void insert(Category category);

    // THÊM METHOD TRẢ VỀ ID KHI INSERT
    @Insert
    long insertCategory(Category category);

    @Update
    void update(Category category);

    @Delete
    void delete(Category category);

    @Query("SELECT * FROM category")
    LiveData<List<Category>> getAllCategories();

    @Query("SELECT * FROM category WHERE id = :categoryId")
    Category getCategoryById(int categoryId);

    // THÊM METHOD TÌM CATEGORY THEO TÊN
    @Query("SELECT * FROM category WHERE name = :name LIMIT 1")
    Category getCategoryByName(String name);

    @Transaction
    @Query("SELECT * FROM category WHERE brandId = :brandId")
    List<Category> getCategoriesForBrand(int brandId);
}
