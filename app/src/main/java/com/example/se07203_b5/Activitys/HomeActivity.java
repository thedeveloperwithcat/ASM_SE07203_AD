package com.example.se07203_b5.Activitys;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.se07203_b5.Database.DatabaseHelper;
import com.example.se07203_b5.R;
import com.example.se07203_b5.Utils.BottomNavHelper;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private BarChart barChart;
    private PieChart pieChart;
    private Button btnLogout;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);  // CHECK: Layout này phải chứa BottomNavigationView

        initViews();
        setupEvents();
        setupBottomNav();
        loadBarChart();
        loadPieChart();

    }

    private void initViews() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        barChart = findViewById(R.id.barChart);
        pieChart = findViewById(R.id.pieChart);
        btnLogout = findViewById(R.id.btnLogout);
        sharedPreferences = getSharedPreferences("AppData", MODE_PRIVATE);
    }
    private void setupBottomNav() {
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
        BottomNavHelper.setup(this, bottomNavigationView);
    }

    private void loadBarChart() {
        DatabaseHelper db = new DatabaseHelper(this);

        long userId = 1; // lấy từ SharedPref nếu có

        Calendar cal = Calendar.getInstance();
        int currentMonth = cal.get(Calendar.MONTH) + 1;
        int currentYear = cal.get(Calendar.YEAR);

        // Tháng trước
        cal.add(Calendar.MONTH, -1);
        int lastMonth = cal.get(Calendar.MONTH) + 1;
        int lastYear = cal.get(Calendar.YEAR);

        int totalLastMonth = db.getTotalExpenseOfMonth(userId, lastMonth, lastYear);
        int totalCurrentMonth = db.getTotalExpenseOfMonth(userId, currentMonth, currentYear);

        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, totalLastMonth));
        entries.add(new BarEntry(1, totalCurrentMonth));

        BarDataSet dataSet = new BarDataSet(entries, "Total Expenses (VND)");
        dataSet.setValueTextSize(14f);

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.4f);

        barChart.setData(data);

        // X-labels
        ArrayList<String> labels = new ArrayList<>();
        labels.add("Last Month");
        labels.add("This Month");

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        barChart.getDescription().setEnabled(false);
        barChart.animateY(1000);
        barChart.invalidate();
    }

    private void loadPieChart() {
        DatabaseHelper db = new DatabaseHelper(this);

        long userId = 1; // sau này lấy từ SharedPref

        Calendar cal = Calendar.getInstance();
        int currentMonth = cal.get(Calendar.MONTH) + 1;
        int currentYear = cal.get(Calendar.YEAR);

        // Lấy tổng budget tháng hiện tại
        int totalBudget = db.getTotalBudgetOfMonth(userId, currentMonth, currentYear);

        // Lấy tổng chi tiêu tháng hiện tại
        int totalExpense = db.getTotalExpenseOfMonth(userId, currentMonth, currentYear);

        // Nếu budget = 0 thì tránh lỗi chia 0
        if (totalBudget <= 0 && totalExpense <= 0) {
            totalBudget = 1;
        }

        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(totalExpense, "Expense"));
        entries.add(new PieEntry(totalBudget, "Budget"));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setSliceSpace(2f);
        dataSet.setValueTextSize(14f);

        // Màu: Expense = đỏ, Budget = xanh lá
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.RED);
        colors.add(Color.GREEN);
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);

        // Tắt mô tả
        pieChart.getDescription().setEnabled(false);

        // Hiệu ứng
        pieChart.animateY(1000);

        // Hiển thị %
        pieChart.setUsePercentValues(true);

        // Căn chỉnh label trong chart
        pieChart.setDrawEntryLabels(true);
        pieChart.setEntryLabelTextSize(12f);

        pieChart.invalidate();
    }

    private void setupEvents() {
        btnLogout.setOnClickListener(v -> {
            SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
            sharedPreferencesEditor.putBoolean("isLogin", false);
            sharedPreferencesEditor.apply();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(this, "Logout successfully!", Toast.LENGTH_SHORT).show();
        });
    }

}