package com.example.shoes_project.model;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OrderWithDetails {
    @Embedded
    public Order order;

    // JOIN với OrderItems
    @Relation(
            parentColumn = "orderId",
            entityColumn = "orderId"
    )
    public List<OrderItem> orderItems;

    // JOIN với OrderItems → Products
    @Relation(
            entity = OrderItem.class,
            parentColumn = "orderId",
            entityColumn = "orderId"
    )
    public List<OrderItemWithProduct> orderItemsWithProducts;

    // JOIN với User
    @Relation(
            parentColumn = "userId",
            entityColumn = "id"
    )
    public User user;

    // Helper methods để lấy thông tin Order
    public String getCustomerName() {
        return order.getCustomerName();
    }

    public String getCustomerPhone() {
        return order.getCustomerPhone();
    }

    public String getCustomerAddress() {
        return order.getCustomerAddress();
    }

    public String getCustomerEmail() {
        return order.getCustomerEmail();
    }

    public String getOrderStatus() {
        return order.getOrderStatus();
    }

    public String getPaymentMethod() {
        return order.getPaymentMethod();
    }

    public String getOrderDate() {
        return order.getOrderDate();
    }

    public String getNotes() {
        return order.getNotes();
    }

    public double getTotalAmount() {
        return order.getTotalAmount();
    }

    public int getOrderId() {
        return order.getOrderId();
    }

    public int getUserId() {
        return order.getUserId();
    }

    // Helper methods để lấy thông tin User
    public String getUserName() {
        return user != null ? user.getFname() : "";
    }

    public String getUserEmail() {
        return user != null ? user.getEmail() : "";
    }

    public boolean isUserAdmin() {
        return user != null && user.isRole();
    }

    public boolean isUserExists() {
        return user != null;
    }

    public boolean isCustomerInfoUpdated() {
        if (user == null) return false;
        boolean nameChanged = !user.getFname().equals(getCustomerName());
        boolean emailChanged = !user.getEmail().equals(getCustomerEmail());
        return nameChanged || emailChanged;
    }

    public String getActualCustomerName() {
        return user != null ? user.getFname() : getCustomerName();
    }

    public String getActualCustomerEmail() {
        return user != null ? user.getEmail() : getCustomerEmail();
    }

    public String getDisplayCustomerName() {
        String userName = getUserName();
        return userName != null && !userName.isEmpty() ? userName : getCustomerName();
    }

    public String getDisplayCustomerEmail() {
        String userEmail = getUserEmail();
        return userEmail != null && !userEmail.isEmpty() ? userEmail : getCustomerEmail();
    }

    public String getUserRole() {
        return isUserAdmin() ? "Admin" : "Customer";
    }

    public int getActualUserId() {
        return user != null ? user.getId() : getUserId();
    }

    public boolean isOwnOrder(int currentUserId) {
        return getUserId() == currentUserId;
    }

    public String getFullCustomerInfo() {
        return getDisplayCustomerName() + " (" + getDisplayCustomerEmail() + ")";
    }

    // Helper methods để lấy thông tin OrderItems
    public int getTotalItems() {
        if (orderItems != null) {
            int total = 0;
            for (OrderItem item : orderItems) {
                total += item.getQuantity();
            }
            return total;
        }
        return 0;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public List<OrderItemWithProduct> getOrderItemsWithProducts() {
        return orderItemsWithProducts;
    }

    public int getTotalItemsWithProducts() {
        if (orderItemsWithProducts != null) {
            int total = 0;
            for (OrderItemWithProduct item : orderItemsWithProducts) {
                total += item.getQuantity();
            }
            return total;
        }
        return 0;
    }

    // Bỏ getOrderBrands() và getOrderCategories() vì Product không có brand và categoryName
    // Thay bằng getOrderProductNames() để lấy danh sách tên sản phẩm
    public String getOrderProductNames() {
        if (orderItemsWithProducts != null) {
            Set<String> productNames = new HashSet<>();
            for (OrderItemWithProduct item : orderItemsWithProducts) {
                String productName = item.getProductName();
                if (productName != null && !productName.isEmpty()) {
                    productNames.add(productName);
                }
            }
            return String.join(", ", productNames);
        }
        return "";
    }

    public String getOrderColors() {
        if (orderItemsWithProducts != null) {
            Set<String> colors = new HashSet<>();
            for (OrderItemWithProduct item : orderItemsWithProducts) {
                String color = item.getSelectedColor();
                if (color != null && !color.isEmpty()) {
                    colors.add(color);
                }
            }
            return String.join(", ", colors);
        }
        return "";
    }

    public String getOrderSizes() {
        if (orderItemsWithProducts != null) {
            Set<String> sizes = new HashSet<>();
            for (OrderItemWithProduct item : orderItemsWithProducts) {
                String size = item.getSelectedSize();
                if (size != null && !size.isEmpty()) {
                    sizes.add(size);
                }
            }
            return String.join(", ", sizes);
        }
        return "";
    }

    public boolean hasPriceChanges() {
        if (orderItemsWithProducts != null) {
            for (OrderItemWithProduct item : orderItemsWithProducts) {
                if (item.isPriceChanged()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasUnavailableProducts() {
        if (orderItemsWithProducts != null) {
            for (OrderItemWithProduct item : orderItemsWithProducts) {
                if (!item.isProductAvailable()) {
                    return true;
                }
            }
        }
        return false;
    }

    public double recalculatedTotalAmount() {
        if (orderItemsWithProducts != null) {
            double total = 0;
            for (OrderItemWithProduct item : orderItemsWithProducts) {
                total += item.getCurrentPrice() * item.getQuantity();
            }
            return total;
        }
        return order.getTotalAmount();
    }

    public String getOrderSummary() {
        return "Đơn hàng #" + getOrderId() +
                " - " + getCustomerName() +
                " - " + getTotalItems() + " sản phẩm" +
                " - " + String.format("%.0f VNĐ", getTotalAmount());
    }
}