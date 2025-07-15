package com.example.shoes_project.data;

import androidx.lifecycle.LiveData; // Import LiveData
import androidx.room.Dao;
import androidx.room.Delete; // Import Delete
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update; // Import Update (cho chức năng update)

import com.example.shoes_project.model.User;

import java.util.List;

@Dao
public interface UserDao {
    @Insert
    void insert(User user);

    // Thêm phương thức update
    @Update
    void update(User user);

    // Thêm phương thức delete
    @Delete
    void delete(User user);

    @Query("SELECT * FROM User WHERE email = :email AND password = :password")
    User login(String email, String password);

    @Query("SELECT COUNT(*) FROM User")
    int countUsers();

    @Query("SELECT * FROM User WHERE email = :email LIMIT 1")
    User getUserByEmail(String email);

    @Query("UPDATE user SET password = :password WHERE email = :email")
    int updatePassword(String email, String password);

    @Query("SELECT * FROM user WHERE id = :id LIMIT 1")
    User getUserById(int id);

    @Query("SELECT id FROM user WHERE email = :email LIMIT 1")
    Integer getUserIdByEmail(String email);

    @Query("UPDATE user SET fname = :fullName, email = :email WHERE id = :id")
    int updateUserInfo(int id, String fullName, String email);

    // Thêm phương thức để lấy tất cả người dùng, trả về LiveData để quan sát thay đổi
    @Query("SELECT * FROM User")
    LiveData<List<User>> getAllUsers();

    // Trong UserDao.java

}