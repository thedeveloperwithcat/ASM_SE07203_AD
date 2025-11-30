package com.example.se07203_b5;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class MonthlyPurchasesActivity extends AppCompatActivity {

    TextView tvMonthlyReportTitle, tvTotalExpense;
    ListView lvMonthlyPurchases;
    DatabaseHelper dbHelper;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_monthly_purchases);

        // Khai báo
        tvMonthlyReportTitle = findViewById(R.id.tvMonthlyReportTitle);
        tvTotalExpense = findViewById(R.id.tvTotalExpense);
        lvMonthlyPurchases = findViewById(R.id.lvMonthlyPurchases);
        dbHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("AppData", MODE_PRIVATE);

        // Lấy thông tin tháng/năm hiện tại (để làm báo cáo mặc định)
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int currentYear = calendar.get(Calendar.YEAR);
        long userId = sharedPreferences.getLong("user_id", -1);

        if (userId == -1) {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy dữ liệu báo cáo
        ArrayList<MonthlyPurchase> monthlyData = dbHelper.getMonthlyPurchases(userId, currentMonth, currentYear);

        // Hiển thị dữ liệu
        showReport(currentMonth, currentYear, monthlyData);
    }

    private void showReport(int month, int year, ArrayList<MonthlyPurchase> monthlyData) {
        tvMonthlyReportTitle.setText("TỔNG QUAN CHI TIÊU THÁNG " + month + "/" + year);

        long totalBill = 0;
        for (MonthlyPurchase item : monthlyData) {
            totalBill += item.getTotalPrice();
        }

        tvTotalExpense.setText("Tổng chi tiêu tháng " + month + "/" + year + ": " + totalBill + " VNĐ");

        // Sử dụng ArrayAdapter để hiển thị danh sách giao dịch
        ArrayAdapter<MonthlyPurchase> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, monthlyData);
        lvMonthlyPurchases.setAdapter(adapter);
    }
}
