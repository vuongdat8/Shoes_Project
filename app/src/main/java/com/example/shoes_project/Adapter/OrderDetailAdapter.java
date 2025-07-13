package com.example.shoes_project.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.shoes_project.model.OrderDetail;
import com.example.shoes_project.R;

import java.util.ArrayList;
import java.util.List;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.OrderDetailViewHolder> {

    private List<OrderDetail> orderDetails;

    public OrderDetailAdapter(List<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails != null ? orderDetails : new ArrayList<>();
    }

    @Override
    public OrderDetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_detail, parent, false);
        return new OrderDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OrderDetailViewHolder holder, int position) {
        // Kiểm tra nếu danh sách không rỗng
        if (orderDetails != null && !orderDetails.isEmpty()) {
            OrderDetail orderDetail = orderDetails.get(position);
            holder.productNameText.setText(orderDetail.productName);
            holder.quantityText.setText(String.valueOf(orderDetail.quantity));
            holder.priceText.setText(String.format("$%.2f", orderDetail.price));
        }
    }

    @Override
    public int getItemCount() {
        // Trả về kích thước của danh sách hoặc 0 nếu danh sách null
        return orderDetails != null ? orderDetails.size() : 0;
    }

    public class OrderDetailViewHolder extends RecyclerView.ViewHolder {
        TextView productNameText, quantityText, priceText;

        public OrderDetailViewHolder(View itemView) {
            super(itemView);
            productNameText = itemView.findViewById(R.id.product_name);
            quantityText = itemView.findViewById(R.id.quantity);
            priceText = itemView.findViewById(R.id.price);
        }
    }
}
