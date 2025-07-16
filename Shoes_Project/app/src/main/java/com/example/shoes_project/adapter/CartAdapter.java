package com.example.shoes_project.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shoes_project.R;
import com.example.shoes_project.model.CartItem;
import com.example.shoes_project.ui.CartViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<CartItem> cartItems = new ArrayList<>();
    private Context context;
    private CartViewModel cartViewModel;

    public CartAdapter(Context context, CartViewModel cartViewModel) {
        this.context = context;
        this.cartViewModel = cartViewModel;
    }

    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
        Log.d("CartAdapter", "Setting cart items: " + cartItems.size());
        notifyDataSetChanged();
    }
    public List<CartItem> getSelectedItems() {
        List<CartItem> selectedItems = new ArrayList<>();
        if (cartItems != null) {
            for (CartItem item : cartItems) {
                if (item.isSelected()) {
                    selectedItems.add(item);
                }
            }
        }
        return selectedItems;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        holder.bind(cartItem);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    class CartViewHolder extends RecyclerView.ViewHolder {
        private CheckBox cbSelect;
        private ImageView ivProduct;
        private TextView tvProductName, tvPrice, tvQuantity;
        private Button btnDecrease, btnIncrease;
        private Spinner spinnerColor, spinnerSize;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            cbSelect = itemView.findViewById(R.id.cbSelect);
            ivProduct = itemView.findViewById(R.id.ivProduct);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            spinnerColor = itemView.findViewById(R.id.spinnerColor);
            spinnerSize = itemView.findViewById(R.id.spinnerSize);
        }

        public void bind(CartItem cartItem) {
            Log.d("CartViewHolder", "Binding item: " + cartItem.getProductName() + ", Image URL: " + cartItem.getImageUrl());
            // Tải ảnh sản phẩm
            Glide.with(context)
                    .load(cartItem.getImageUrl())
                    .placeholder(R.drawable.image_placeholder_background)
                    .error(R.drawable.image_placeholder_background)
                    .into(ivProduct);

            // Thiết lập thông tin sản phẩm
            tvProductName.setText(cartItem.getProductName());
            tvPrice.setText(String.format("%.0f VND", cartItem.getPrice()));
            tvQuantity.setText(String.valueOf(cartItem.getQuantity()));

            // Thiết lập checkbox
            cbSelect.setOnCheckedChangeListener(null);
            cbSelect.setChecked(cartItem.isSelected());
            cbSelect.setOnCheckedChangeListener((buttonView, isChecked) -> {
                cartViewModel.updateSelection(cartItem, isChecked);
            });

            // Thiết lập nút tăng/giảm số lượng
            btnDecrease.setOnClickListener(v -> {
                if (cartItem.getQuantity() > 1) {
                    cartViewModel.updateQuantity(cartItem, cartItem.getQuantity() - 1);
                } else {
                    showDeleteItemDialog(cartItem);
                }
            });

            btnIncrease.setOnClickListener(v -> {
                cartViewModel.updateQuantity(cartItem, cartItem.getQuantity() + 1);
            });

            // Thiết lập spinner màu sắc
            setupColorSpinner(cartItem);

            // Thiết lập spinner kích thước
            setupSizeSpinner(cartItem);
        }

        private void setupColorSpinner(CartItem cartItem) {
            List<String> colors = cartViewModel.getAvailableColors(cartItem.getProductId());
            if (colors == null || colors.isEmpty()) {
                colors = Arrays.asList("Đen", "Trắng", "Đỏ", "Xanh");
            }

            ArrayAdapter<String> colorAdapter = new ArrayAdapter<>(context,
                    android.R.layout.simple_spinner_item, colors);
            colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerColor.setAdapter(colorAdapter);

            // Thiết lập màu đã chọn
            int colorPosition = colors.indexOf(cartItem.getSelectedColor());
            if (colorPosition >= 0) {
                spinnerColor.setSelection(colorPosition);
            }

            List<String> finalColors = colors;
            spinnerColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedColor = finalColors.get(position);
                    if (!selectedColor.equals(cartItem.getSelectedColor())) {
                        cartViewModel.updateColor(cartItem, selectedColor);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
        }

        private void setupSizeSpinner(CartItem cartItem) {
            List<String> sizes = cartViewModel.getAvailableSizes(cartItem.getProductId());
            if (sizes == null || sizes.isEmpty()) {
                sizes = Arrays.asList("S", "M", "L", "XL");
            }

            ArrayAdapter<String> sizeAdapter = new ArrayAdapter<>(context,
                    android.R.layout.simple_spinner_item, sizes);
            sizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerSize.setAdapter(sizeAdapter);

            // Thiết lập size đã chọn
            int sizePosition = sizes.indexOf(cartItem.getSelectedSize());
            if (sizePosition >= 0) {
                spinnerSize.setSelection(sizePosition);
            }

            List<String> finalSizes = sizes;
            spinnerSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedSize = finalSizes.get(position);
                    if (!selectedSize.equals(cartItem.getSelectedSize())) {
                        cartViewModel.updateSize(cartItem, selectedSize);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
        }

        private void showDeleteItemDialog(CartItem cartItem) {
            new AlertDialog.Builder(context)
                    .setTitle("Xóa sản phẩm")
                    .setMessage("Bạn có muốn xóa sản phẩm này khỏi giỏ hàng?")
                    .setPositiveButton("Xóa", (dialog, which) -> {
                        cartViewModel.deleteCartItem(cartItem);
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        }

    }
}
