package com.example.shoes_project.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.shoes_project.model.OrderItem;

import java.util.List;
@Dao
public interface OrderItemDao {
    // Lấy tất cả order items
    @Query("SELECT * FROM order_items")
    List<OrderItem> getAllOrderItems();

    // Lấy order items theo order ID
    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    List<OrderItem> getOrderItemsByOrderId(int orderId);

    // Lấy order items theo product ID
    @Query("SELECT * FROM order_items WHERE productId = :productId")
    List<OrderItem> getOrderItemsByProductId(int productId);

    // Thêm order item
    @Insert
    void insertOrderItem(OrderItem orderItem);

    // Thêm nhiều order items
    @Insert
    void insertOrderItems(List<OrderItem> orderItems);

    // Cập nhật order item
    @Update
    void updateOrderItem(OrderItem orderItem);

    // Xóa order item
    @Delete
    void deleteOrderItem(OrderItem orderItem);

    // Xóa order items theo order ID
    @Query("DELETE FROM order_items WHERE orderId = :orderId")
    void deleteOrderItemsByOrderId(int orderId);

    // Tính tổng tiền theo order ID
    @Query("SELECT SUM(subtotal) FROM order_items WHERE orderId = :orderId")
    double getTotalAmountByOrderId(int orderId);
}
