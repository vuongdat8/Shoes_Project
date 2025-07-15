package com.example.shoes_project.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoes_project.R;
import com.example.shoes_project.model.User;

import java.util.List;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder> {

    private List<User> userList;
    private OnCustomerActionListener listener;

    public CustomerAdapter(List<User> userList, OnCustomerActionListener listener) {
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_customer, parent, false);
        return new CustomerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {
        User user = userList.get(position);
        holder.nameTextView.setText(user.getFname());
        holder.emailTextView.setText(user.getEmail());
        holder.roleTextView.setText("Vai trò: " + (user.isRole() ? "Quản trị viên" : "Khách hàng"));

        holder.editButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(user);
            }
        });

        holder.deleteButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList != null ? userList.size() : 0;
    }

    public void setUsers(List<User> newUserList) {
        this.userList = newUserList;
        notifyDataSetChanged();
    }

    public static class CustomerViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView emailTextView;
        TextView roleTextView;
        Button editButton;
        Button deleteButton;

        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.text_customer_name);
            emailTextView = itemView.findViewById(R.id.text_customer_email);
            roleTextView = itemView.findViewById(R.id.text_customer_role);
            editButton = itemView.findViewById(R.id.button_edit_customer);
            deleteButton = itemView.findViewById(R.id.button_delete_customer);
        }
    }

    public interface OnCustomerActionListener {
        void onEditClick(User user);
        void onDeleteClick(User user);
    }
}