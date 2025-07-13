package com.example.shoes_project.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

@Entity
public class Order {
    @PrimaryKey(autoGenerate = true)
    public int orderId;

    public int userId;

    public String date;

    public double totalAmount;

    public Order() { }

    public Order(int userId, String date, double totalAmount) {
        this.userId = userId;
        this.date = date;
        this.totalAmount = totalAmount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
