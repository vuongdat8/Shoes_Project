package com.example.shoes_project.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.shoes_project.model.Brand;

import java.util.List;

@Dao
public interface BrandDao {

    @Insert
    void insert(Brand brand);

    // THÊM METHOD TRẢ VỀ ID KHI INSERT
    @Insert
    long insertBrand(Brand brand);

    @Update
    void update(Brand brand);

    @Delete
    void delete(Brand brand);

    @Query("SELECT * FROM brand")
    LiveData<List<Brand>> getAllBrand();

    @Query("SELECT * FROM brand WHERE id = :brandId")
    Brand getBrandById(int brandId);

    // THÊM METHOD TÌM BRAND THEO TÊN
    @Query("SELECT * FROM brand WHERE name = :name LIMIT 1")
    Brand getBrandByName(String name);

    @Query("SELECT COUNT(*) FROM brand")
    LiveData<Integer> getBrandCount();
}