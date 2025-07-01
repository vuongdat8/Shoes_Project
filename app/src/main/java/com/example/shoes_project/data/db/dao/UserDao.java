package com.example.shoes_project.data.db.dao;

import com.example.shoes_project.data.db.entity.User;

public interface UserDao {
    void insert(User user);

    CharSequence getAllUsers();
}
