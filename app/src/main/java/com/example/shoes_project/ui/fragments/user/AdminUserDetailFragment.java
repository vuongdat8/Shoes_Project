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
import com.example.shoes_project.data.db.entity.User;
import com.example.shoes_project.ui.view.base.UserView;

import java.util.List;
import java.util.UUID;

public class AdminUserDetailFragment extends Fragment implements UserView {
    private UserController userController;
    private EditText usernameEditText, emailEditText, phoneEditText, roleEditText, verifiedEditText;
    private Button saveButton, deleteButton;
    private User currentUser;

    private static final String ARG_USER_ID = "user_id";

    public static AdminUserDetailFragment newInstance(UUID userId) {
        AdminUserDetailFragment fragment = new AdminUserDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, userId.toString());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_user_detail, container, false);

        usernameEditText = view.findViewById(R.id.user_username);
        emailEditText = view.findViewById(R.id.user_email);
        phoneEditText = view.findViewById(R.id.user_phone);
        roleEditText = view.findViewById(R.id.user_role);
        verifiedEditText = view.findViewById(R.id.user_verified);
        saveButton = view.findViewById(R.id.save_button);
        deleteButton = view.findViewById(R.id.delete_button);

        userController = new UserController(this);
        UUID userId = UUID.fromString(getArguments().getString(ARG_USER_ID));
        userController.loadUserDetails(userId);

        saveButton.setOnClickListener(v -> updateUser());
        deleteButton.setOnClickListener(v -> deleteUser());

        return view;
    }

    @Override
    public void displayUserDetails(User user) {
        currentUser = user;
        if (user != null) {
            usernameEditText.setText(user.getUsername() != null ? user.getUsername() : "");
            emailEditText.setText(user.getEmail() != null ? user.getEmail() : "");
            phoneEditText.setText(user.getPhone() != null ? user.getPhone() : "");
            roleEditText.setText(String.valueOf(user.getRole()));
            verifiedEditText.setText(String.valueOf(user.isVerified()));
        }
    }

    private void updateUser() {
        try {
            if (currentUser != null) {
                currentUser.setUsername(usernameEditText.getText().toString());
                currentUser.setEmail(emailEditText.getText().toString());
                currentUser.setPhone(phoneEditText.getText().toString());
                currentUser.setRole(Integer.parseInt(roleEditText.getText().toString()));
                currentUser.setVerified(Boolean.parseBoolean(verifiedEditText.getText().toString()));

                userController.updateUser(currentUser);
                Toast.makeText(getContext(), "User updated successfully", Toast.LENGTH_SHORT).show();
                getParentFragmentManager().popBackStack();
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteUser() {
        if (currentUser != null) {
            userController.deleteUser(currentUser);
            Toast.makeText(getContext(), "User deleted successfully", Toast.LENGTH_SHORT).show();
            getParentFragmentManager().popBackStack();
        }
    }

    @Override
    public void onLoginSuccess(User user) { }

    @Override
    public void displayUsers(List<User> users) { }
}