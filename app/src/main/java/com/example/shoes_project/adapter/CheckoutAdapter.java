package com.example.shoes_project.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.shoes_project.R;
import com.example.shoes_project.model.Product;

import java.io.File;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CheckoutAdapter extends RecyclerView.Adapter<CheckoutAdapter.CheckoutViewHolder> {
    private Context context;
    private List<Product> products;
    private List<Integer> quantities;
    private List<String> selectedColors;
    private List<Double> selectedSizes;

    public CheckoutAdapter(Context context, List<Product> products, List<Integer> quantities,
                           List<String> selectedColors, List<Double> selectedSizes) {
        this.context = context;
        this.products = products;
        this.quantities = quantities;
        this.selectedColors = selectedColors;
        this.selectedSizes = selectedSizes;
    }

    @NonNull
    @Override
    public CheckoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_checkout, parent, false);
        return new CheckoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckoutViewHolder holder, int position) {
        Product product = products.get(position);
        int quantity = quantities.get(position);
        String color = selectedColors.get(position);
        Double size = selectedSizes.get(position);

        // Bind text data
        holder.tvProductName.setText(product.getProductName());
        holder.tvPrice.setText(formatCurrency(product.getPrice()));
        holder.tvQuantity.setText("x" + quantity);
        double subtotal = product.getPrice() * quantity;
        holder.tvSubtotal.setText(formatCurrency(subtotal));

        // Bind color and size
        holder.tvColor.setText("Màu: " + (color != null && !color.isEmpty() ? color : "N/A"));
        holder.tvSize.setText("Kích thước: " + (size != null ? size : "N/A"));
        holder.tvColor.setVisibility(View.VISIBLE);
        holder.tvSize.setVisibility(View.VISIBLE);

        // Hide brand since Product doesn't have a brand field
        holder.tvBrand.setVisibility(View.GONE);

        // Load product image
        loadProductImage(product.getImageUrl(), holder.ivProductImage);
    }

    private void loadProductImage(String imagePath, ImageView imageView) {
        if (imagePath != null && !imagePath.isEmpty()) {
            Log.d("CheckoutAdapter", "Loading image: " + imagePath);
            if (imagePath.startsWith("http") || imagePath.startsWith("https")) {
                // Load image from URL
                Glide.with(context)
                        .load(imagePath)
                        .thumbnail(0.25f)
                        .override(100, 100)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.giay)
                        .error(R.drawable.giay)
                        .centerCrop()
                        .into(imageView);
            } else if (imagePath.startsWith("content://")) {
                // Load image from Content URI
                Glide.with(context)
                        .load(imagePath)
                        .thumbnail(0.25f)
                        .override(100, 100)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.giay)
                        .error(R.drawable.giay)
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
                            .thumbnail(0.25f)
                            .override(100, 100)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(R.drawable.giay)
                            .error(R.drawable.giay)
                            .centerCrop()
                            .into(imageView);
                } else {
                    imageView.setImageResource(R.drawable.giay);
                    Log.e("CheckoutAdapter", "Image file not found: " + imageFile.getAbsolutePath());
                }
            }
        } else {
            imageView.setImageResource(R.drawable.giay);
            Log.e("CheckoutAdapter", "Image path is null or empty for product: " );
        }
    }
    @Override
    public int getItemCount() {
        return products != null ? products.size() : 0;
    }

    public double getTotalAmount() {
        double total = 0;
        if (products != null && quantities != null) {
            for (int i = 0; i < products.size(); i++) {
                total += products.get(i).getPrice() * quantities.get(i);
            }
        }
        return total;
    }

    private String formatCurrency(double amount) {
        return NumberFormat.getNumberInstance(Locale.getDefault()).format(amount) + "₫";
    }

    public static class CheckoutViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvProductName, tvBrand, tvPrice, tvQuantity, tvSubtotal, tvSize, tvColor;

        public CheckoutViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.iv_product_image);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvBrand = itemView.findViewById(R.id.tv_brand);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            tvSubtotal = itemView.findViewById(R.id.tv_subtotal);
            tvSize = itemView.findViewById(R.id.tv_size);
            tvColor = itemView.findViewById(R.id.tv_color);
        }
    }
}