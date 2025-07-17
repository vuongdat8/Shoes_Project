package com.example.shoes_project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shoes_project.R;
import com.example.shoes_project.model.Product;
import com.example.shoes_project.model.ProductWithDetails;

import java.io.File;
import java.util.List;

public class CustomerProductAdapter extends RecyclerView.Adapter<CustomerProductAdapter.ProductViewHolder> {

    private Context context;
    private List<ProductWithDetails> productList;
    private OnProductClickListener listener;

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    public CustomerProductAdapter(Context context, List<ProductWithDetails> productList) {
        this.context = context;
        this.productList = productList;
    }

    public void setOnProductClickListener(OnProductClickListener listener) {
        this.listener = listener;
    }

    public void updateProductList(List<ProductWithDetails> newList) {
        this.productList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_customer_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        ProductWithDetails productWithDetails = productList.get(position);
        Product product = productWithDetails.product;

        // Set product information
        holder.tvProductName.setText(product.getProductName());
        holder.tvPrice.setText("$" + String.format("%.2f", product.getPrice()));

        // Set brand name
        if (productWithDetails.brand != null) {
            holder.tvBrand.setText(productWithDetails.brand.getName());
        } else {
            holder.tvBrand.setText("Unknown Brand");
        }

        // Set category name
        if (productWithDetails.category != null) {
            holder.tvCategory.setText(productWithDetails.category.getName());
        } else {
            holder.tvCategory.setText("Unknown Category");
        }

        // Load product image - THIS IS THE IMPORTANT PART
        loadProductImage(product.getImageUrl(), holder.ivProductImage);

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProductClick(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    // THÊM METHOD LOAD IMAGE - QUAN TRỌNG
    private void loadProductImage(String imagePath, ImageView imageView) {
        if (imagePath != null && !imagePath.isEmpty()) {
            if (imagePath.startsWith("http") || imagePath.startsWith("https")) {
                // Load image from URL
                Glide.with(context)
                        .load(imagePath)
                        .placeholder(R.drawable.ic_image_placeholder)
                        .error(R.drawable.ic_image_placeholder)
                        .into(imageView);
            } else if (imagePath.startsWith("content://")) {
                // Load image from Content URI
                Glide.with(context)
                        .load(imagePath)
                        .placeholder(R.drawable.ic_image_placeholder)
                        .error(R.drawable.ic_image_placeholder)
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
                            .into(imageView);
                } else {
                    // File doesn't exist, show placeholder
                    imageView.setImageResource(R.drawable.ic_image_placeholder);
                    // Log for debugging
                    android.util.Log.e("CustomerAdapter", "Image file not found: " + imageFile.getAbsolutePath());
                }
            }
        } else {
            // No image path, show placeholder
            imageView.setImageResource(R.drawable.ic_image_placeholder);
        }
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvProductName, tvPrice, tvBrand, tvCategory;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.iv_product_image);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvBrand = itemView.findViewById(R.id.tv_brand);
            tvCategory = itemView.findViewById(R.id.tv_category);
        }
    }
}
