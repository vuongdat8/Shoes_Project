package com.example.shoes_project.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.shoes_project.model.OrderDetail;

import java.util.List;

@Dao
public interface OrderDetailDao {
    @Insert
    void insert(OrderDetail detail);

    @Query("SELECT * FROM `OrderDetail` WHERE orderId = :orderId")
    List<OrderDetail> getDetailsByOrderId(int orderId);



}
