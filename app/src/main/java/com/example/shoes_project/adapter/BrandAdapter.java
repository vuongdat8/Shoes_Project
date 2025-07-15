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
import com.example.shoes_project.ui.admin.BrandDetailActivity; // <-- Bạn cần tạo activity này
import com.example.shoes_project.ui.admin.Category_Admin_Activity;

import java.util.List;

public class BrandAdapter extends RecyclerView.Adapter<BrandAdapter.BrandViewHolder> {

    private List<Brand> brands;
    private BrandActionListener actionListener;

    public BrandAdapter(List<Brand> brands, BrandActionListener actionListener) {
        this.brands = brands;
        this.actionListener = actionListener;
    }

    @Override
    public BrandViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.brand_item, parent, false); // <-- Bạn cần tạo brand_item.xml
        return new BrandViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(BrandViewHolder holder, int position) {
        Brand brand = brands.get(position);
        holder.nameTextView.setText(brand.getName());
        holder.descriptionTextView.setText(brand.getDescription());

        holder.statusTextView.setText("Trạng thái: " + (brand.isActive() ? "Hoạt động" : "Ngừng hoạt động"));
        holder.createdAtTextView.setText("Ngày tạo: " + (brand.getCreatedAt() != null ? brand.getCreatedAt() : "N/A"));
        holder.updatedAtTextView.setText("Ngày cập nhật: " + (brand.getUpdatedAt() != null ? brand.getUpdatedAt() : "N/A"));

        if (brand.getImageUrl() != null && !brand.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(Uri.parse(brand.getImageUrl()))
                    .placeholder(R.drawable.a1)
                    .error(R.drawable.a2)
                    .into(holder.imageView);
        } else {
            holder.imageView.setImageResource(R.drawable.a4);
        }

        // Bấm toàn item để xem chi tiết
        holder.itemLayout.setOnClickListener(v -> {
            Brand currentBrand = brands.get(holder.getAdapterPosition());

            // 2. Tạo Intent để mở màn hình quản lý Category
            Intent intent = new Intent(v.getContext(), Category_Admin_Activity.class);

            // 3. QUAN TRỌNG: Gửi ID của Brand sang Activity tiếp theo
            // Key "extra_brand_id" phải khớp với key bạn định nghĩa trong Category_Admin_Activity
            intent.putExtra(Category_Admin_Activity.EXTRA_BRAND_ID, currentBrand.getId());

            // 4. Khởi động Activity
            v.getContext().startActivity(intent);
        });

        holder.editButton.setOnClickListener(v -> actionListener.onEditBrand(brand));
        holder.deleteButton.setOnClickListener(v -> actionListener.onDeleteBrand(brand));
    }

    @Override
    public int getItemCount() {
        return brands != null ? brands.size() : 0;
    }

    public void setBrands(List<Brand> brands) {
        this.brands = brands;
        notifyDataSetChanged();
    }

    public static class BrandViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView, descriptionTextView, statusTextView, createdAtTextView, updatedAtTextView;
        public ImageView imageView;
        public Button editButton, deleteButton;
        public LinearLayout itemLayout;

        public BrandViewHolder(View view) {
            super(view);
            itemLayout = view.findViewById(R.id.linearLayout_item_root); // phải có ID này trong brand_item.xml

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

    public interface BrandActionListener {
        void onEditBrand(Brand brand);
        void onDeleteBrand(Brand brand);
    }


}
