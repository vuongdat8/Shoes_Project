package com.prm.shoes_project.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prm.shoes_project.R;
import com.prm.shoes_project.data.db.entity.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private Context context;
    private List<Product> products = new ArrayList<>();
    private OnProductClickListener listener;

    public ProductAdapter(Context context, OnProductClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_item_product_card, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);

        holder.nameTextView.setText(product.getName());
        holder.importPriceTextView.setText("Import Price: $" + product.getImportPrice());
        holder.sellingPriceTextView.setText("Selling Price: $" + product.getSellingPrice());
        holder.stockTextView.setText("Stock: " + product.getStock());
        holder.soldTextView.setText("Sold: " + product.getSold());

        holder.moreButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProductClick(product.getProductId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView nameTextView, importPriceTextView, sellingPriceTextView, stockTextView, soldTextView;
        ImageButton moreButton;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.clothes_image);
            nameTextView = itemView.findViewById(R.id.clothes_name);
            importPriceTextView = itemView.findViewById(R.id.clothes_inprice);
            sellingPriceTextView = itemView.findViewById(R.id.clothes_price);
            stockTextView = itemView.findViewById(R.id.clothes_stock);
            soldTextView = itemView.findViewById(R.id.clothes_sold);
            moreButton = itemView.findViewById(R.id.clothes_more_button);
        }
    }

    // Interface để xử lý sự kiện nhấn nút "More"
    public interface OnProductClickListener {
        void onProductClick(UUID productId);
    }
}
