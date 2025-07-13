package com.example.shoes_project.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.shoes_project.model.Order;

import java.util.List;

@Dao
public interface OrderDao {
    @Insert
    void insert(Order order);

    @Query("SELECT * FROM `Order` WHERE userId = :userId ORDER BY orderId DESC")
    List<Order> getOrdersByUserId(int userId);

    @Query("SELECT * FROM `Order` ORDER BY orderId DESC")
    List<Order> getAllOrders();

    @Query("SELECT * FROM `Order` WHERE orderId = :orderId LIMIT 1")
    Order getOrderById(int orderId);
}
