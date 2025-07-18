package com.example.shoes_project.ui;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.shoes_project.data.CartRepository;
import com.example.shoes_project.model.CartA;
import com.example.shoes_project.ui.admin.ProductRepository;

import java.util.List;

public class CartViewModel extends AndroidViewModel {
    private CartRepository cartRepository;
    private ProductRepository productRepository;
    private MutableLiveData<List<CartA>> CartAs;
    private MutableLiveData<Double> totalPrice;
    private MutableLiveData<Integer> selectedItemCount;
    private MutableLiveData<Boolean> isAllSelected;
    private int currentUserId;

    public  CartViewModel(@NonNull Application application) {
        super(application);
        cartRepository = new CartRepository(application);
        productRepository = new ProductRepository(application);
        CartAs = new MutableLiveData<>();
        totalPrice = new MutableLiveData<>();
        selectedItemCount = new MutableLiveData<>();
        isAllSelected = new MutableLiveData<>();
    }

    public void setUserId(int userId) {
        this.currentUserId = userId;
        loadCartAs();

    }

    public void loadCartAs() {
        new Thread(() -> {
            List<CartA> items = cartRepository.getCartAs(currentUserId);
            Log.d("CartDebug", "Cart size = " + items.size());
            double total = cartRepository.getTotalSelectedPrice(currentUserId);
            int selectedCount = cartRepository.getSelectedItemCount(currentUserId);
            boolean allSelected = items.size() > 0 && selectedCount == items.size();

            CartAs.postValue(items);
            totalPrice.postValue(total);
            selectedItemCount.postValue(selectedCount);
            isAllSelected.postValue(allSelected);
        }).start();
    }

    public void updateQuantity(CartA CartA, int quantity) {
        new Thread(() -> {
            cartRepository.updateCartAQuantity(CartA, quantity);
            loadCartAs();
        }).start();
    }

    public void updateColor(CartA CartA, String color) {
        new Thread(() -> {
            cartRepository.updateCartAColor(CartA, color);
            loadCartAs();
        }).start();
    }

    public void updateSize(CartA CartA, double size) {
        new Thread(() -> {
            cartRepository.updateCartASize(CartA, size);
            loadCartAs();
        }).start();
    }

    public void updateSelection(CartA CartA, boolean selected) {
        new Thread(() -> {
            cartRepository.updateCartASelection(CartA, selected);
            loadCartAs();
        }).start();
    }

    public void selectAllItems(boolean selected) {
        new Thread(() -> {
            cartRepository.selectAllCartAs(currentUserId, selected);
            loadCartAs();
        }).start();
    }

    public void deleteSelectedItems() {
        new Thread(() -> {
            cartRepository.deleteSelectedCartAs(currentUserId);
            loadCartAs();
        }).start();
    }

    public void deleteCartA(CartA CartA) {
        new Thread(() -> {
            cartRepository.deleteCartA(CartA);
            loadCartAs();
        }).start();
    }

    public void addToCart(int productId, String color, double size){

        new Thread(() -> {
            cartRepository.addToCart(currentUserId, productId, color, size);
            loadCartAs();
        }).start();
    }

    public List<String> getAvailableColors(int productId) {
        return productRepository.getAvailableColors(productId);
    }

    public List<Double> getAvailableSizes(int productId) {
        return productRepository.getAvailableSizes(productId);
    }

    // Getters cho LiveData
    public LiveData<List<CartA>> getCartAs() {
        return CartAs;
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