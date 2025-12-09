package com.example.se07203_b5.Activitys;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.se07203_b5.Activitys.CreateExpenseActivity;
import com.example.se07203_b5.Database.DatabaseHelper;
import com.example.se07203_b5.Models.Expense;
import com.example.se07203_b5.R;
import com.example.se07203_b5.Utils.AppData;
import com.example.se07203_b5.Utils.BottomNavHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class ExpenseActivity extends AppCompatActivity {

    // UI components
    private Button btnCreate;
    private ListView lvListItem;
    private BottomNavigationView bottomNavigationView;
    private TextView tvEmpty;

    // Data & helpers
    private ArrayAdapter<Expense> adapter;
    private DatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;


    // ============================
    //           onCreate
    // ============================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Kiểm tra login
        sharedPreferences = getSharedPreferences("AppData", MODE_PRIVATE);
        if (!sharedPreferences.getBoolean("isLogin", false)) {
            Intent intent = new Intent(ExpenseActivity.this, CreateExpenseActivity.class);
            startActivityForResult(intent, AppData.CREATE_TASK);
            finish();
            return;
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_expense);

        initViews();        // Ánh xạ view
        initDatabase();     // Khởi tạo DB
        loadData();         // Load dữ liệu ListView
        setupEvents();      // Bắt sự kiện
        setupBottomNav();   // Setup bottom navigation
    }


    // ============================
    //      Init view components
    // ============================
    private void initViews() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        btnCreate = findViewById(R.id.btnCreate);
        lvListItem = findViewById(R.id.lvItem);
        tvEmpty = findViewById(R.id.tvEmpty);
        lvListItem.setEmptyView(tvEmpty);
    }


    // ============================
    //      Init database
    // ============================
    private void initDatabase() {
        dbHelper = new DatabaseHelper(this);

        long userId = sharedPreferences.getLong("user_id", 0);

        // Khi mở app lần sau (đã login trước đó), load lại data ngay lập tức
        if (userId > 0) {
            AppData.ListItemExpense = dbHelper.getExpenseByUserId(userId);
            AppData.ListItemBudget = dbHelper.getBudgetByUserId(userId);
        }
    }


    // ============================
    //      Load data vào ListView
    // ============================
    private void loadData() {
        long userId = sharedPreferences.getLong("user_id", 0);

        ArrayList<Expense> items = dbHelper.getExpenseByUserId(userId);
        AppData.ListItemExpense.clear();
        AppData.ListItemExpense = items;

        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                AppData.ListItemExpense);

        lvListItem.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }


    // ============================
    //     Setup event listeners
    // ============================
    private void setupEvents() {

        // Create new item
        btnCreate.setOnClickListener(v ->
                startActivityForResult(
                        new Intent(ExpenseActivity.this, CreateExpenseActivity.class),
                        AppData.CREATE_TASK
                )
        );

        // Click on a list item
        lvListItem.setOnItemClickListener((parent, view, position, id) -> {
            showOptionsDialog(position);
        });
    }


    // ============================
    //      Dialog Update/Delete
    // ============================
    private void showOptionsDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose action");

        String[] options = {"Detail Info", "Update", "Delete"};

        builder.setItems(options, (dialog, which) -> {

            if (which == 0) {          // 📌 Detail Info
                showDetailDialog(position);

            } else if (which == 1) {   // ✏ Update
                Intent intent = new Intent(ExpenseActivity.this, CreateExpenseActivity.class);
                intent.putExtra("position", position);
                startActivityForResult(intent, AppData.EDIT_TASK);

            } else {  // 🗑 Delete
                Expense item = AppData.ListItemExpense.get(position);
                long itemId = item.getId();

                boolean result = dbHelper.removeExpenseById(itemId);
                if (result) {
                    AppData.ListItemExpense.remove(position);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(this, "Deleted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Delete failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.create().show();
    }


    private void showDetailDialog(int position) {
        Expense item = AppData.ListItemExpense.get(position);

        String message =
                "\nExpense name: " + item.getName() +
                        "\nQuantity: " + item.getQuantity() +
                        "\nPrice: " + item.getUnitPrice() + " vnd" +
                        "\nDate: " + new java.text.SimpleDateFormat("dd/MM/yyyy")
                        .format(new java.util.Date(item.getTimestamp()));

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Expense Details");
        dialog.setMessage(message);
        dialog.setPositiveButton("OK", null);
        dialog.show();
    }


    // ============================
    //        Receive update
    // ============================
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == AppData.CREATE_TASK) {

                loadData();                 // 🔥 load lại dữ liệu từ DB
                adapter.notifyDataSetChanged();
            }

            if (requestCode == AppData.EDIT_TASK) {

                loadData();                 // 🔥 load lại dữ liệu từ DB
                adapter.notifyDataSetChanged();
            }
        }
    }


    // ============================
    //     Bottom Navigation
    // ============================
    private void setupBottomNav() {
        bottomNavigationView.setSelectedItemId(R.id.nav_expense);
        BottomNavHelper.setup(this, bottomNavigationView);
    }
}
