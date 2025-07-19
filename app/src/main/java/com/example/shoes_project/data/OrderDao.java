package com.example.shoes_project.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.shoes_project.model.Order;

import java.util.List;

@Dao
public interface OrderDao {
    // Lấy tất cả đơn hàng
    @Query("SELECT * FROM orders")
    List<Order> getAllOrders();

    // Lấy đơn hàng theo ID
    @Query("SELECT * FROM orders WHERE orderId = :orderId")
    Order getOrderById(int orderId);

    // Lấy đơn hàng theo user ID
    @Query("SELECT * FROM orders WHERE userId = :userId ORDER BY orderDate DESC")
    List<Order> getOrdersByUserId(int userId);

    // Lấy đơn hàng theo trạng thái
    @Query("SELECT * FROM orders WHERE orderStatus = :status")
    List<Order> getOrdersByStatus(String status);

    // Thêm đơn hàng mới (trả về ID)
    @Insert
    long insertOrder(Order order);

    // Cập nhật đơn hàng
    @Update
    void updateOrder(Order order);

    // Xóa đơn hàng
    @Delete
    void deleteOrder(Order order);

    // Cập nhật trạng thái đơn hàng
    @Query("UPDATE orders SET orderStatus = :status WHERE orderId = :orderId")
    void updateOrderStatus(int orderId, String status);

    // Đếm số đơn hàng
    @Query("SELECT COUNT(*) FROM orders")
    int getOrderCount();

    // Tính tổng doanh thu
    @Query("SELECT SUM(totalAmount) FROM orders WHERE orderStatus = 'delivered'")
    double getTotalRevenue();
    // Lấy tất cả orders với details (JOIN với User và OrderItems)
//    @Transaction
//    @Query("SELECT * FROM orders ORDER BY orderDate DESC")
//    List<OrderWithDetails> getAllOrdersWithDetails();
//
//    // Lấy order với details theo ID
//    @Transaction
//    @Query("SELECT * FROM orders WHERE orderId = :orderId")
//    OrderWithDetails getOrderWithDetailsById(int orderId);
//
//    // Lấy orders với details theo user ID
//    @Transaction
//    @Query("SELECT * FROM orders WHERE userId = :userId ORDER BY orderDate DESC")
//    List<OrderWithDetails> getOrdersWithDetailsByUserId(int userId);
//
//    // Lấy orders với details theo status
//    @Transaction
//    @Query("SELECT * FROM orders WHERE orderStatus = :status ORDER BY orderDate DESC")
//    List<OrderWithDetails> getOrdersWithDetailsByStatus(String status);
//
//    // Lấy orders với details trong khoảng thời gian
//    @Transaction
//    @Query("SELECT * FROM orders WHERE orderDate BETWEEN :startDate AND :endDate ORDER BY orderDate DESC")
//    List<OrderWithDetails> getOrdersWithDetailsByDateRange(String startDate, String endDate);
//
//    // Lấy orders với details theo customer name
//    @Transaction
//    @Query("SELECT * FROM orders WHERE customerName LIKE '%' || :customerName || '%' ORDER BY orderDate DESC")
//    List<OrderWithDetails> getOrdersWithDetailsByCustomerName(String customerName);
//
//    // Lấy orders với details theo payment method
//    @Transaction
//    @Query("SELECT * FROM orders WHERE paymentMethod = :paymentMethod ORDER BY orderDate DESC")
//    List<OrderWithDetails> getOrdersWithDetailsByPaymentMethod(String paymentMethod);
//
//    // Lấy orders với details với tổng tiền >= amount
//    @Transaction
//    @Query("SELECT * FROM orders WHERE totalAmount >= :minAmount ORDER BY totalAmount DESC")
//    List<OrderWithDetails> getOrdersWithDetailsByMinAmount(double minAmount);
    @Query("SELECT COUNT(*) FROM orders WHERE strftime('%m', orderDate) = :month AND strftime('%Y', orderDate) = :year")
    int countOrdersByMonth(String month, String year);

}
