package com.example.shoes_project.data;

import android.app.Application;

import com.example.shoes_project.model.CartA;
import com.example.shoes_project.model.Product;

import java.util.List;

public class CartRepository {
    private CartADao CartADao;
    private ProductDao productDao;

    public CartRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        CartADao = database.cartADao();
        productDao = database.productDao();
    }

    // Lấy tất cả sản phẩm trong giỏ hàng
    public List<CartA> getCartAs(int userId) {
        return CartADao.getCartAsByUserId(userId);
    }


    // Lấy sản phẩm đã chọn
    public List<CartA> getSelectedCartAs(int userId) {
        return CartADao.getSelectedCartAs(userId);
    }

    // Thêm sản phẩm vào giỏ hàng
    public void addToCart(int userId, int productId, String color, double size){

        Product product = productDao.getProductById(productId);
        if (product != null) {
        CartA existingItem = CartADao.getCartAByDetails(userId, productId, color, size);
            if (existingItem != null) {
                existingItem.setQuantity(existingItem.getQuantity() + 1);
                CartADao.updateCartA(existingItem);
            } else {
                CartA newItem = new CartA(userId, productId, product.getProductName(),
                        product.getImageUrl(), product.getSellingPrice(), 1, color, size);

                CartADao.insertCartA(newItem);
            }
        }
    }

    // Cập nhật số lượng sản phẩm
    public void updateCartAQuantity(CartA CartA, int quantity) {
        if (quantity <= 0) {
            CartADao.deleteCartA(CartA);
        } else {
            CartA.setQuantity(quantity);
            CartADao.updateCartA(CartA);
        }
    }

    // Cập nhật màu sắc
    public void updateCartAColor(CartA CartA, String color) {
        CartA.setSelectedColor(color);
        CartADao.updateCartA(CartA);
    }

    // Cập nhật kích thước
    public void updateCartASize(CartA CartA, double size) {
        CartA.setSelectedSize(size);
        CartADao.updateCartA(CartA);
    }

    // Chọn/bỏ chọn sản phẩm
    public void updateCartASelection(CartA CartA, boolean selected) {
        CartA.setSelected(selected);
        CartADao.updateCartA(CartA);
    }

    // Chọn/bỏ chọn tất cả
    public void selectAllCartAs(int userId, boolean selected) {
        CartADao.updateAllCartAsSelection(userId, selected);
    }

    // Xóa sản phẩm đã chọn
    public void deleteSelectedCartAs(int userId) {
        CartADao.deleteSelectedCartAs(userId);
    }

    // Xóa sản phẩm khỏi giỏ hàng
    public void deleteCartA(CartA CartA) {
        CartADao.deleteCartA(CartA);
    }

    // Lấy tổng giá trị đã chọn
    public double getTotalSelectedPrice(int userId) {
        Double total = CartADao.getTotalSelectedPrice(userId);
        return total != null ? total : 0.0;
    }

    // Lấy số lượng sản phẩm đã chọn
    public int getSelectedItemCount(int userId) {
        return CartADao.getSelectedItemCount(userId);
    }

    // Lấy tổng số sản phẩm trong giỏ hàng
    public int getCartACount(int userId) {
        return CartADao.getCartACount(userId);
    }
}
