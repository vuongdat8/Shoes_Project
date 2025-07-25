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
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.shoes_project.R;
import com.example.shoes_project.model.CartA;
import com.example.shoes_project.ui.CartViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<CartA> cartAs = new ArrayList<>();
    private Context context;
    private CartViewModel cartViewModel;

    public CartAdapter(Context context, CartViewModel cartViewModel) {
        this.context = context;
        this.cartViewModel = cartViewModel;
    }

    public void setCartAs(List<CartA> cartAs) {
        this.cartAs = cartAs;
        Log.d("CartAdapter", "Setting cart items: " + cartAs.size());
        notifyDataSetChanged();
    }

    public List<CartA> getSelectedItems() {
        List<CartA> selectedItems = new ArrayList<>();
        if (cartAs != null) {
            for (CartA item : cartAs) {
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
        CartA cartA = cartAs.get(position);
        holder.bind(cartA);
    }

    @Override
    public int getItemCount() {
        return cartAs.size();
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

        public void bind(CartA cartA) {
            Log.d("CartViewHolder", "Binding item: " + cartA.getProductName() + ", Image URL: " + cartA.getImageUrl());

            // Tải ảnh sản phẩm
            loadProductImage(cartA.getImageUrl(), ivProduct);

            // Thiết lập thông tin sản phẩm
            tvProductName.setText(cartA.getProductName());
            tvPrice.setText(String.format("%.0f VNĐ", cartA.getPrice()));
            tvQuantity.setText(String.valueOf(cartA.getQuantity()));

            // Thiết lập checkbox
            cbSelect.setOnCheckedChangeListener(null);
            cbSelect.setChecked(cartA.isSelected());
            cbSelect.setOnCheckedChangeListener((buttonView, isChecked) -> {
                cartViewModel.updateSelection(cartA, isChecked);
            });

            // Thiết lập nút tăng/giảm số lượng
            btnDecrease.setOnClickListener(v -> {
                if (cartA.getQuantity() > 1) {
                    cartViewModel.updateQuantity(cartA, cartA.getQuantity() - 1);
                } else {
                    showDeleteItemDialog(cartA);
                }
            });

            btnIncrease.setOnClickListener(v -> {
                cartViewModel.updateQuantity(cartA, cartA.getQuantity() + 1);
            });

            // Thiết lập spinner màu sắc
            setupColorSpinner(cartA);

            // Thiết lập spinner kích thước
            setupSizeSpinner(cartA);
        }
        private void loadProductImage(String imagePath, ImageView imageView) {
            if (imagePath != null && !imagePath.isEmpty()) {
                Log.d("CartViewHolder", "Loading image: " + imagePath);
                if (imagePath.startsWith("http") || imagePath.startsWith("https")) {
                    // Load image from URL
                    Glide.with(context)
                            .load(imagePath)
                            .placeholder(R.drawable.placeholder_product)
                            .error(R.drawable.placeholder_product)
                            .centerCrop()
                            .into(imageView);
                } else if (imagePath.startsWith("content://")) {
                    // Load image from Content URI
                    Glide.with(context)
                            .load(imagePath)
                            .placeholder(R.drawable.placeholder_product)
                            .error(R.drawable.placeholder_product)
                            .centerCrop()
                            .into(imageView);
                } else {
                    // Handle file path (both absolute and relative)
                    File imageFile;
                    if (imagePath.startsWith("/")) {
                        // Absolute path
                        imageFile = new File(imagePath);
                    } else {
                        // Relative path - construct full path
                        imageFile = new File(context.getFilesDir(), imagePath);
                    }

                    if (imageFile.exists()) {
                        Glide.with(context)
                                .load(imageFile)
                                .placeholder(R.drawable.placeholder_product)
                                .error(R.drawable.placeholder_product)
                                .centerCrop()
                                .into(imageView);
                    } else {
                        // File doesn't exist, show placeholder
                        imageView.setImageResource(R.drawable.placeholder_product);
                        Log.e("CartViewHolder", "Image file not found: " + imageFile.getAbsolutePath());
                    }
                }
            } else {
                // No image path, show placeholder
                imageView.setImageResource(R.drawable.placeholder_product);
                Log.e("CartViewHolder", "Image path is null or empty");
            }
        }
        private void setupColorSpinner(CartA cartA) {
            List<String> colors = cartViewModel.getAvailableColors(cartA.getProductId());
            if (colors == null || colors.isEmpty()) {
                colors = Arrays.asList("Đen", "Trắng", "Đỏ", "Xanh");
            }

            ArrayAdapter<String> colorAdapter = new ArrayAdapter<>(context,
                    android.R.layout.simple_spinner_item, colors);
            colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerColor.setAdapter(colorAdapter);

            // Thiết lập màu đã chọn
            int colorPosition = colors.indexOf(cartA.getSelectedColor());
            if (colorPosition >= 0) {
                spinnerColor.setSelection(colorPosition);
            }

            List<String> finalColors = colors;
            spinnerColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedColor = finalColors.get(position);
                    if (!selectedColor.equals(cartA.getSelectedColor())) {
                        cartViewModel.updateColor(cartA, selectedColor);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
        }

        private void setupSizeSpinner(CartA cartA) {
            List<Double> sizes = cartViewModel.getAvailableSizes(cartA.getProductId());
            if (sizes == null || sizes.isEmpty()) {

                sizes = Arrays.asList(39.5, 40.0, 41.0,41.5,42.0,42.5,43.0);  // fallback

            }

            ArrayAdapter<Double> sizeAdapter = new ArrayAdapter<>(context,
                    android.R.layout.simple_spinner_item, sizes);
            sizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerSize.setAdapter(sizeAdapter);

            int sizePosition = sizes.indexOf(cartA.getSelectedSize());
            if (sizePosition >= 0) {
                spinnerSize.setSelection(sizePosition);
            }

            List<Double> finalSizes = sizes;
            spinnerSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Double selectedSize = finalSizes.get(position);
                    if (!selectedSize.equals(cartA.getSelectedSize())) {
                        cartViewModel.updateSize(cartA, selectedSize);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });

        }

        private void showDeleteItemDialog(CartA cartA) {
            new AlertDialog.Builder(context)
                    .setTitle("Xóa sản phẩm")
                    .setMessage("Bạn có muốn xóa sản phẩm này khỏi giỏ hàng?")
                    .setPositiveButton("Xóa", (dialog, which) -> {
                        cartViewModel.deleteCartA(cartA);
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        }
    }
}