package com.example.shoes_project.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoes_project.R;
import com.example.shoes_project.adapter.CheckoutAdapter;
import com.example.shoes_project.data.AppDatabase;
import com.example.shoes_project.model.Order;
import com.example.shoes_project.model.OrderItem;
import com.example.shoes_project.model.Product;
import com.example.shoes_project.data.OrderRepository;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CheckoutActivity extends AppCompatActivity {
    private OrderRepository orderRepository;
    private AppDatabase database;
    private RecyclerView rvCheckoutItems;
    private EditText etCustomerName, etCustomerPhone, etCustomerEmail, etCustomerAddress, etNotes;
    private RadioGroup rgPaymentMethod;
    private RadioButton rbCash, rbCard, rbBanking;
    private TextView tvSubtotal, tvShippingFee, tvTotalAmount;
    private Button btnPlaceOrder;
    private ImageButton btnBack;
    private CheckoutAdapter adapter;
    private List<Product> selectedProducts;
    private List<Integer> quantities;
    private List<String> selectedColors;
    private List<Double> selectedSizes;
    private double subtotal = 0;
    private double shippingFee = 30000;
    private double totalAmount = 0;
    private static final int REQUEST_MAP_PICKER = 1001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        orderRepository = new OrderRepository(this);
        database = AppDatabase.getInstance(this);

        initViews();
        loadData();
        calculateTotals();
        setupListeners();
    }
    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnPlaceOrder.setOnClickListener(v -> {
            if (validateInputs()) {
                placeOrder();
            }
        });

        rgPaymentMethod.setOnCheckedChangeListener((group, checkedId) -> updatePaymentMethod(checkedId));

        findViewById(R.id.btn_select_on_map).setOnClickListener(v -> {
            Intent intent = new Intent(this, MapPickerActivity.class);
            startActivityForResult(intent, REQUEST_MAP_PICKER);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_MAP_PICKER && resultCode == RESULT_OK && data != null) {
            double lat = data.getDoubleExtra("latitude", 0);
            double lng = data.getDoubleExtra("longitude", 0);

            // Gọi geocoder để chuyển thành địa chỉ (cần permission INTERNET)
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
                if (!addresses.isEmpty()) {
                    String address = addresses.get(0).getAddressLine(0);
                    etCustomerAddress.setText(address);
                }
            } catch (IOException e) {
                e.printStackTrace();
                etCustomerAddress.setText(lat + ", " + lng); // fallback
            }
        }
    }
    private void initViews() {
        rvCheckoutItems = findViewById(R.id.recycler_view_checkout);
        etCustomerName = findViewById(R.id.et_customer_name);
        etCustomerPhone = findViewById(R.id.et_customer_phone);
        etCustomerEmail = findViewById(R.id.et_customer_email);
        etCustomerAddress = findViewById(R.id.et_customer_address);
        etNotes = findViewById(R.id.et_notes);
        rgPaymentMethod = findViewById(R.id.rg_payment_method);
        rbCash = findViewById(R.id.rb_cash);
        rbCard = findViewById(R.id.rb_card);
        rbBanking = findViewById(R.id.rb_banking);
        tvSubtotal = findViewById(R.id.tv_subtotal);
        tvShippingFee = findViewById(R.id.tv_shipping_fee);
        tvTotalAmount = findViewById(R.id.tv_total_amount);
        btnPlaceOrder = findViewById(R.id.btn_place_order);
        btnBack = findViewById(R.id.btn_back);

        rvCheckoutItems.setLayoutManager(new LinearLayoutManager(this));
        rvCheckoutItems.setNestedScrollingEnabled(false);
        rvCheckoutItems.setItemAnimator(null); // Disable animations to avoid SurfaceFlinger issues
    }

    private void loadData() {
        selectedProducts = new ArrayList<>();
        quantities = new ArrayList<>();
        selectedColors = new ArrayList<>();
        selectedSizes = new ArrayList<>();

        Intent intent = getIntent();
        if (intent.hasExtra("product_ids")) {
            try {
                ArrayList<Integer> productIds = intent.getIntegerArrayListExtra("product_ids");
                ArrayList<String> productNames = intent.getStringArrayListExtra("product_names");
                ArrayList<String> imageUrls = intent.getStringArrayListExtra("product_image_urls");
                ArrayList<Double> prices = (ArrayList<Double>) intent.getSerializableExtra("product_prices");
                ArrayList<Integer> receivedQuantities = intent.getIntegerArrayListExtra("quantities");
                ArrayList<String> colors = intent.getStringArrayListExtra("selected_colors");
                ArrayList<Double> sizes = (ArrayList<Double>) intent.getSerializableExtra("selected_sizes");

                if (productIds != null && productNames != null && imageUrls != null &&
                        prices != null && receivedQuantities != null && colors != null && sizes != null &&
                        productIds.size() == productNames.size() &&
                        productIds.size() == receivedQuantities.size()) {

                    Map<String, Integer> quantityMap = new HashMap<>();
                    Map<String, Product> productMap = new HashMap<>();

                    for (int i = 0; i < productIds.size(); i++) {
                        int id = productIds.get(i);
                        String name = productNames.get(i);
                        String imageUrl = imageUrls.get(i);
                        double price = prices.get(i);
                        int qty = receivedQuantities.get(i);
                        String color = colors.get(i);
                        double size = sizes.get(i);

                        String key = id + "_" + color + "_" + size;

                        if (quantityMap.containsKey(key)) {
                            int newQty = quantityMap.get(key) + qty;
                            quantityMap.put(key, newQty);
                        } else {
                            Product product = new Product();
                            product.setId(id);
                            product.setProductName(name);
                            product.setImageUrl(imageUrl);
                            product.setPrice(price);
                            product.setColor(color);
                            product.setSize(size);

                            productMap.put(key, product);
                            quantityMap.put(key, qty);
                        }
                    }

                    for (String key : productMap.keySet()) {
                        Product product = productMap.get(key);
                        int qty = quantityMap.get(key);
                        selectedProducts.add(product);
                        quantities.add(qty);
                        selectedColors.add(product.getColor());
                        selectedSizes.add(product.getSize());
                    }

                    setupAdapter();
                } else {
                    Log.e("CheckoutActivity", "Invalid or mismatched data from CartActivity");
                    loadSampleData();
                }
            } catch (Exception e) {
                Log.e("CheckoutActivity", "Error loading data from Intent", e);
                loadSampleData();
            }
        } else {
            Log.d("CheckoutActivity", "No product data received from CartActivity");
            loadSampleData();
        }
    }



    private void loadSampleData() {
        // Sample data for testing
        Product product1 = new Product();
        product1.setId(1);
        product1.setProductName("Nike Air Max 270");
        product1.setPrice(2500000);
        product1.setImageUrl("");
        product1.setColor("Black");
        product1.setSize(42.0);

        Product product2 = new Product();
        product2.setId(2);
        product2.setProductName("Adidas Ultra Boost 22");
        product2.setPrice(1800000);
        product2.setImageUrl("");
        product2.setColor("White");
        product2.setSize(41.0);

        selectedProducts.add(product1);
        selectedProducts.add(product2);
        quantities.add(2);
        quantities.add(1);
        selectedColors.add("Black");
        selectedColors.add("White");
        selectedSizes.add(42.0);
        selectedSizes.add(41.0);

        // Set up adapter after loading sample data
        setupAdapter();
    }

    private void setupAdapter() {
        adapter = new CheckoutAdapter(this, selectedProducts, quantities, selectedColors, selectedSizes);
        rvCheckoutItems.setAdapter(adapter);
        Log.d("CheckoutActivity", "Adapter set with " + selectedProducts.size() + " items");
    }

    private void calculateTotals() {
        subtotal = 0;
        for (int i = 0; i < selectedProducts.size(); i++) {
            subtotal += selectedProducts.get(i).getPrice() * quantities.get(i);
        }
        totalAmount = subtotal + shippingFee;

        tvSubtotal.setText(formatCurrency(subtotal));
        tvShippingFee.setText(formatCurrency(shippingFee));
        tvTotalAmount.setText(formatCurrency(totalAmount));
    }



    private void updatePaymentMethod(int checkedId) {
        if (checkedId == R.id.rb_cash) {
            shippingFee = 30000; // COD fee
        } else if (checkedId == R.id.rb_card) {
            shippingFee = 20000; // Card payment fee
        } else if (checkedId == R.id.rb_banking) {
            shippingFee = 15000; // Bank transfer fee
        }
        calculateTotals();
    }

    private boolean validateInputs() {
        String name = etCustomerName.getText().toString().trim();
        String phone = etCustomerPhone.getText().toString().trim();
        String address = etCustomerAddress.getText().toString().trim();

        if (name.isEmpty()) {
            etCustomerName.setError("Vui lòng nhập họ tên");
            etCustomerName.requestFocus();
            return false;
        }

        if (phone.isEmpty()) {
            etCustomerPhone.setError("Vui lòng nhập số điện thoại");
            etCustomerPhone.requestFocus();
            return false;
        }

        if (phone.length() < 10) {
            etCustomerPhone.setError("Số điện thoại phải có ít nhất 10 số");
            etCustomerPhone.requestFocus();
            return false;
        }

        if (address.isEmpty()) {
            etCustomerAddress.setError("Vui lòng nhập địa chỉ giao hàng");
            etCustomerAddress.requestFocus();
            return false;
        }

        if (selectedProducts == null || selectedProducts.isEmpty()) {
            Toast.makeText(this, "Không có sản phẩm nào để đặt hàng!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void placeOrder() {
        if (!validateInputs()) return;

        Order order = new Order();
        order.setUserId(getCurrentUserId());
        order.setCustomerName(etCustomerName.getText().toString().trim());
        order.setCustomerPhone(etCustomerPhone.getText().toString().trim());
        order.setCustomerEmail(etCustomerEmail.getText().toString().trim());
        order.setCustomerAddress(etCustomerAddress.getText().toString().trim());
        order.setTotalAmount(totalAmount);
        order.setPaymentMethod(getSelectedPaymentMethod());
        order.setNotes(etNotes.getText().toString().trim());
        order.setOrderStatus("pending");
        order.setOrderDate(String.valueOf(System.currentTimeMillis()));

        List<OrderItem> orderItems = new ArrayList<>();
        for (int i = 0; i < selectedProducts.size(); i++) {
            Product product = selectedProducts.get(i);
            int quantity = quantities.get(i);
            String color = selectedColors.get(i);
            Double size = selectedSizes.get(i);

            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getProductName());
            orderItem.setProductImage(product.getImageUrl());
            orderItem.setProductPrice(product.getPrice());
            orderItem.setQuantity(quantity);
            orderItem.setSelectedColor(color);
            orderItem.setSelectedSize(String.valueOf(size));
            orderItem.setSubtotal(product.getPrice() * quantity);

            orderItems.add(orderItem);
        }

        orderRepository.createCompleteOrder(order, orderItems, new OrderRepository.OnCompleteOrderCreatedListener() {
            @Override
            public void onCompleteOrderCreated(boolean success, int orderId) {
                if (success) {
                    Toast.makeText(CheckoutActivity.this, "Đặt hàng thành công! Mã đơn hàng: " + orderId, Toast.LENGTH_LONG).show();
                    clearSelectedCartItems();
                    order.setOrderId(orderId); // Gán mã đơn hàng vào object Order
                    Intent intent = new Intent(CheckoutActivity.this, OrderConfirmationActivity.class);
                    intent.putExtra("order", order); // Gửi object Order qua màn OrderConfirmation
                    startActivity(intent);
                    finish();

                } else {
                    Toast.makeText(CheckoutActivity.this, "Đặt hàng thất bại. Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private int getCurrentUserId() {
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        return prefs.getInt("userId", -1);
    }

    private void clearSelectedCartItems() {
        int userId = getCurrentUserId();
        if (userId != -1) {
            new Thread(() -> {
                for (Product product : selectedProducts) {
                    database.cartADao().deleteSelectedCartAs(userId);
                }
            }).start();
        }
    }

    private String getSelectedPaymentMethod() {
        int selectedId = rgPaymentMethod.getCheckedRadioButtonId();
        if (selectedId == R.id.rb_cash) {
            return "Thanh toán khi nhận hàng (COD)";
        } else if (selectedId == R.id.rb_card) {
            return "Thanh toán bằng thẻ";
        } else if (selectedId == R.id.rb_banking) {
            return "Chuyển khoản ngân hàng";
        }
        return "Thanh toán khi nhận hàng (COD)";
    }

    private String formatCurrency(double amount) {
        return NumberFormat.getNumberInstance(Locale.getDefault()).format(amount) + "₫";
    }
}