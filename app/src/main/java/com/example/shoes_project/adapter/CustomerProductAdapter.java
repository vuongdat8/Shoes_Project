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
import com.example.shoes_project.R;
import com.example.shoes_project.model.Product;
import java.util.List;

public class CustomerProductAdapter extends RecyclerView.Adapter<CustomerProductAdapter.ProductViewHolder> {
    private Context context;
    private List<Product> productList;
    private OnProductClickListener onProductClickListener;

    public interface OnProductClickListener {
        void onProductClick(Product product);
        void onViewDetailsClick(Product product);
    }

    public CustomerProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    public void setOnProductClickListener(OnProductClickListener listener) {
        this.onProductClickListener = listener;
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

    public void updateProductList(List<Product> newProductList) {
        this.productList = newProductList;
        notifyDataSetChanged();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivProductImage;
        private TextView tvProductName, tvBrand, tvCategory, tvPrice, tvOriginalPrice,
                tvStockStatus, tvSize;
        private View viewStockIndicator;
        private Button btnViewDetails;

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
            tvStockStatus = itemView.findViewById(R.id.tv_stock_status);
            tvSize = itemView.findViewById(R.id.tv_size);
            viewStockIndicator = itemView.findViewById(R.id.view_stock_indicator);
            btnViewDetails = itemView.findViewById(R.id.btn_view_details);
        }

        private void setupClickListeners() {
            itemView.setOnClickListener(v -> {
                if (onProductClickListener != null) {
                    onProductClickListener.onProductClick(productList.get(getAdapterPosition()));
                }
            });

            btnViewDetails.setOnClickListener(v -> {
                if (onProductClickListener != null) {
                    onProductClickListener.onViewDetailsClick(productList.get(getAdapterPosition()));
                }
            });
        }

        public void bind(Product product) {
            tvProductName.setText(product.getProductName());
            tvBrand.setText(product.getBrand());
            tvCategory.setText(product.getCategoryName());
            tvPrice.setText("$" + String.format("%.2f", product.getPrice()));
            tvSize.setText("Size: " + product.getSize());

            // Show original price if selling price is different
            if (product.getSellingPrice() != product.getPrice()) {
                tvOriginalPrice.setText("$" + String.format("%.2f", (double) product.getSellingPrice()));
                tvOriginalPrice.setVisibility(View.VISIBLE);
                // Add strikethrough effect
                tvOriginalPrice.setPaintFlags(tvOriginalPrice.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                tvOriginalPrice.setVisibility(View.GONE);
            }

            // Set stock status
            if (product.getQuantity() > 0) {
                if (product.getQuantity() > 10) {
                    tvStockStatus.setText("In Stock (" + product.getQuantity() + ")");
                    tvStockStatus.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
                    viewStockIndicator.setBackgroundResource(R.drawable.circle_green);
                } else {
                    tvStockStatus.setText("Low Stock (" + product.getQuantity() + ")");
                    tvStockStatus.setTextColor(context.getResources().getColor(android.R.color.holo_orange_dark));
                    viewStockIndicator.setBackgroundResource(R.drawable.circle_orange);
                }
            } else {
                tvStockStatus.setText("Out of Stock");
                tvStockStatus.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
                viewStockIndicator.setBackgroundResource(R.drawable.circle_red);
            }

            // Load product image using Glide or Picasso
            // Glide.with(context).load(product.getImageUrl()).into(ivProductImage);
        }
    }
}