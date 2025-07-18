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
import com.example.shoes_project.model.CartA;
import com.example.shoes_project.ui.CartViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<CartA> CartAs = new ArrayList<>();
    private Context context;
    private CartViewModel cartViewModel;

    public CartAdapter(Context context, CartViewModel cartViewModel) {
        this.context = context;
        this.cartViewModel = cartViewModel;
    }

    public void setCartAs(List<CartA> CartAs) {
        this.CartAs = CartAs;
        Log.d("CartAdapter", "Setting cart items: " + CartAs.size());
        notifyDataSetChanged();
    }
    public List<CartA> getSelectedItems() {
        List<CartA> selectedItems = new ArrayList<>();
        if (CartAs != null) {
            for (CartA item : CartAs) {
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
        CartA CartA = CartAs.get(position);
        holder.bind(CartA);
    }

    @Override
    public int getItemCount() {
        return CartAs.size();
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

        public void bind(CartA CartA) {
            Log.d("CartViewHolder", "Binding item: " + CartA.getProductName() + ", Image URL: " + CartA.getImageUrl());
            // Tải ảnh sản phẩm
            Glide.with(context)
                    .load(CartA.getImageUrl())
                    .placeholder(R.drawable.placeholder_product)
                    .error(R.drawable.placeholder_product)
                    .into(ivProduct);

            // Thiết lập thông tin sản phẩm
            tvProductName.setText(CartA.getProductName());
            tvPrice.setText(String.format("%.0f VND", CartA.getPrice()));
            tvQuantity.setText(String.valueOf(CartA.getQuantity()));

            // Thiết lập checkbox
            cbSelect.setOnCheckedChangeListener(null);
            cbSelect.setChecked(CartA.isSelected());
            cbSelect.setOnCheckedChangeListener((buttonView, isChecked) -> {
                cartViewModel.updateSelection(CartA, isChecked);
            });

            // Thiết lập nút tăng/giảm số lượng
            btnDecrease.setOnClickListener(v -> {
                if (CartA.getQuantity() > 1) {
                    cartViewModel.updateQuantity(CartA, CartA.getQuantity() - 1);
                } else {
                    showDeleteItemDialog(CartA);
                }
            });

            btnIncrease.setOnClickListener(v -> {
                cartViewModel.updateQuantity(CartA, CartA.getQuantity() + 1);
            });

            // Thiết lập spinner màu sắc
            setupColorSpinner(CartA);

            // Thiết lập spinner kích thước
            setupSizeSpinner(CartA);
        }

        private void setupColorSpinner(CartA CartA) {
            List<String> colors = cartViewModel.getAvailableColors(CartA.getProductId());
            if (colors == null || colors.isEmpty()) {
                colors = Arrays.asList("Đen", "Trắng", "Đỏ", "Xanh");
            }

            ArrayAdapter<String> colorAdapter = new ArrayAdapter<>(context,
                    android.R.layout.simple_spinner_item, colors);
            colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerColor.setAdapter(colorAdapter);

            // Thiết lập màu đã chọn
            int colorPosition = colors.indexOf(CartA.getSelectedColor());
            if (colorPosition >= 0) {
                spinnerColor.setSelection(colorPosition);
            }

            List<String> finalColors = colors;
            spinnerColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedColor = finalColors.get(position);
                    if (!selectedColor.equals(CartA.getSelectedColor())) {
                        cartViewModel.updateColor(CartA, selectedColor);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
        }

        private void setupSizeSpinner(CartA CartA) {
            List<Double> sizes = cartViewModel.getAvailableSizes(CartA.getProductId());
            if (sizes == null || sizes.isEmpty()) {
                sizes = Arrays.asList(39.5, 40.0, 41.0,41.5,42.0,42.5,43.0);  // fallback
            }

            ArrayAdapter<Double> sizeAdapter = new ArrayAdapter<>(context,
                    android.R.layout.simple_spinner_item, sizes);
            sizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerSize.setAdapter(sizeAdapter);

            int sizePosition = sizes.indexOf(CartA.getSelectedSize());
            if (sizePosition >= 0) {
                spinnerSize.setSelection(sizePosition);
            }

            List<Double> finalSizes = sizes;
            spinnerSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Double selectedSize = finalSizes.get(position);
                    if (!selectedSize.equals(CartA.getSelectedSize())) {
                        cartViewModel.updateSize(CartA, selectedSize);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
        }


        private void showDeleteItemDialog(CartA CartA) {
            new AlertDialog.Builder(context)
                    .setTitle("Xóa sản phẩm")
                    .setMessage("Bạn có muốn xóa sản phẩm này khỏi giỏ hàng?")
                    .setPositiveButton("Xóa", (dialog, which) -> {
                        cartViewModel.deleteCartA(CartA);
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        }

    }
}
