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

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Calendar;

public class MonthlyPurchasesActivity extends AppCompatActivity {

    TextView tvMonthlyReportTitle, tvTotalExpense;
    ListView lvMonthlyPurchases;
    Button btnBackToMainFromReport, btnLoadReport;
    EditText edtSelectMonth, edtSelectYear;
    DatabaseHelper dbHelper;
    SharedPreferences sharedPreferences;
    BottomNavigationView bottomNavigationView;  // 🔥 ĐÃ THÊM

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_monthly_purchases);

        // ÁNH XẠ VIEW
        tvMonthlyReportTitle = findViewById(R.id.tvMonthlyReportTitle);
        tvTotalExpense = findViewById(R.id.tvTotalExpense);
        lvMonthlyPurchases = findViewById(R.id.lvMonthlyPurchases);
        btnBackToMainFromReport = findViewById(R.id.btnBackToMainFromReport);
        btnLoadReport = findViewById(R.id.btnLoadReport);
        edtSelectMonth = findViewById(R.id.edtSelectMonth);
        edtSelectYear = findViewById(R.id.edtSelectYear);
        bottomNavigationView = findViewById(R.id.bottom_navigation);  // 🔥 ĐÃ ÁNH XẠ

        dbHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("AppData", MODE_PRIVATE);

        // Lấy tháng/năm hiện tại
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int currentYear = calendar.get(Calendar.YEAR);
        long userId = sharedPreferences.getLong("user_id", -1);

        if (userId == -1) {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            return;
        }

        // Gán tháng/năm mặc định vào input
        edtSelectMonth.setText(String.valueOf(currentMonth));
        edtSelectYear.setText(String.valueOf(currentYear));

        // Tải báo cáo mặc định
        loadReport(userId, currentMonth, currentYear);

        // NÚT BACK VỀ HOME
        btnBackToMainFromReport.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // BOTTOM NAVIGATION
        bottomNavigationView.setSelectedItemId(R.id.nav_report);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.nav_report) {
                return true;
            }
            return false;
        });

        // NÚT "XEM" – Load báo cáo theo tháng/năm
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

        ArrayAdapter<MonthlyPurchase> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, monthlyData);

        lvMonthlyPurchases.setAdapter(adapter);
    }
}
