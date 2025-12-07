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

        // Kiểm tra login
        sharedPreferences = getSharedPreferences("AppData", MODE_PRIVATE);
        if (!sharedPreferences.getBoolean("isLogin", false)) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        super.onCreate(savedInstanceState);
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
                startActivity(new Intent(ExpenseActivity.this, CreateExpenseActivity.class))
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

        String[] options = {"Update", "Delete"};

        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {             // Update
                Intent intent = new Intent(ExpenseActivity.this, CreateExpenseActivity.class);
                intent.putExtra("position", position);
                startActivityForResult(intent, AppData.EDIT_TASK);

            } else {                      // Delete
                Expense item = AppData.ListItemExpense.get(position);
                long itemId = item.getId();

                boolean result = dbHelper.removeProductById(itemId);
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


    // ============================
    //        Receive update
    // ============================
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AppData.EDIT_TASK && resultCode == RESULT_OK) {
            adapter.notifyDataSetChanged();
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
