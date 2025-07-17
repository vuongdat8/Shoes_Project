package com.example.shoes_project.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.shoes_project.R;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.shoes_project.model.Product;
import com.example.shoes_project.model.ProductWithDetails;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    public interface OnProductClickListener {
        void onProductClick(ProductWithDetails product);
        void onProductLongClick(ProductWithDetails product);
    }

    private final Context context;
    private List<ProductWithDetails> productList = new ArrayList<>();
    private OnProductClickListener listener;

    public ProductAdapter(Context context) {
        this.context = context;
    }

    public void setOnProductClickListener(OnProductClickListener listener) {
        this.listener = listener;
    }

    public void updateProducts(List<ProductWithDetails> newProducts) {
        this.productList = newProducts != null ? newProducts : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        if (position < productList.size()) {
            holder.bind(productList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvProductName, tvBrand, tvPrice, tvCategory, tvQuantity;
        private final ImageView ivProduct;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvBrand = itemView.findViewById(R.id.tv_brand);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvCategory = itemView.findViewById(R.id.tv_category);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            ivProduct = itemView.findViewById(R.id.iv_product);

            // Gắn sự kiện click tại đây
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    Log.d("ProductAdapter", "Item clicked at position: " + position);
                    listener.onProductClick(productList.get(position));
                }
            });

            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onProductLongClick(productList.get(position));
                    return true;
                }
                return false;
            });
        }

        public void bind(ProductWithDetails item) {
            if (item.product == null) return;

            tvProductName.setText(item.product.getProductName());
            tvBrand.setText(item.brand != null ? item.brand.getName() : "Unknown Brand");
            tvCategory.setText(item.category != null ? item.category.getName() : "Unknown Category");
            tvPrice.setText(String.format("$%.2f", item.product.getPrice()));
            tvQuantity.setText("Qty: " + item.product.getQuantity());

            // SỬA PHẦN LOAD IMAGE - QUAN TRỌNG
            loadProductImage(item.product.getImageUrl(), ivProduct);

            // Debug log
            Log.d("ProductAdapter", "Loading image: " + item.product.getImageUrl());
        }

        // THÊM METHOD LOAD IMAGE GIỐNG NHƯ TRONG CustomerProductAdapter
        private void loadProductImage(String imagePath, ImageView imageView) {
            if (imagePath != null && !imagePath.isEmpty()) {
                if (imagePath.startsWith("http") || imagePath.startsWith("https")) {
                    // Load image from URL
                    Glide.with(context)
                            .load(imagePath)
                            .placeholder(R.drawable.ic_image_placeholder)
                            .error(R.drawable.ic_image_placeholder)
                            .centerCrop()
                            .into(imageView);
                } else if (imagePath.startsWith("content://")) {
                    // Load image from Content URI
                    Glide.with(context)
                            .load(imagePath)
                            .placeholder(R.drawable.ic_image_placeholder)
                            .error(R.drawable.ic_image_placeholder)
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
                                .placeholder(R.drawable.ic_image_placeholder)
                                .error(R.drawable.ic_image_placeholder)
                                .centerCrop()
                                .into(imageView);
                    } else {
                        // File doesn't exist, show placeholder
                        imageView.setImageResource(R.drawable.ic_image_placeholder);
                        // Log for debugging
                        Log.e("ProductAdapter", "Image file not found: " + imageFile.getAbsolutePath());
                    }
                }
            } else {
                // No image path, show placeholder
                imageView.setImageResource(R.drawable.ic_image_placeholder);
            }
        }
    }
}