package com.example.shoes_project.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.shoes_project.model.Product;
import java.util.List;

@Dao
public interface ProductDao {

    @Query("SELECT * FROM product")
    List<Product> getAllProducts();

    @Query("SELECT * FROM product WHERE id = :id")
    Product getProductById(int id);

    @Query("SELECT * FROM product WHERE categoryName = :category")
    List<Product> getProductsByCategory(String category);

    @Query("SELECT * FROM product WHERE brand = :brand")
    List<Product> getProductsByBrand(String brand);

    @Query("SELECT * FROM product WHERE productName LIKE :name")
    List<Product> searchProductsByName(String name);

    @Insert
    void insertProduct(Product product);

    @Insert
    void insertProducts(List<Product> products);

    @Update
    void updateProduct(Product product);

    @Delete
    void deleteProduct(Product product);

    @Query("DELETE FROM product WHERE id = :id")
    void deleteProductById(int id);

    @Query("DELETE FROM product")
    void deleteAllProducts();

    @Query("SELECT COUNT(*) FROM product")
    int getProductCount();

    @Query("SELECT * FROM product WHERE quantity > 0")
    List<Product> getAvailableProducts();

    @Query("SELECT * FROM product WHERE quantity <= :threshold")
    List<Product> getLowStockProducts(int threshold);
}
