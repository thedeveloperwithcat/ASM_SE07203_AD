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

import java.util.ArrayList;

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

        sharedPreferences = getSharedPreferences("AppData", MODE_PRIVATE);
        if (!sharedPreferences.getBoolean("isLogin", false)) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        super.onCreate(savedInstanceState);
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
    }

    private void loadData() {
        long userId = sharedPreferences.getLong("user_id", 0);

        ArrayList<Budget> list = dbHelper.getBudgets(userId);

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
                        AppData.EDIT_TASK
                )
        );

        lvItemBudget.setOnItemClickListener((parent, view, position, id) -> showOptionsDialog(position));
    }

    private void showOptionsDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose action");

        String[] options = {"Delete"};

        builder.setItems(options, (dialog, which) -> {

            Budget b = AppData.ListItemBudget.get(position);

            boolean result = dbHelper.removeBudgettById(b.getId());

            if (result) {
                AppData.ListItemBudget.remove(position);
                adapter.notifyDataSetChanged();
                Toast.makeText(this, "Deleted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Delete failed!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AppData.EDIT_TASK && resultCode == RESULT_OK) {
            loadData();
        }
    }

    private void setupBottomNav() {
        bottomNavigationView.setSelectedItemId(R.id.nav_budget);
        BottomNavHelper.setup(this, bottomNavigationView);
    }
}
