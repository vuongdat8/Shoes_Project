package com.example.shoes_project.ui.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.content.Context;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.shoes_project.data.db.entity.Brand;

import java.util.List;

public class BrandAdapter extends ArrayAdapter<Brand> {

    public BrandAdapter(Context context, List<Brand> brands) {
        super(context, 0, brands);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createView(position, convertView, parent);
    }
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createView(position, convertView, parent);
    }
    private View createView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_spinner_item, parent, false);
        }
        TextView textView = convertView.findViewById(android.R.id.text1);
        Brand brand = getItem(position);
        if (brand != null) {
            textView.setText(brand.getName());
        }
        return convertView;
    }
}