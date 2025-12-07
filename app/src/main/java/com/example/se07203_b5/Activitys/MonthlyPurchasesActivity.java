package com.example.se07203_b5.Activitys;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.se07203_b5.Database.DatabaseHelper;
import com.example.se07203_b5.Models.Expense;
import com.example.se07203_b5.R;
import com.example.se07203_b5.Utils.BottomNavHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Calendar;

public class MonthlyPurchasesActivity extends AppCompatActivity {

    // UI Components
    private TextView tvTotalExpense;
    private ListView lvMonthlyPurchases;
    private Button btnLoadReport;
    private EditText edtSelectMonth, edtSelectYear;
    private BottomNavigationView bottomNavigationView;

    // Data
    private DatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;


    // ============================================
    //                  onCreate
    // ============================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_monthly_purchases);

        initViews();
        initDatabase();
        setupBottomNav();

        long userId = loadUserId();
        if (userId == -1) return;

        setDefaultMonthYear();

        int currentMonth = getCurrentMonth();
        int currentYear = getCurrentYear();

        loadReport(userId, currentMonth, currentYear);

        setupButtonEvent(userId);
    }


    // ============================================
    //                INIT FUNCTIONS
    // ============================================
    private void initViews() {
        lvMonthlyPurchases = findViewById(R.id.lvMonthlyPurchases);
        btnLoadReport = findViewById(R.id.btnLoadReport);
        edtSelectMonth = findViewById(R.id.edtSelectMonth);
        edtSelectYear = findViewById(R.id.edtSelectYear);
        tvTotalExpense = findViewById(R.id.tvTotalExpense);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
    }

    private void initDatabase() {
        dbHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("AppData", MODE_PRIVATE);
    }


    private long loadUserId() {
        long userId = sharedPreferences.getLong("user_id", -1);
        if (userId == -1) {
            Toast.makeText(this, "User information not found", Toast.LENGTH_SHORT).show();
        }
        return userId;
    }


    // ============================================
    //                 DEFAULT VALUES
    // ============================================
    private void setDefaultMonthYear() {
        edtSelectMonth.setText(String.valueOf(getCurrentMonth()));
        edtSelectYear.setText(String.valueOf(getCurrentYear()));
    }

    private int getCurrentMonth() {
        return Calendar.getInstance().get(Calendar.MONTH) + 1;
    }

    private int getCurrentYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }


    // ============================================
    //         BUTTON EVENT (LOAD REPORT)
    // ============================================
    private void setupButtonEvent(long userId) {
        btnLoadReport.setOnClickListener(v -> {
            String monthStr = edtSelectMonth.getText().toString().trim();
            String yearStr = edtSelectYear.getText().toString().trim();

            if (!validateInput(monthStr, yearStr)) return;

            try {
                int selectedMonth = Integer.parseInt(monthStr);
                int selectedYear = Integer.parseInt(yearStr);

                if (selectedMonth < 1 || selectedMonth > 12) {
                    showToast("Month must be from 1 to 12");
                    return;
                }

                loadReport(userId, selectedMonth, selectedYear);

            } catch (NumberFormatException e) {
                showToast("Month and Year must be valid numbers");
            }
        });
    }


    // ============================================
    //              VALIDATE INPUT
    // ============================================
    private boolean validateInput(String monthStr, String yearStr) {
        if (TextUtils.isEmpty(monthStr) || TextUtils.isEmpty(yearStr)) {
            showToast("Please enter full Month and Year");
            return false;
        }
        return true;
    }


    // ============================================
    //                LOAD REPORT
    // ============================================
    private void loadReport(long userId, int month, int year) {
        ArrayList<Expense> expenses = dbHelper.getExpensesByMonth(userId, month, year);
        showReport(month, year, expenses);
    }


    // ============================================
    //               SHOW REPORT
    // ============================================
    private void showReport(int month, int year, ArrayList<Expense> list) {
        long totalBill = 0;
        for (Expense e : list) {
            totalBill += (long) e.getUnitPrice() * e.getQuantity();
        }

        tvTotalExpense.setText(
                "Total monthly expenses " + month + "/" + year + ": " + totalBill + " VND"
        );

        ArrayAdapter<Expense> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                list
        );

        lvMonthlyPurchases.setAdapter(adapter);
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    // ============================
    //     Bottom Navigation
    // ============================
    private void setupBottomNav() {
        bottomNavigationView.setSelectedItemId(R.id.nav_report);
        BottomNavHelper.setup(this, bottomNavigationView);
    }
}
