package com.example.shoes_project.model;// package com.example.shoes_project.model.relations;

import androidx.room.Embedded;
import androidx.room.Relation;
import com.example.shoes_project.model.Brand;
import com.example.shoes_project.model.Category;

public class CategoryWithBrand {
    @Embedded
    public Category category;

    @Relation(
            parentColumn = "brandId",
            entityColumn = "id"
    )
    public Brand brand;
}