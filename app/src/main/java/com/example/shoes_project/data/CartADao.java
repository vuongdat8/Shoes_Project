package com.example.shoes_project.data;

import static android.provider.SyncStateContract.Helpers.insert;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.shoes_project.model.CartA;

import java.util.List;


@Dao
public interface CartADao {
    @Query("SELECT * FROM cart_items  WHERE userId = :userId")
    List<CartA> getCartAsByUserId(int userId);

    @Query("SELECT * FROM cart_items  WHERE userId = :userId AND isSelected = 1")
    List<CartA> getSelectedCartAs(int userId);

    @Query("SELECT * FROM cart_items  WHERE userId = :userId AND productId = :productId AND selectedColor = :color AND selectedSize = :size")
    CartA getCartAByDetails(int userId, int productId, String color, double size);

    @Insert
    void insertCartA(CartA cart_items );

    @Update
    void updateCartA(CartA cart_items );

    @Delete
    void deleteCartA(CartA cart_items );

    @Query("DELETE FROM cart_items  WHERE userId = :userId AND isSelected = 1")
    void deleteSelectedCartAs(int userId);

    @Query("DELETE FROM cart_items  WHERE userId = :userId")
    void deleteAllCartAs(int userId);

    @Query("UPDATE cart_items  SET isSelected = :selected WHERE userId = :userId")
    void updateAllCartAsSelection(int userId, boolean selected);

    @Query("SELECT COUNT(*) FROM cart_items  WHERE userId = :userId")
    int getCartACount(int userId);

    @Query("SELECT SUM(price * quantity) FROM cart_items  WHERE userId = :userId AND isSelected = 1")
    double getTotalSelectedPrice(int userId);

    @Query("SELECT COUNT(*) FROM cart_items  WHERE userId = :userId AND isSelected = 1")
    int getSelectedItemCount(int userId);
    @Query("SELECT * FROM cart_items WHERE userId = :userId AND productId = :productId AND selectedColor = :color AND selectedSize = :size LIMIT 1")
    CartA getExistingCartA(int userId, int productId, String color, Double size);

    @Transaction
    default void addToCart(int userId, int productId, String color, Double size) {
        CartA existing = getExistingCartA(userId, productId, color, size);
        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + 1);
            updateCartA(existing);
        } else {
            CartA newItem = new CartA(userId, productId, color, size);

            insertCartA(newItem);
        }
    }


}


