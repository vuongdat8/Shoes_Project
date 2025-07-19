package com.example.shoes_project.data;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.shoes_project.model.Order;
import com.example.shoes_project.model.OrderItem;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OrderRepository {
    private OrderDao orderDao;
    private OrderItemDao orderItemDao;
    private ExecutorService executor;
    private Handler mainHandler;

    public OrderRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        orderDao = db.orderDao();
        orderItemDao = db.orderItemDao();
        executor = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());
    }
    public void createOrder(Order order, OnOrderCreatedListener listener) {
        executor.execute(() -> {
            try {
                long orderId = orderDao.insertOrder(order);
                mainHandler.post(() -> {
                    if (listener != null) {
                        listener.onOrderCreated(true, (int) orderId);
                    }
                });
            } catch (Exception e) {
                mainHandler.post(() -> {
                    if (listener != null) {
                        listener.onOrderCreated(false, -1);
                    }
                });
            }
        });
    }
    public void getOrdersByUserId(int userId, OnOrdersLoadedListener listener) {
        executor.execute(() -> {
            try {
                List<Order> orders = orderDao.getOrdersByUserId(userId);
                mainHandler.post(() -> {
                    if (listener != null) {
                        listener.onOrdersLoaded(orders);
                    }
                });
            } catch (Exception e) {
                mainHandler.post(() -> {
                    if (listener != null) {
                        listener.onOrdersLoaded(null);
                    }
                });
            }
        });
    }
    public void getOrderById(int orderId, OnOrderLoadedListener listener) {
        executor.execute(() -> {
            try {
                Order order = orderDao.getOrderById(orderId);
                mainHandler.post(() -> {
                    if (listener != null) {
                        listener.onOrderLoaded(order);
                    }
                });
            } catch (Exception e) {
                Log.e("OrderRepository", "Error creating order", e);
                mainHandler.post(() -> {
                    if (listener != null) {
                        listener.onOrderLoaded(null);
                    }
                });
            }
        });
    }
    public void getAllOrders(OnOrdersLoadedListener listener) {
        executor.execute(() -> {
            try {
                List<Order> orders = orderDao.getAllOrders();
                mainHandler.post(() -> {
                    if (listener != null) {
                        listener.onOrdersLoaded(orders);
                    }
                });
            } catch (Exception e) {
                mainHandler.post(() -> {
                    if (listener != null) {
                        listener.onOrdersLoaded(null);
                    }
                });
            }
        });
    }
    public void updateOrderStatus(int orderId, String status, OnOrderStatusUpdatedListener listener) {
        executor.execute(() -> {
            try {
                orderDao.updateOrderStatus(orderId, status);
                mainHandler.post(() -> {
                    if (listener != null) {
                        listener.onOrderStatusUpdated(true);
                    }
                });
            } catch (Exception e) {
                mainHandler.post(() -> {
                    if (listener != null) {
                        listener.onOrderStatusUpdated(false);
                    }
                });
            }
        });
    }
    public void createOrderItems(List<OrderItem> orderItems, OnOrderItemsCreatedListener listener) {
        executor.execute(() -> {
            try {
                orderItemDao.insertOrderItems(orderItems);
                mainHandler.post(() -> {
                    if (listener != null) {
                        listener.onOrderItemsCreated(true);
                    }
                });
            } catch (Exception e) {
                mainHandler.post(() -> {
                    if (listener != null) {
                        listener.onOrderItemsCreated(false);
                    }
                });
            }
        });
    }
    public void getOrderItemsByOrderId(int orderId, OnOrderItemsLoadedListener listener) {
        executor.execute(() -> {
            try {
                List<OrderItem> orderItems = orderItemDao.getOrderItemsByOrderId(orderId);
                mainHandler.post(() -> {
                    if (listener != null) {
                        listener.onOrderItemsLoaded(orderItems);
                    }
                });
            } catch (Exception e) {
                mainHandler.post(() -> {
                    if (listener != null) {
                        listener.onOrderItemsLoaded(null);
                    }
                });
            }
        });
    }
    public void createCompleteOrder(Order order, List<OrderItem> orderItems, OnCompleteOrderCreatedListener listener) {
        executor.execute(() -> {
            try {
                long orderId = orderDao.insertOrder(order);

                for (OrderItem item : orderItems) {
                    item.setOrderId((int) orderId);
                }

                orderItemDao.insertOrderItems(orderItems);

                mainHandler.post(() -> {
                    if (listener != null) {
                        listener.onCompleteOrderCreated(true, (int) orderId);
                    }
                });
            } catch (Exception e) {
                mainHandler.post(() -> {
                    if (listener != null) {
                        listener.onCompleteOrderCreated(false, -1);
                    }
                });
            }
        });
    }
    // Lấy tất cả orders với details (JOIN với User và OrderItems)
//    public void getAllOrdersWithDetails(OnOrdersWithDetailsLoadedListener listener) {
//        executor.execute(() -> {
//            try {
//                List<OrderWithDetails> orders = orderDao.getAllOrdersWithDetails();
//                mainHandler.post(() -> {
//                    if (listener != null) {
//                        listener.onOrdersWithDetailsLoaded(orders);
//                    }
//                });
//            } catch (Exception e) {
//                mainHandler.post(() -> {
//                    if (listener != null) {
//                        listener.onOrdersWithDetailsLoaded(null);
//                    }
//                });
//            }
//        });
//    }
//
//    // Lấy order với details theo ID
//    public void getOrderWithDetailsById(int orderId, OnOrderWithDetailsLoadedListener listener) {
//        executor.execute(() -> {
//            try {
//                OrderWithDetails order = orderDao.getOrderWithDetailsById(orderId);
//                mainHandler.post(() -> {
//                    if (listener != null) {
//                        listener.onOrderWithDetailsLoaded(order);
//                    }
//                });
//            } catch (Exception e) {
//                mainHandler.post(() -> {
//                    if (listener != null) {
//                        listener.onOrderWithDetailsLoaded(null);
//                    }
//                });
//            }
//        });
//    }
//
//    // Lấy orders với details theo user ID
//    public void getOrdersWithDetailsByUserId(int userId, OnOrdersWithDetailsLoadedListener listener) {
//        executor.execute(() -> {
//            try {
//                List<OrderWithDetails> orders = orderDao.getOrdersWithDetailsByUserId(userId);
//                mainHandler.post(() -> {
//                    if (listener != null) {
//                        listener.onOrdersWithDetailsLoaded(orders);
//                    }
//                });
//            } catch (Exception e) {
//                mainHandler.post(() -> {
//                    if (listener != null) {
//                        listener.onOrdersWithDetailsLoaded(null);
//                    }
//                });
//            }
//        });
//    }
//
//    // Lấy orders với details theo status
//    public void getOrdersWithDetailsByStatus(String status, OnOrdersWithDetailsLoadedListener listener) {
//        executor.execute(() -> {
//            try {
//                List<OrderWithDetails> orders = orderDao.getOrdersWithDetailsByStatus(status);
//                mainHandler.post(() -> {
//                    if (listener != null) {
//                        listener.onOrdersWithDetailsLoaded(orders);
//                    }
//                });
//            } catch (Exception e) {
//                mainHandler.post(() -> {
//                    if (listener != null) {
//                        listener.onOrdersWithDetailsLoaded(null);
//                    }
//                });
//            }
//        });
//    }
//
//    // Lấy orders với details theo customer name
//    public void getOrdersWithDetailsByCustomerName(String customerName, OnOrdersWithDetailsLoadedListener listener) {
//        executor.execute(() -> {
//            try {
//                List<OrderWithDetails> orders = orderDao.getOrdersWithDetailsByCustomerName(customerName);
//                mainHandler.post(() -> {
//                    if (listener != null) {
//                        listener.onOrdersWithDetailsLoaded(orders);
//                    }
//                });
//            } catch (Exception e) {
//                mainHandler.post(() -> {
//                    if (listener != null) {
//                        listener.onOrdersWithDetailsLoaded(null);
//                    }
//                });
//            }
//        });
//    }
//
//    // Lấy orders với details theo payment method
//    public void getOrdersWithDetailsByPaymentMethod(String paymentMethod, OnOrdersWithDetailsLoadedListener listener) {
//        executor.execute(() -> {
//            try {
//                List<OrderWithDetails> orders = orderDao.getOrdersWithDetailsByPaymentMethod(paymentMethod);
//                mainHandler.post(() -> {
//                    if (listener != null) {
//                        listener.onOrdersWithDetailsLoaded(orders);
//                    }
//                });
//            } catch (Exception e) {
//                mainHandler.post(() -> {
//                    if (listener != null) {
//                        listener.onOrdersWithDetailsLoaded(null);
//                    }
//                });
//            }
//        });
//    }
//
//    // Lấy orders với details với tổng tiền >= amount
//    public void getOrdersWithDetailsByMinAmount(double minAmount, OnOrdersWithDetailsLoadedListener listener) {
//        executor.execute(() -> {
//            try {
//                List<OrderWithDetails> orders = orderDao.getOrdersWithDetailsByMinAmount(minAmount);
//                mainHandler.post(() -> {
//                    if (listener != null) {
//                        listener.onOrdersWithDetailsLoaded(orders);
//                    }
//                });
//            } catch (Exception e) {
//                mainHandler.post(() -> {
//                    if (listener != null) {
//                        listener.onOrdersWithDetailsLoaded(null);
//                    }
//                });
//            }
//        });
//    }
//
//    // Lấy orders với details trong khoảng thời gian
//    public void getOrdersWithDetailsByDateRange(String startDate, String endDate, OnOrdersWithDetailsLoadedListener listener) {
//        executor.execute(() -> {
//            try {
//                List<OrderWithDetails> orders = orderDao.getOrdersWithDetailsByDateRange(startDate, endDate);
//                mainHandler.post(() -> {
//                    if (listener != null) {
//                        listener.onOrdersWithDetailsLoaded(orders);
//                    }
//                });
//            } catch (Exception e) {
//                mainHandler.post(() -> {
//                    if (listener != null) {
//                        listener.onOrdersWithDetailsLoaded(null);
//                    }
//                });
//            }
//        });
//    }
    public interface OnOrderCreatedListener {
        void onOrderCreated(boolean success, int orderId);
    }
    public interface OnOrdersLoadedListener {
        void onOrdersLoaded(List<Order> orders);
    }
    public interface OnOrderLoadedListener {
        void onOrderLoaded(Order order);
    }

    public interface OnOrderStatusUpdatedListener {
        void onOrderStatusUpdated(boolean success);
    }
    public interface OnOrderItemsCreatedListener {
        void onOrderItemsCreated(boolean success);
    }

    public interface OnOrderItemsLoadedListener {
        void onOrderItemsLoaded(List<OrderItem> orderItems);
    }

    public interface OnCompleteOrderCreatedListener {
        void onCompleteOrderCreated(boolean success, int orderId);
    }
//    public interface OnOrdersWithDetailsLoadedListener {
//        void onOrdersWithDetailsLoaded(List<OrderWithDetails> orders);
//    }
//
//    public interface OnOrderWithDetailsLoadedListener {
//        void onOrderWithDetailsLoaded(OrderWithDetails order);
//    }
//
//    public interface OnOrderItemsWithProductLoadedListener {
//        void onOrderItemsWithProductLoaded(List<OrderItemWithProduct> orderItems);
//    }
}
