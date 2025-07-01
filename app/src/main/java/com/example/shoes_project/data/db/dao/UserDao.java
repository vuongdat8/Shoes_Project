package com.example.shoes_project.data.db.dao;



import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Insert;
import androidx.room.Update;
import androidx.room.Delete;
import com.example.shoes_project.data.db.entity.User;
import java.util.List;
import java.util.UUID;

@Dao
public interface UserDao extends BaseDao<User> {
    @Query("SELECT * FROM users")
    List<User> getAllUsers();

    @Query("SELECT * FROM users WHERE userId = :userId")
    User getUserById(UUID userId);

    @Query("SELECT * FROM users WHERE username = :username")
    User getUserByUsername(String username);

    @Query("SELECT * FROM users WHERE username = :username OR email = :email OR phone = :phone LIMIT 1")
    User findByUsernameEmailOrPhone(String username, String email, String phone);

    @Insert
    void insert(User user);

    @Update
    void update(User user);

    @Delete
    void delete(User user);

}