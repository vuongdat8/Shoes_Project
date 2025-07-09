package com.example.shoes_project.adapter;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shoes_project.R;
import com.example.shoes_project.model.Brand;
import com.example.shoes_project.ui.admin.BrandDetailActivity;

import java.util.List;

public class BrandAdapter extends RecyclerView.Adapter<BrandAdapter.BrandViewHolder> {
    public static final String EXTRA_Brand = "extra_brand";

    private List<Brand> categories;
    private BrandActionListener actionListener;
    public LinearLayout itemLayout; // Khai báo này là đúng
    private List<Brand> brands ;

    public BrandAdapter(List<Brand> categories, BrandActionListener actionListener) {
        this.categories = categories;
        this.actionListener = actionListener;
    }

    @Override
    public BrandViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate your Brand_item.xml layout
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_brand_detail, parent, false);
        return new BrandViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(BrandViewHolder holder, int position) {
        Brand Brand = categories.get(position);
        holder.nameTextView.setText(Brand.getName());
        holder.descriptionTextView.setText(Brand.getDescription());

        // Set isActive status text
        holder.statusTextView.setText("Trạng thái: " + (Brand.isActive() ? "Hoạt động" : "Ngừng hoạt động"));

        // Set createdAt and updatedAt text
        holder.createdAtTextView.setText("Ngày tạo: " + (Brand.getCreatedAt() != null ? Brand.getCreatedAt() : "N/A"));
        holder.updatedAtTextView.setText("Ngày cập nhật: " + (Brand.getUpdatedAt() != null ? Brand.getUpdatedAt() : "N/A"));

        // Load image using Glide
        if (Brand.getImageUrl() != null && !Brand.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(Uri.parse(Brand.getImageUrl())) // Parse the URI string back to Uri object
                    .placeholder(R.drawable.a1) // Placeholder while loading
                    .error(R.drawable.a2) // Image to show if loading fails
                    .into(holder.imageView);
        } else {
            // If no image URL, show a default placeholder (e.g., a4)
            holder.imageView.setImageResource(R.drawable.a4);
        }

        // REQUIRED: Set OnClickListener for the entire item (LinearLayout)
        // This will allow clicking anywhere on the item to view details
        holder.itemLayout.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), BrandDetailActivity.class);
            // Pass the entire Brand object to the detail activity
            intent.putExtra(BrandDetailActivity.EXTRA_BRAND, Brand);
            v.getContext().startActivity(intent);
        });

        // Xử lý sự kiện nhấn nút Edit
        holder.editButton.setOnClickListener(v -> actionListener.onEditBrand(Brand));

        // Xử lý sự kiện nhấn nút Delete
        holder.deleteButton.setOnClickListener(v -> actionListener.onDeleteBrand(Brand));
    }

    @Override
    public int getItemCount() {
        return categories != null ? categories.size() : 0;
    }

    public void setBrands(List<Brand> brands) {
        this.brands = brands;
        notifyDataSetChanged();
    }

    public class BrandViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView, descriptionTextView, statusTextView, createdAtTextView, updatedAtTextView;
        public ImageView imageView;
        public Button editButton, deleteButton;
        public LinearLayout itemLayout; // REQUIRED: Add reference to the root LinearLayout of the item

        public BrandViewHolder(View view) {
            super(view);
            // REQUIRED: Initialize itemLayout by finding its ID in the item's view
            itemLayout = view.findViewById(R.id.linearLayout_item_roott); // Make sure you add this ID in Brand_item.xml

            nameTextView = view.findViewById(R.id.brand_name);
            descriptionTextView = view.findViewById(R.id.brand_description);
            imageView = view.findViewById(R.id.brand_image);
            statusTextView = view.findViewById(R.id.brand_status);
            createdAtTextView = view.findViewById(R.id.brand_created_at);
            updatedAtTextView = view.findViewById(R.id.brand_updated_at);

            editButton = view.findViewById(R.id.button_edit_brand);
            deleteButton = view.findViewById(R.id.button_delete_brand);
        }
    }

    public void setCategories(List<Brand> categories) {
        this.categories = categories;
        notifyDataSetChanged();
    }

    // Listener interface for edit and delete actions
    public interface BrandActionListener {
        void onEditBrand(Brand Brand);
        void onDeleteBrand(Brand Brand);
    }
}
