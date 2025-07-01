package com.example.shoes_project.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.shoes_project.R;
import com.example.shoes_project.data.db.entity.User;
import com.example.shoes_project.ui.view.user.AdminUserListActivity;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private Context context;
    private List<User> users;

    public UserAdapter(Context context) {
        this.context = context;
        this.users = new ArrayList<>();
    }

    public void setUsers(List<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_card, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);

        holder.usernameTextView.setText(user.getUsername());
        holder.emailTextView.setText("Email: " + (user.getEmail() != null ? user.getEmail() : "N/A"));
        holder.phoneTextView.setText("Phone: " + (user.getPhone() != null ? user.getPhone() : "N/A"));
        //role: 0-admin, 1-staff, 2-customer
        holder.roleTextView.setText("Role: " + (user.getRole() == 0 ? "Admin" : (user.getRole() == 1 ? "Staff" : "Customer")));
        holder.verifiedTextView.setText("Verified: " + (user.isVerified() ? "Yes" : "No"));

        holder.moreButton.setOnClickListener(v -> {
            ((AdminUserListActivity) context).showUserDetail(user.getUserId());
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView, emailTextView, phoneTextView, roleTextView, verifiedTextView;
        ImageButton moreButton;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.user_username);
            emailTextView = itemView.findViewById(R.id.user_email);
            phoneTextView = itemView.findViewById(R.id.user_phone);
            roleTextView = itemView.findViewById(R.id.user_role);
            verifiedTextView = itemView.findViewById(R.id.user_verified);
            moreButton = itemView.findViewById(R.id.user_more_button);
        }
    }
}