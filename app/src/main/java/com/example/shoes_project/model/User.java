package com.example.shoes_project.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class User {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String fname;

    public String email;
    public String password;
    public boolean role;

    public User() { }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isRole() {
        return role;
    }

    public void setRole(boolean role) {
        this.role = role;
    }

//    @Ignore
//    public User(String fname, String email, String password, boolean role) {
//        this.email = email;
//        this.password = password;
//    }
public User(String fname, String email, String password, boolean role) {
    this.fname = fname;
    this.email = email;
    this.password = password;
    this.role = role;
    }
}

