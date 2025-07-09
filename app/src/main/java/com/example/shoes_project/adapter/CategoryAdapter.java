package com.example.shoes_project.adapter;

import android.content.Intent; // REQUIRED: Import Intent for starting new activities
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout; // REQUIRED: Import LinearLayout for the clickable item
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.shoes_project.R;
import com.example.shoes_project.model.Category;
import com.example.shoes_project.ui.admin.CategoryDetailActivity; // REQUIRED: Import your detail activity
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<Category> categories;
    private CategoryActionListener actionListener;
    public LinearLayout itemLayout; // Khai báo này là đúng

    public CategoryAdapter(List<Category> categories, CategoryActionListener actionListener) {
        this.categories = categories;
        this.actionListener = actionListener;
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate your category_item.xml layout
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false);
        return new CategoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.nameTextView.setText(category.getName());
        holder.descriptionTextView.setText(category.getDescription());

        // Set isActive status text
        holder.statusTextView.setText("Trạng thái: " + (category.isActive() ? "Hoạt động" : "Ngừng hoạt động"));

        // Set createdAt and updatedAt text
        holder.createdAtTextView.setText("Ngày tạo: " + (category.getCreatedAt() != null ? category.getCreatedAt() : "N/A"));
        holder.updatedAtTextView.setText("Ngày cập nhật: " + (category.getUpdatedAt() != null ? category.getUpdatedAt() : "N/A"));

        // Load image using Glide
        if (category.getImageUrl() != null && !category.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(Uri.parse(category.getImageUrl())) // Parse the URI string back to Uri object
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
            Intent intent = new Intent(v.getContext(), CategoryDetailActivity.class);
            // Pass the entire Category object to the detail activity
            intent.putExtra(CategoryDetailActivity.EXTRA_CATEGORY, category);
            v.getContext().startActivity(intent);
        });

        // Xử lý sự kiện nhấn nút Edit
        holder.editButton.setOnClickListener(v -> actionListener.onEditCategory(category));

        // Xử lý sự kiện nhấn nút Delete
        holder.deleteButton.setOnClickListener(v -> actionListener.onDeleteCategory(category));
    }

    @Override
    public int getItemCount() {
        return categories != null ? categories.size() : 0;
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView, descriptionTextView, statusTextView, createdAtTextView, updatedAtTextView;
        public ImageView imageView;
        public Button editButton, deleteButton;
        public LinearLayout itemLayout; // REQUIRED: Add reference to the root LinearLayout of the item

        public CategoryViewHolder(View view) {
            super(view);
            // REQUIRED: Initialize itemLayout by finding its ID in the item's view
            itemLayout = view.findViewById(R.id.linearLayout_item_root); // Make sure you add this ID in category_item.xml

            nameTextView = view.findViewById(R.id.category_name);
            descriptionTextView = view.findViewById(R.id.category_description);
            imageView = view.findViewById(R.id.category_image);
            statusTextView = view.findViewById(R.id.category_status);
            createdAtTextView = view.findViewById(R.id.category_created_at);
            updatedAtTextView = view.findViewById(R.id.category_updated_at);

            editButton = view.findViewById(R.id.button_edit_category);
            deleteButton = view.findViewById(R.id.button_delete_category);
        }
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
        notifyDataSetChanged();
    }

    // Listener interface for edit and delete actions
    public interface CategoryActionListener {
        void onEditCategory(Category category);
        void onDeleteCategory(Category category);
    }
}