package com.example.shoes_project.data;

import android.annotation.SuppressLint;
import android.content.Context;

import com.example.shoes_project.data.db.AppDatabase;
import com.example.shoes_project.data.db.entity.Brand;
import com.example.shoes_project.data.db.entity.Category;
import com.example.shoes_project.data.db.entity.Product;
import com.example.shoes_project.data.db.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class SeedData {
    private final AppDatabase db;
    private final ExecutorService executorService;
    public SeedData(Context context) {
        DatabaseClient dbClient = DatabaseClient.getInstance(context);
        this.db = dbClient.getAppDatabase();
        this.executorService = dbClient.getExecutorService();
    }
    @SuppressLint("NewApi")
    public void seedData() {
        executorService.execute(() -> {
            // Kiểm tra xem database đã có dữ liệu chưa
            if (db.userDao().getAllUsers().isEmpty()) {
                seedUsers();
                seedCategories();
                seedBrands();
                seedProducts();
            }
        });
    }

    private void seedUsers() {
        List<User> users = new ArrayList<>();

        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword("password");
        admin.setEmail("admin@example.com");
        admin.setPhone(null); // Không có trong C# mẫu
        admin.setVerified(true);
        admin.setRole(0); // Admin
        users.add(admin);

        User customer1 = new User();
        customer1.setUsername("customer1");
        customer1.setPassword("password");
        customer1.setEmail("customer1@example.com");
        customer1.setPhone("+84 386570165");
        customer1.setVerified(true);
        customer1.setRole(2); // Customer
        users.add(customer1);

        for (User user : users) {
            db.userDao().insert(user);
        }
    }

    private void seedCategories(){

    }
    private void seedBrands(){

    }
    private void seedProducts(){
        List<Brand> brands = db.brandDao().getAllBrands();
        List<Category> categories = db.categoryDao().getAllCategories();

        if (brands.isEmpty() || categories.isEmpty()) {
            return; // Không thêm sản phẩm nếu thiếu brand hoặc category
        }
        // Ánh xạ Brand giày
        Brand nike = brands.stream().filter(b -> "Nike".equalsIgnoreCase(b.getName())).findFirst().orElse(null);
        Brand adidas = brands.stream().filter(b -> "Adidas".equalsIgnoreCase(b.getName())).findFirst().orElse(null);
        Brand puma = brands.stream().filter(b -> "Puma".equalsIgnoreCase(b.getName())).findFirst().orElse(null);
        Brand converse = brands.stream().filter(b -> "Converse".equalsIgnoreCase(b.getName())).findFirst().orElse(null);
        Brand vans = brands.stream().filter(b -> "Vans".equalsIgnoreCase(b.getName())).findFirst().orElse(null);
        Brand newBalance = brands.stream().filter(b -> "New Balance".equalsIgnoreCase(b.getName())).findFirst().orElse(null);
        Brand bitis = brands.stream().filter(b -> "Bitis".equalsIgnoreCase(b.getName())).findFirst().orElse(null);
        Brand jordan = brands.stream().filter(b -> "Jordan".equalsIgnoreCase(b.getName())).findFirst().orElse(null);

// Ánh xạ Category giày
        Category sneaker = categories.stream().filter(c -> "Sneaker".equalsIgnoreCase(c.getName())).findFirst().orElse(null);
        Category running = categories.stream().filter(c -> "Running".equalsIgnoreCase(c.getName())).findFirst().orElse(null);
        Category sandals = categories.stream().filter(c -> "Sandals".equalsIgnoreCase(c.getName())).findFirst().orElse(null);
        Category boots = categories.stream().filter(c -> "Boots".equalsIgnoreCase(c.getName())).findFirst().orElse(null);
        Category loafers = categories.stream().filter(c -> "Loafers".equalsIgnoreCase(c.getName())).findFirst().orElse(null);
        Category sport = categories.stream().filter(c -> "Sport".equalsIgnoreCase(c.getName())).findFirst().orElse(null);

// Kiểm tra đủ dữ liệu
        if (nike == null || adidas == null || puma == null || converse == null || vans == null || newBalance == null || bitis == null || jordan == null ||
                sneaker == null || running == null || sandals == null || boots == null || loafers == null || sport == null) {
            System.err.println(" Thiếu dữ liệu thương hiệu hoặc danh mục giày. Dừng thêm sản phẩm.");
            return;
        }
        List<Product> products = new ArrayList<>();
        // Nike Shoes
        products.add(createProduct("Nike Air Max", 129.99, 199.99, "Iconic sports shoes", 100, 20, sneaker, nike, "nike_air_max.png"));
        products.add(createProduct("Nike Running Tights", 49.99, 79.99, "Breathable running tights", 200, 40, running, nike, "nike_running_tights.png"));
        products.add(createProduct("Nike Backpack", 49.99, 79.99, "Durable backpack for sports and daily use", 200, 40, sport, nike, "nike_backpack.png"));
        products.add(createProduct("Nike Sport Shorts", 39.99, 59.99, "Comfortable shorts for running", 180, 35, sport, nike, "nike_sport_shorts.png"));
        products.add(createProduct("Nike Running Jacket", 69.99, 99.99, "Lightweight jacket for running", 110, 20, running, nike, "nike_running_jacket.png"));
        products.add(createProduct("Nike Sports Cap", 19.99, 29.99, "Adjustable sports cap", 150, 30, boots, nike, "nike_sports_cap.png"));

// Adidas Shoes
        products.add(createProduct("Adidas Ultraboost", 179.99, 249.99, "Premium running shoes", 80, 15, sneaker, adidas, "adidas_ultraboost.png"));
        products.add(createProduct("Adidas Running Shoes", 119.99, 179.99, "High-performance running shoes", 120, 25, running, adidas, "adidas_running_shoes.png"));
        products.add(createProduct("Adidas Socks", 9.99, 19.99, "Comfortable sports socks", 250, 50, loafers, adidas, "adidas_socks.png"));
        products.add(createProduct("Adidas Yoga Mat", 39.99, 59.99, "Non-slip yoga mat", 100, 20, sandals, adidas, "adidas_yoga_mat.png"));

// Puma Shoes
        products.add(createProduct("Puma Running Shoes", 99.99, 149.99, "Comfortable running shoes", 110, 20, running, puma, "puma_running_shoes.png"));
        products.add(createProduct("Puma Basketball Shoes", 119.99, 179.99, "High-performance basketball shoes", 80, 15, sandals, puma, "puma_basketball_shoes.png"));
        products.add(createProduct("Puma Sport Cap", 24.99, 39.99, "Comfortable sports cap", 180, 35, sport, puma, "puma_sport_cap.png"));
        products.add(createProduct("Puma Tracksuit", 89.99, 129.99, "Comfortable tracksuit for sports", 100, 20, sport, puma, "puma_tracksuit.png"));

// Converse Shoes
        products.add(createProduct("Converse Chuck Taylor", 89.99, 129.99, "Classic canvas sneaker", 100, 20, sneaker, converse, "converse_chuck_taylor.png"));

// Vans Shoes
        products.add(createProduct("Vans Old Skool", 79.99, 119.99, "Skateboarding sneaker", 90, 18, sneaker, vans, "vans_old_skool.png"));

// New Balance
        products.add(createProduct("New Balance 574", 99.99, 149.99, "Retro running sneakers", 120, 22, sneaker, newBalance, "new_balance_574.png"));

// Jordan Shoes
        products.add(createProduct("Air Jordan 1", 149.99, 219.99, "Iconic basketball shoes", 70, 15, sport, jordan, "air_jordan_1.png"));

// Bitis Shoes
        products.add(createProduct("Bitis Hunter X", 69.99, 99.99, "Lightweight daily shoes", 200, 35, sandals, bitis, "bitis_hunter_x.png"));

        for (Product product : products) {
            db.productDao().insert(product);
        }
    }
    private Product createProduct(String name, double importPrice, double sellingPrice, String description,
                                  int stock, int sold, Category category, Brand brand, String image) {
        Product product = new Product();
        product.setName(name);
        product.setImportPrice(importPrice);
        product.setSellingPrice(sellingPrice);
        product.setDescription(description);
        product.setStock(stock);
        product.setSold(sold);
        product.setCategoryId(category.getCategoryId());
        product.setBrandId(brand.getBrandId());
        product.setImage(image);
        return product;
    }
}
