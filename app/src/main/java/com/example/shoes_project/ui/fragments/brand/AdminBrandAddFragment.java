package com.example.shoes_project.ui.fragments.brand;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

import com.example.shoes_project.R;
import com.example.shoes_project.controller.BrandController;
import com.example.shoes_project.data.db.entity.Brand;

import java.util.UUID;

public class AdminBrandAddFragment extends Fragment {
    private EditText nameEditText, descriptionEditText;
    private Button saveButton;
    private BrandController brandController;

    private static final String ARG_BRAND_ID = "brand_id";

    public static AdminBrandAddFragment newInstance(UUID brandId) {
        AdminBrandAddFragment fragment = new AdminBrandAddFragment();
        Bundle args = new Bundle();
        args.putString(ARG_BRAND_ID, brandId.toString());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_brand_detail, container, false);

        nameEditText = view.findViewById(R.id.shoe_brand_name);
        descriptionEditText = view.findViewById(R.id.shoe_brand_description); // ✅ SỬA LẠI ID CHO KHỚP
        saveButton = view.findViewById(R.id.save_button);
        Button deleteButton = view.findViewById(R.id.delete_button);
        deleteButton.setVisibility(View.GONE); // ẩn nút xóa khi thêm mới

        brandController = new BrandController(getContext());

        saveButton.setOnClickListener(v -> addShoeBrand());

        return view;
    }

    private void addShoeBrand() {
        String name = nameEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();

        if (name.isEmpty()) {
            nameEditText.setError("Please enter shoe brand name");
            return;
        }

        try {
            Brand newBrand = new Brand();
            newBrand.setBrandId(UUID.fromString(getArguments().getString(ARG_BRAND_ID)));
            newBrand.setName(name);
            newBrand.setDescription(description);

            brandController.addBrand(newBrand);
            Toast.makeText(getContext(), "Shoe brand added successfully", Toast.LENGTH_SHORT).show();
            getParentFragmentManager().popBackStack();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
