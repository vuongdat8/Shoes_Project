package com.example.shoes_project.ui.fragments.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoes_project.R;
import com.example.shoes_project.controller.UserController;
import com.example.shoes_project.data.db.entity.User;
import com.example.shoes_project.ui.adapter.UserAdapter;
import com.example.shoes_project.ui.view.base.UserView;

import java.util.List;

public class AdminUserListFragment extends Fragment implements UserView {
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private UserController userController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_user_list, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_users);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        userAdapter = new UserAdapter(getContext());
        recyclerView.setAdapter(userAdapter);

        userController = new UserController(this);
        userController.loadUsers();

        return view;
    }

    @Override
    public void displayUsers(List<User> users) {
        userAdapter.setUsers(users);
    }

    @Override
    public void displayUserDetails(User user) {
        // Không dùng trong Fragment này
    }

    @Override
    public void onLoginSuccess(User user) {
        // Không dùng trong Fragment này
    }