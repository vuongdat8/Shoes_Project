package com.example.shoes_project.ui;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.shoes_project.data.CartRepository;
import com.example.shoes_project.model.CartItem;
import com.example.shoes_project.data.ProductRepository;

import java.util.List;

public class CartViewModel extends AndroidViewModel {
    private CartRepository cartRepository;
    private ProductRepository productRepository;
    private MutableLiveData<List<CartItem>> cartItems;
    private MutableLiveData<Double> totalPrice;
    private MutableLiveData<Integer> selectedItemCount;
    private MutableLiveData<Boolean> isAllSelected;
    private int currentUserId;

    public CartViewModel(@NonNull Application application) {
        super(application);
        cartRepository = new CartRepository(application);
        productRepository = new ProductRepository(application);
        cartItems = new MutableLiveData<>();
        totalPrice = new MutableLiveData<>();
        selectedItemCount = new MutableLiveData<>();
        isAllSelected = new MutableLiveData<>();
    }

    public void setUserId(int userId) {
        this.currentUserId = userId;
        loadCartItems();

    }

    public void loadCartItems() {
        new Thread(() -> {
            List<CartItem> items = cartRepository.getCartItems(currentUserId);
            Log.d("CartDebug", "Cart size = " + items.size());
            double total = cartRepository.getTotalSelectedPrice(currentUserId);
            int selectedCount = cartRepository.getSelectedItemCount(currentUserId);
            boolean allSelected = items.size() > 0 && selectedCount == items.size();

            cartItems.postValue(items);
            totalPrice.postValue(total);
            selectedItemCount.postValue(selectedCount);
            isAllSelected.postValue(allSelected);
        }).start();
    }

    public void updateQuantity(CartItem cartItem, int quantity) {
        new Thread(() -> {
            cartRepository.updateCartItemQuantity(cartItem, quantity);
            loadCartItems();
        }).start();
    }

    public void updateColor(CartItem cartItem, String color) {
        new Thread(() -> {
            cartRepository.updateCartItemColor(cartItem, color);
            loadCartItems();
        }).start();
    }

    public void updateSize(CartItem cartItem, String size) {
        new Thread(() -> {
            cartRepository.updateCartItemSize(cartItem, size);
            loadCartItems();
        }).start();
    }

    public void updateSelection(CartItem cartItem, boolean selected) {
        new Thread(() -> {
            cartRepository.updateCartItemSelection(cartItem, selected);
            loadCartItems();
        }).start();
    }

    public void selectAllItems(boolean selected) {
        new Thread(() -> {
            cartRepository.selectAllCartItems(currentUserId, selected);
            loadCartItems();
        }).start();
    }

    public void deleteSelectedItems() {
        new Thread(() -> {
            cartRepository.deleteSelectedCartItems(currentUserId);
            loadCartItems();
        }).start();
    }

    public void deleteCartItem(CartItem cartItem) {
        new Thread(() -> {
            cartRepository.deleteCartItem(cartItem);
            loadCartItems();
        }).start();
    }

    public void addToCart(int productId, String color, String size) {
        new Thread(() -> {
            cartRepository.addToCart(currentUserId, productId, color, size);
            loadCartItems();
        }).start();
    }

    public List<String> getAvailableColors(int productId) {
        return productRepository.getAvailableColors(productId);
    }

    public List<String> getAvailableSizes(int productId) {
        return productRepository.getAvailableSizes(productId);
    }

    // Getters cho LiveData
    public LiveData<List<CartItem>> getCartItems() {
        return cartItems;
    }

    public LiveData<Double> getTotalPrice() {
        return totalPrice;
    }

    public LiveData<Integer> getSelectedItemCount() {
        return selectedItemCount;
    }

    public LiveData<Boolean> getIsAllSelected() {
        return isAllSelected;
    }
}