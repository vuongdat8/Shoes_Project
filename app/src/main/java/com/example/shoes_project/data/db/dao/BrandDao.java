package com.example.shoes_project.data.db.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.shoes_project.data.db.entity.Brand;

import java.util.List;
import java.util.UUID;

@Dao
public interface BrandDao extends BaseDao<Brand> {
    @Query("SELECT * FROM brands")
    List<Brand> getAllBrands();

    @Query("SELECT * FROM brands WHERE brandId = :brandId")
    Brand getBrandById(UUID brandId);

    @Query("SELECT * FROM brands WHERE name = :name")
    Brand getBrandByName(String name);

    @Query("SELECT COUNT(*) FROM products WHERE brandId = :brandId")
    int getProductCountByBrand(UUID brandId); // Thêm phương thức đếm số sản phẩm
}
