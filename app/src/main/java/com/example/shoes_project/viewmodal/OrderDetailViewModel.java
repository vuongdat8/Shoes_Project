package com.example.shoes_project.viewmodal;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.shoes_project.data.AppDatabase;
import com.example.shoes_project.data.dao.OrderDetailDao;
import com.example.shoes_project.model.OrderDetail;

import java.util.List;

public class OrderDetailViewModel extends AndroidViewModel {

    private OrderDetailDao orderDetailDao;
    private LiveData<List<OrderDetail>> orderDetails;

    public OrderDetailViewModel(Application application) {
        super(application);
        AppDatabase db = AppDatabase.getInstance(application);
        orderDetailDao = db.orderDetailDao();
    }

    public LiveData<List<OrderDetail>> getOrderDetails(int orderId) {
        return (LiveData<List<OrderDetail>>) orderDetailDao.getDetailsByOrderId(orderId);
    }
}
