package com.example.shoes_project.ui.fragments.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.shoes_project.R;
import com.example.shoes_project.controller.UserController;
import com.example.shoes_project.ui.view.base.UserView;

import java.util.List;
import java.util.UUID;

public class AdminUserAddFragment extends Fragment implements UserView {
    private UserController userController;
    private EditText usernameEditText, emailEditText, phoneEditText, roleEditText, verifiedEditText;
    private Button saveButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_user_add, container, false);

        usernameEditText = view.findViewById(R.id.user_username);
        emailEditText = view.findViewById(R.id.user_email);
        phoneEditText = view.findViewById(R.id.user_phone);
        roleEditText = view.findViewById(R.id.user_role);
        verifiedEditText = view.findViewById(R.id.user_verified);
        saveButton = view.findViewById(R.id.save_button);

        // Khởi tạo UserController với this (fragment implements UserView)
        userController = new UserController(this);

        saveButton.setOnClickListener(v -> addUser());

        return view;
    }

    private void addUser() {
        try {
            User newUser = new User();
            newUser.setUserId(UUID.randomUUID()); // Tạo UUID mới
            newUser.setUsername(usernameEditText.getText().toString());
            newUser.setEmail(emailEditText.getText().toString());
            newUser.setPhone(phoneEditText.getText().toString());
            newUser.setRole(Integer.parseInt(roleEditText.getText().toString()));
            newUser.setVerified(Boolean.parseBoolean(verifiedEditText.getText().toString()));

            userController.insertUser(newUser);
            // Toast sẽ được hiển thị trong onLoginSuccess
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Implement các phương thức của UserView
    @Override
    public void displayUsers(List<User> users) {
        // Không cần trong fragment này
    }

    @Override
    public void displayUserDetails(User user) {
        // Không cần trong fragment này
    }

    @Override
    public void onLoginSuccess(User user) {
        Toast.makeText(getContext(), "Successfully", Toast.LENGTH_SHORT).show();
        getParentFragmentManager().popBackStack();
    }
}