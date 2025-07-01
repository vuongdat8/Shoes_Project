package com.example.shoes_project.data.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.shoes_project.data.db.dao.BrandDao;
import com.example.shoes_project.data.db.dao.CategoryDao;
import com.example.shoes_project.data.db.dao.ConversationDao;
import com.example.shoes_project.data.db.dao.FeedbackDao;
import com.example.shoes_project.data.db.dao.MessageDao;
import com.example.shoes_project.data.db.dao.OrderDao;
import com.example.shoes_project.data.db.dao.OrderDetailDao;
import com.example.shoes_project.data.db.dao.ProductDao;
import com.example.shoes_project.data.db.dao.UserDao;
import com.example.shoes_project.data.db.entity.Brand;
import com.example.shoes_project.data.db.entity.Category;
import com.example.shoes_project.data.db.entity.Conversation;
import com.example.shoes_project.data.db.entity.Feedback;
import com.example.shoes_project.data.db.entity.Message;
import com.example.shoes_project.data.db.entity.Order;
import com.example.shoes_project.data.db.entity.OrderDetail;
import com.example.shoes_project.data.db.entity.Product;
import com.example.shoes_project.data.db.entity.User;

@Database(entities = {User.class, Product.class, Order.class, OrderDetail.class, Conversation.class, Message.class, Feedback.class, Category.class, Brand.class},
        version = 4,
        exportSchema = true
)
@TypeConverters({DateConverter.class})

public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract ProductDao productDao();
    public abstract OrderDao orderDao();
    public abstract OrderDetailDao orderDetailDao();
    public abstract ConversationDao conversationDao();
    public abstract MessageDao messageDao();
    public abstract FeedbackDao feedbackDao();
    public abstract CategoryDao categoryDao();
    public abstract BrandDao brandDao();
}
