package com.example.shoes_project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.shoes_project.R;
import com.example.shoes_project.model.Product;
import com.bumptech.glide.Glide;
import java.io.File;
import java.util.List;

public class CustomerProductAdapter extends RecyclerView.Adapter<CustomerProductAdapter.ProductViewHolder> {
    private Context context;
    private List<Product> productList;
    private OnProductClickListener listener;

    public interface OnProductClickListener {
        void onProductClick(Product product);
        void onViewDetailsClick(Product product);
    }

    public CustomerProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    public void setOnProductClickListener(OnProductClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_customer_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivProductImage;
        private TextView tvProductName, tvBrand, tvCategory, tvPrice, tvOriginalPrice,
                tvDiscountBadge, tvStockStatus, tvQuantity;
        private View viewStockIndicator;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            initViews();
            setupClickListeners();
        }

        private void initViews() {
            ivProductImage = itemView.findViewById(R.id.iv_product_image);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvBrand = itemView.findViewById(R.id.tv_brand);
            tvCategory = itemView.findViewById(R.id.tv_category);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvOriginalPrice = itemView.findViewById(R.id.tv_original_price);
            tvDiscountBadge = itemView.findViewById(R.id.tv_discount_badge);
            tvStockStatus = itemView.findViewById(R.id.tv_stock_status);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            viewStockIndicator = itemView.findViewById(R.id.view_stock_indicator);
        }

        private void setupClickListeners() {
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onProductClick(productList.get(getAdapterPosition()));
                }
            });
        }

        public void bind(Product product) {
            // Load product image - THÊM CHỨC NĂNG LOAD IMAGE
            loadProductImage(product.getImageUrl());

            // Basic product information
            tvProductName.setText(product.getProductName());
            tvBrand.setText(product.getBrand());
            tvCategory.setText(product.getCategoryName());
            tvPrice.setText("$" + String.format("%.2f", product.getPrice()));

            // Handle original price and discount
            if (product.getSellingPrice() != product.getPrice()) {
                tvOriginalPrice.setText("$" + String.format("%.2f", (double) product.getSellingPrice()));
                tvOriginalPrice.setVisibility(View.VISIBLE);
                tvOriginalPrice.setPaintFlags(tvOriginalPrice.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);

                // Calculate and show discount percentage
                double discountPercent = ((product.getSellingPrice() - product.getPrice()) / product.getSellingPrice()) * 1;
                tvDiscountBadge.setText(String.format("%.0f%% OFF", discountPercent));
                tvDiscountBadge.setVisibility(View.VISIBLE);
            } else {
                tvOriginalPrice.setVisibility(View.GONE);
                tvDiscountBadge.setVisibility(View.GONE);
            }

            // Stock status and quantity
            updateStockStatus(product);
        }

        // THÊM METHOD LOAD IMAGE - TƯƠNG TỰ NHƯ CustomerProductDetailActivity
        private void loadProductImage(String imagePath) {
            if (imagePath != null && !imagePath.isEmpty()) {
                if (imagePath.startsWith("http") || imagePath.startsWith("https")) {
                    // Load ảnh từ URL
                    Glide.with(context)
                            .load(imagePath)
                            .placeholder(R.drawable.ic_image_placeholder)
                            .error(R.drawable.ic_image_placeholder)
                            .into(ivProductImage);
                } else if (imagePath.startsWith("content://")) {
                    // Load ảnh từ Content URI (Gallery)
                    Glide.with(context)
                            .load(imagePath)
                            .placeholder(R.drawable.ic_image_placeholder)
                            .error(R.drawable.ic_image_placeholder)
                            .into(ivProductImage);
                } else {
                    // Load ảnh từ file path (Internal Storage)
                    File imageFile = new File(imagePath);
                    if (imageFile.exists()) {
                        Glide.with(context)
                                .load(imageFile)
                                .placeholder(R.drawable.ic_image_placeholder)
                                .error(R.drawable.ic_image_placeholder)
                                .into(ivProductImage);
                    } else {
                        // Nếu file không tồn tại, hiển thị placeholder
                        ivProductImage.setImageResource(R.drawable.ic_image_placeholder);
                    }
                }
            } else {
                // Nếu không có ảnh, hiển thị placeholder
                ivProductImage.setImageResource(R.drawable.ic_image_placeholder);
            }
        }

        private void updateStockStatus(Product product) {
            int quantity = product.getQuantity();
            tvQuantity.setText(quantity + " items");

            if (quantity > 0) {
                if (quantity > 10) {
                    tvStockStatus.setText("In Stock");
                    tvStockStatus.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
                    if (viewStockIndicator != null) {
                        viewStockIndicator.setBackgroundResource(R.drawable.circle_green);
                    }
                } else {
                    tvStockStatus.setText("Low Stock");
                    tvStockStatus.setTextColor(context.getResources().getColor(android.R.color.holo_orange_dark));
                    if (viewStockIndicator != null) {
                        viewStockIndicator.setBackgroundResource(R.drawable.circle_orange);
                    }
                }
            } else {
                tvStockStatus.setText("Out of Stock");
                tvStockStatus.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
                if (viewStockIndicator != null) {
                    viewStockIndicator.setBackgroundResource(R.drawable.circle_red);
                }
            }
        }
    }
}