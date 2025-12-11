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
import com.example.se07203_b5.Models.Budget;
import com.example.se07203_b5.R;
import com.example.se07203_b5.Utils.AppData;
import com.example.se07203_b5.Utils.BottomNavHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class BudgetActivity extends AppCompatActivity {

    private Button btnCreateBudget;
    private ListView lvItemBudget;
    private TextView tvEmptyBudget;
    private BottomNavigationView bottomNavigationView;

    private ArrayAdapter<Budget> adapter;
    private DatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences("AppData", MODE_PRIVATE);
        if (!sharedPreferences.getBoolean("isLogin", false)) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_budget);

        initViews();
        initDatabase();
        loadData();
        setupEvents();
        setupBottomNav();
    }

    private void initViews() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        btnCreateBudget = findViewById(R.id.btnCreateBudget);
        lvItemBudget = findViewById(R.id.lvItemBudget);
        tvEmptyBudget = findViewById(R.id.tvEmptyBudget);
        lvItemBudget.setEmptyView(tvEmptyBudget);
    }

    private void initDatabase() {
        dbHelper = new DatabaseHelper(this);

        long userId = sharedPreferences.getLong("user_id", 0);
        if (userId > 0) {
//            AppData.ListItemExpense = dbHelper.getExpenseByUserId(userId);
            AppData.ListItemBudget = dbHelper.getBudgetByUserId(userId);
        }
    }

    private void loadData() {
        long userId = sharedPreferences.getLong("user_id", 0);
        ArrayList<Budget> list = dbHelper.getAllBudgets(userId);


        AppData.ListItemBudget.clear();
        AppData.ListItemBudget = list;

        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                AppData.ListItemBudget);

        lvItemBudget.setAdapter(adapter);
    }

    private void setupEvents() {

        btnCreateBudget.setOnClickListener(v ->
                startActivityForResult(
                        new Intent(this, CreateBudgetActivity.class),
                        AppData.CREATE_TASK
                )
        );

        lvItemBudget.setOnItemClickListener((parent, view, position, id) -> showOptionsDialog(position));
    }

    private void showOptionsDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose action");

        String[] options = {"Detail Info", "Delete"};

        builder.setItems(options, (dialog, which) -> {

            Budget b = AppData.ListItemBudget.get(position);

            if (which == 0) {  //  Detail Info
                showDetailDialog(position);
            } else { // 🗑 Delete
                boolean result = dbHelper.removeBudgettById(b.getId());

                if (result) {
                    AppData.ListItemBudget.remove(position);
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
        Budget b = AppData.ListItemBudget.get(position);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        String message =
                "\nBudget name: " + b.getName() +
                "\nPrice: " + b.getPrice() + " vnd" +
                "\nDate Start: " + dateFormat.format(new Date(b.getStartTimestamp())) +
                "\nDate End: " + dateFormat.format(new Date(b.getEndTimestamp()));

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Budget Details");
        dialog.setMessage(message);
        dialog.setPositiveButton("OK", null);
        dialog.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AppData.CREATE_TASK  && resultCode == RESULT_OK) {
            loadData();
        }
    }

    private void setupBottomNav() {
        bottomNavigationView.setSelectedItemId(R.id.nav_budget);
        BottomNavHelper.setup(this, bottomNavigationView);
    }
}
