package com.example.shoes_project.ui.fragments.product;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.shoes_project.controller.BrandController;
import com.example.shoes_project.controller.CategoryController;
import com.example.shoes_project.controller.ProductController;
import com.example.shoes_project.ui.view.base.ProductView;
import com.example.shoes_project.data.db.entity.Brand;
import com.example.shoes_project.data.db.entity.Category;
import com.example.shoes_project.data.db.entity.Product;
import com.example.shoes_project.ui.adapter.BrandAdapter;
import com.example.shoes_project.ui.adapter.CategoryAdapter;
import com.example.shoes_project.R;
import com.example.shoes_project.ui.view.base.ProductView;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AdminProductAddFragment extends Fragment implements ProductView, CategoryController.CategorySimpleCallback, BrandController.BrandSimpleCallback {
private ProductController productController;
    private CategoryController categoryController;
    private BrandController brandController;
    private EditText nameEditText, sellingPriceEditText, importPriceEditText, stockEditText, soldEditText, descriptionEditText;
    private Spinner categorySpinner, brandSpinner;
    private Button saveButton;
    private List<Category> categoryList = new ArrayList<>();
    private List<Brand> brandList = new ArrayList<>();
    private CategoryAdapter categoryAdapter;
    private BrandAdapter brandAdapter;
    private static final String ARG_PRODUCT_ID = "product_id";
    public static AdminProductAddFragment newInstance(UUID productId) {
        AdminProductAddFragment fragment = new AdminProductAddFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PRODUCT_ID, productId.toString());
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize your controllers and adapters here
        // productController = new ProductController(this);
        // categoryController = new CategoryController(this);
        // brandController = new BrandController(this);
        // ...
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_product_detail, container, false);
        nameEditText = view.findViewById(R.id.shoes_name);
        sellingPriceEditText = view.findViewById(R.id.shoes_selling_price);
        importPriceEditText = view.findViewById(R.id.shoes_import_price);
        stockEditText = view.findViewById(R.id.shoes_stock);
        soldEditText = view.findViewById(R.id.shoes_sold);
        categorySpinner = view.findViewById(R.id.shoes_category);
        brandSpinner = view.findViewById(R.id.shoes_brand);
        descriptionEditText = view.findViewById(R.id.shoes_description);

        saveButton = view.findViewById(R.id.save_button);
        Button deleteButton = view.findViewById(R.id.delete_button);
        deleteButton.setVisibility(View.GONE);

        productController = new ProductController();
        categoryController = new CategoryController(getContext());
        brandController = new BrandController(getContext());

        categoryAdapter = new CategoryAdapter(getContext(), categoryList);
        brandAdapter = new BrandAdapter(getContext(), brandList);

        categorySpinner.setAdapter(categoryAdapter);
        brandSpinner.setAdapter(brandAdapter);

        brandController.loadBrands(this); // Sử dụng hàm trả model thường
        categoryController.loadCategories(this); // Sử dụng hàm trả model thường

        Product newProduct = new Product();
        newProduct.setProductId(UUID.fromString(getArguments().getString(ARG_PRODUCT_ID)));
        displayProductDetails(newProduct);

        saveButton.setOnClickListener(v -> addProduct());
        return view;
    }
    @Override
    public void displayProductDetails(Product product) {
        nameEditText.setText(product.getName() != null ? product.getName() : "");
        sellingPriceEditText.setText(String.valueOf(product.getSellingPrice()));
        importPriceEditText.setText(String.valueOf(product.getImportPrice()));
        stockEditText.setText(String.valueOf(product.getStock()));
        soldEditText.setText(String.valueOf(product.getSold()));
        descriptionEditText.setText(product.getDescription() != null ? product.getDescription() : "");

        if (product.getCategoryId() != null && !categoryList.isEmpty()) {
            for (int i = 0; i < categoryList.size(); i++) {
                if (categoryList.get(i).getCategoryId().equals(product.getCategoryId())) {
                    categorySpinner.setSelection(i);
                    break;
                }
            }
        }
        if (product.getBrandId() != null && !brandList.isEmpty()) {
            for (int i = 0; i < brandList.size(); i++) {
                if (brandList.get(i).getBrandId().equals(product.getBrandId())) {
                    brandSpinner.setSelection(i);
                    break;
                }
            }
        }
    }

    @Override
    public void displayProducts(List<Product> products) {
        // Không dùng trong Fragment này
    }

    @Override
    public void onSimpleBrandsLoaded(List<Brand> brands) {
        brandAdapter.clear();
        brandAdapter.addAll(brands);
        brandAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSimpleCategoriesLoaded(List<Category> categories) {
        categoryAdapter.clear();
        categoryAdapter.addAll(categories);
        categoryAdapter.notifyDataSetChanged();
    }

    private void addProduct() {
        try {
            Product newProduct = new Product();
            newProduct.setProductId(UUID.fromString(getArguments().getString(ARG_PRODUCT_ID)));
            newProduct.setName(nameEditText.getText().toString());
            newProduct.setSellingPrice(Double.parseDouble(sellingPriceEditText.getText().toString()));
            newProduct.setImportPrice(Double.parseDouble(importPriceEditText.getText().toString()));
            newProduct.setStock(Integer.parseInt(stockEditText.getText().toString()));
            newProduct.setSold(Integer.parseInt(soldEditText.getText().toString()));
            newProduct.setCategoryId(categoryList.get(categorySpinner.getSelectedItemPosition()).getCategoryId());
            newProduct.setBrandId(brandList.get(brandSpinner.getSelectedItemPosition()).getBrandId());
            newProduct.setDescription(descriptionEditText.getText().toString());

            productController.addProduct(newProduct);
            Toast.makeText(getContext(), "Product added", Toast.LENGTH_SHORT).show();
            getParentFragmentManager().popBackStack();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onCategoryLoaded(List<Category> categories) {
        // Handle the loaded categories
    }

    @Override
    public void onCategoryError(String error) {
        // Handle the error
    }
}
