package com.example.shoes_project.data;

import android.app.Application;

import com.example.shoes_project.model.CartItem;
import com.example.shoes_project.model.Product;

import java.util.List;

public class CartRepository {
    private CartItemDao cartItemDao;
    private ProductDao productDao;

    public CartRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        cartItemDao = database.cartItemDao();
        productDao = database.productDao();
    }

    // Lấy tất cả sản phẩm trong giỏ hàng
    public List<CartItem> getCartItems(int userId) {
        return cartItemDao.getCartItemsByUserId(userId);
    }


    // Lấy sản phẩm đã chọn
    public List<CartItem> getSelectedCartItems(int userId) {
        return cartItemDao.getSelectedCartItems(userId);
    }

    // Thêm sản phẩm vào giỏ hàng
    public void addToCart(int userId, int productId, String color, String size) {
        Product product = productDao.getProductById(productId);
        if (product != null) {
            CartItem existingItem = cartItemDao.getCartItemByDetails(userId, productId, color, size);
            if (existingItem != null) {
                existingItem.setQuantity(existingItem.getQuantity() + 1);
                cartItemDao.updateCartItem(existingItem);
            } else {
                CartItem newItem = new CartItem(userId, productId, product.getProductName(),
                        product.getImageUrl(), product.getPrice(), 1, color, size);
                cartItemDao.insertCartItem(newItem);
            }
        }
    }

    // Cập nhật số lượng sản phẩm
    public void updateCartItemQuantity(CartItem cartItem, int quantity) {
        if (quantity <= 0) {
            cartItemDao.deleteCartItem(cartItem);
        } else {
            cartItem.setQuantity(quantity);
            cartItemDao.updateCartItem(cartItem);
        }
    }

    // Cập nhật màu sắc
    public void updateCartItemColor(CartItem cartItem, String color) {
        cartItem.setSelectedColor(color);
        cartItemDao.updateCartItem(cartItem);
    }

    // Cập nhật kích thước
    public void updateCartItemSize(CartItem cartItem, String size) {
        cartItem.setSelectedSize(size);
        cartItemDao.updateCartItem(cartItem);
    }

    // Chọn/bỏ chọn sản phẩm
    public void updateCartItemSelection(CartItem cartItem, boolean selected) {
        cartItem.setSelected(selected);
        cartItemDao.updateCartItem(cartItem);
    }

    // Chọn/bỏ chọn tất cả
    public void selectAllCartItems(int userId, boolean selected) {
        cartItemDao.updateAllCartItemsSelection(userId, selected);
    }

    // Xóa sản phẩm đã chọn
    public void deleteSelectedCartItems(int userId) {
        cartItemDao.deleteSelectedCartItems(userId);
    }

    // Xóa sản phẩm khỏi giỏ hàng
    public void deleteCartItem(CartItem cartItem) {
        cartItemDao.deleteCartItem(cartItem);
    }

    // Lấy tổng giá trị đã chọn
    public double getTotalSelectedPrice(int userId) {
        Double total = cartItemDao.getTotalSelectedPrice(userId);
        return total != null ? total : 0.0;
    }

    // Lấy số lượng sản phẩm đã chọn
    public int getSelectedItemCount(int userId) {
        return cartItemDao.getSelectedItemCount(userId);
    }

    // Lấy tổng số sản phẩm trong giỏ hàng
    public int getCartItemCount(int userId) {
        return cartItemDao.getCartItemCount(userId);
    }
}
