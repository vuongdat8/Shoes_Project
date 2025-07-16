package com.example.shoes_project.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.shoes_project.model.CartItem;
import com.example.shoes_project.model.CartItemWithProduct;

import java.util.List;

@Dao
public interface CartDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CartItem cart_items);

    @Query("SELECT * FROM cart_items")
    LiveData<List<CartItem>> getAllCartItems();

    @Update
    void update(CartItem cart_items);

    @Delete
    void delete(CartItem cart_items);

    @Query("DELETE FROM cart_items")
    void clearCart();
    @Transaction
    @Query("SELECT * FROM cart_items WHERE userId = :userId")
    List<CartItemWithProduct> getCartItemsWithProduct(int userId);
    @Query("SELECT * FROM cart_items WHERE userId = :userId AND productId = :productId AND selectedColor = :color AND selectedSize = :size LIMIT 1")
    CartItem getExistingCartItem(int userId, int productId, String color, String size);

    @Transaction
    default void addToCart(int userId, int productId, String color, String size) {
        CartItem existing = getExistingCartItem(userId, productId, color, size);
        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + 1);
            insert(existing);
        } else {
            CartItem newItem = new CartItem(userId, productId, color, size);

            insert(newItem);
        }
    }
}
