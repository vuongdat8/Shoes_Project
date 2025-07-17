package com.example.shoes_project.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;
import com.example.shoes_project.model.Product;
import com.example.shoes_project.model.ProductWithDetails;

import java.util.List;

@Dao
public interface ProductDao {

    @Query("SELECT * FROM product")
    List<Product> getAllProducts();

    @Query("SELECT * FROM product WHERE id = :id")
    Product getProductById(int id);

    @Query("SELECT * FROM product WHERE categoryId = :categoryId")
    List<Product> getProductsByCategoryId(int categoryId);

    @Query("SELECT * FROM product WHERE brandId = :brandId")
    List<Product> getProductsByBrandId(int brandId);

    @Query("SELECT * FROM product WHERE productName LIKE '%' || :name || '%'")
    List<Product> searchProductsByName(String name);

    @Insert
    void insertProduct(Product product);

    @Update
    void updateProduct(Product product);

    @Delete
    void deleteProduct(Product product);

    @Query("DELETE FROM product WHERE id = :id")
    void deleteProductById(int id);


    @Query("SELECT * FROM product WHERE quantity <= :threshold")
    List<Product> getLowStockProducts(int threshold);

    @Query("SELECT COUNT(*) FROM product")
    LiveData<Integer> getProductCount();
    @Transaction
    @Query("SELECT * FROM product")
    List<ProductWithDetails> getAllProductsWithDetails();
    @Transaction
    @Query("SELECT * FROM Product WHERE id = :productId")
    ProductWithDetails getProductWithDetailsById(int productId);

    @Transaction
    @Query("SELECT * FROM product WHERE productName LIKE :query")
    List<ProductWithDetails> searchProductsWithDetailsByName(String query);
}

