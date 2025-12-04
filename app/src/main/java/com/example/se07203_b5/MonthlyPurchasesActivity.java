package com.example.se07203_b5;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class MonthlyPurchasesActivity extends AppCompatActivity {

    TextView tvMonthlyReportTitle, tvTotalExpense;
    ListView lvMonthlyPurchases;
    Button btnBackToMainFromReport, btnLoadReport; // KHAI BÁO NÚT XEM MỚI
    EditText edtSelectMonth, edtSelectYear; // KHAI BÁO INPUT MỚI
    DatabaseHelper dbHelper;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_monthly_purchases);

        // Khai báo và Ánh xạ
        tvMonthlyReportTitle = findViewById(R.id.tvMonthlyReportTitle);
        tvTotalExpense = findViewById(R.id.tvTotalExpense);
        lvMonthlyPurchases = findViewById(R.id.lvMonthlyPurchases);
        btnBackToMainFromReport = findViewById(R.id.btnBackToMainFromReport);

        // ÁNH XẠ CÁC THÀNH PHẦN MỚI
        btnLoadReport = findViewById(R.id.btnLoadReport);
        edtSelectMonth = findViewById(R.id.edtSelectMonth);
        edtSelectYear = findViewById(R.id.edtSelectYear);

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

        // Đặt giá trị mặc định cho input là tháng/năm hiện tại
        edtSelectMonth.setText(String.valueOf(currentMonth));
        edtSelectYear.setText(String.valueOf(currentYear));

        // Lấy và hiển thị báo cáo mặc định
        loadReport(userId, currentMonth, currentYear);

        // Xử lý sự kiện nút Back
        btnBackToMainFromReport.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });

        // XỬ LÝ SỰ KIỆN NÚT 'XEM' (LOAD REPORT)
        btnLoadReport.setOnClickListener(v -> {
            String monthStr = edtSelectMonth.getText().toString();
            String yearStr = edtSelectYear.getText().toString();

            if (TextUtils.isEmpty(monthStr) || TextUtils.isEmpty(yearStr)) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ Tháng và Năm", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int selectedMonth = Integer.parseInt(monthStr);
                int selectedYear = Integer.parseInt(yearStr);

                if (selectedMonth < 1 || selectedMonth > 12) {
                    Toast.makeText(this, "Tháng phải từ 1 đến 12", Toast.LENGTH_SHORT).show();
                    return;
                }

                loadReport(userId, selectedMonth, selectedYear);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Tháng và Năm phải là số hợp lệ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadReport(long userId, int month, int year) {
        ArrayList<MonthlyPurchase> monthlyData = dbHelper.getMonthlyPurchases(userId, month, year);
        showReport(month, year, monthlyData);
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