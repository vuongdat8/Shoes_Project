package com.example.shoes_project.controller;

import android.content.Context;

import com.example.shoes_project.data.DatabaseClient;
import com.example.shoes_project.data.db.entity.Brand;
import com.example.shoes_project.data.dto.BrandProductsDto;
import com.example.shoes_project.ui.fragments.product.AdminProductAddFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BrandController {
    private DatabaseClient dbClient;
    public BrandController(Context context) {
        this.dbClient = DatabaseClient.getInstance(context);
    }

    // Trả về DTO cho danh sách có số lượng sản phẩm
    public void loadBrandsWithProductCount(BrandListCallback callback) {
        dbClient.getExecutorService().execute(() -> {
            final List<Brand> brands = dbClient.getAppDatabase().brandDao().getAllBrands();
            List<BrandProductsDto> brandDtos = new ArrayList<>();
            for (Brand brand : brands) {
                int productCount = dbClient.getAppDatabase().brandDao().getProductCountByBrand(brand.getBrandId());
                brandDtos.add(new BrandProductsDto(brand, productCount));
            }
            dbClient.getMainHandler().post(() -> callback.onBrandsLoaded(brandDtos));
        });
    }

    // Trả về model thường cho các trường hợp khác
    public void loadBrands(BrandSimpleCallback callback) {
        dbClient.getExecutorService().execute(() -> {
            final List<Brand> brands = dbClient.getAppDatabase().brandDao().getAllBrands();
            dbClient.getMainHandler().post(() -> callback.onSimpleBrandsLoaded(brands));
        });
    }

    public void loadBrandDetails(UUID brandId, BrandDetailCallback callback) {
        dbClient.getExecutorService().execute(() -> {
            final Brand brand = dbClient.getAppDatabase().brandDao().getBrandById(brandId);
            dbClient.getMainHandler().post(() -> callback.onBrandLoaded(brand));
        });
    }

    public void addBrand(Brand brand) {
        dbClient.getExecutorService().execute(() -> dbClient.getAppDatabase().brandDao().insert(brand));
    }

    public void updateBrand(Brand brand) {
        dbClient.getExecutorService().execute(() -> dbClient.getAppDatabase().brandDao().update(brand));
    }

    public void deleteBrand(Brand brand) {
        dbClient.getExecutorService().execute(() -> dbClient.getAppDatabase().brandDao().delete(brand));
    }

    // Callback cho DTO
    public interface BrandListCallback {
        void onBrandsLoaded(List<BrandProductsDto> brands);
    }

    // Callback cho model thường
    public interface BrandSimpleCallback {
        void onSimpleBrandsLoaded(List<Brand> brands);
    }

    public interface BrandDetailCallback {
        void onBrandLoaded(Brand brand);
    }
}
