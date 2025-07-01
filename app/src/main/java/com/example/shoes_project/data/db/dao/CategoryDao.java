package com.example.shoes_project.data.db.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.shoes_project.data.db.entity.Category;

import java.util.List;
import java.util.UUID;
@Dao
public interface CategoryDao extends BaseDao<Category> {
    @Query("SELECT * FROM categories")
    List<Category> getAllCategories();

    @Query("SELECT * FROM categories WHERE categoryId = :categoryId")
    Category getCategoryById(UUID categoryId);

    @Query("SELECT * FROM categories WHERE name = :name")
    Category getCategoryByName(String name);

    @Query("SELECT COUNT(*) FROM products WHERE categoryId = :categoryId")
    int getProductCountByCategory(UUID categoryId); // Thêm phương thức đếm số sản phẩm
}
