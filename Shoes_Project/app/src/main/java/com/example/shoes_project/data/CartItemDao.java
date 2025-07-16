package com.example.shoes_project.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.shoes_project.model.CartItem;

import java.util.List;


@Dao
public interface CartItemDao {
    @Query("SELECT * FROM cart_items  WHERE userId = :userId")
    List<CartItem> getCartItemsByUserId(int userId);

    @Query("SELECT * FROM cart_items  WHERE userId = :userId AND isSelected = 1")
    List<CartItem> getSelectedCartItems(int userId);

    @Query("SELECT * FROM cart_items  WHERE userId = :userId AND productId = :productId AND selectedColor = :color AND selectedSize = :size")
    CartItem getCartItemByDetails(int userId, int productId, String color, String size);

    @Insert
    void insertCartItem(CartItem cart_items );

    @Update
    void updateCartItem(CartItem cart_items );

    @Delete
    void deleteCartItem(CartItem cart_items );

    @Query("DELETE FROM cart_items  WHERE userId = :userId AND isSelected = 1")
    void deleteSelectedCartItems(int userId);

    @Query("DELETE FROM cart_items  WHERE userId = :userId")
    void deleteAllCartItems(int userId);

    @Query("UPDATE cart_items  SET isSelected = :selected WHERE userId = :userId")
    void updateAllCartItemsSelection(int userId, boolean selected);

    @Query("SELECT COUNT(*) FROM cart_items  WHERE userId = :userId")
    int getCartItemCount(int userId);

    @Query("SELECT SUM(price * quantity) FROM cart_items  WHERE userId = :userId AND isSelected = 1")
    double getTotalSelectedPrice(int userId);

    @Query("SELECT COUNT(*) FROM cart_items  WHERE userId = :userId AND isSelected = 1")
    int getSelectedItemCount(int userId);
}


