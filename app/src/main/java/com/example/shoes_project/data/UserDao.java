package com.example.shoes_project.data;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface UserDao {
    @Insert
    void insert(User user);

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

}
