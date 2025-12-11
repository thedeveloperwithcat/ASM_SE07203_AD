package com.example.se07203_b5.Activitys;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.se07203_b5.Adapters.RecurringAdapter;
import com.example.se07203_b5.Database.DatabaseHelper;
import com.example.se07203_b5.Models.RecurringExpense;
import com.example.se07203_b5.R;
import com.example.se07203_b5.Utils.BottomNavHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class RecurringListActivity extends AppCompatActivity {
    private RecyclerView rvRecurringList;
    private TextView tvTotalMonthly;
    private FloatingActionButton fabAdd;
    private BottomNavigationView bottomNavigationView;
    private RecurringAdapter adapter;
    private ArrayList<RecurringExpense> listData;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recurring_list);

        initViews();
        setupBottomNav();

        dbHelper = new DatabaseHelper(this);
        listData = new ArrayList<>();
        adapter = new RecurringAdapter(this, listData);
        rvRecurringList.setLayoutManager(new LinearLayoutManager(this));
        rvRecurringList.setAdapter(adapter);

        fabAdd.setOnClickListener(v -> {
            startActivity(new Intent(RecurringListActivity.this, CreateRecurringActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void initViews() {
        rvRecurringList = findViewById(R.id.rvRecurringList);
        tvTotalMonthly = findViewById(R.id.tvTotalMonthly);
        fabAdd = findViewById(R.id.fabAdd);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
    }

    private void setupBottomNav() {
        // Highlight nút Recurring khi đang ở màn hình này
        bottomNavigationView.setSelectedItemId(R.id.nav_recurring);
        BottomNavHelper.setup(this, bottomNavigationView);
    }

    private void loadData() {
        listData.clear();
        listData.addAll(dbHelper.getAllRecurringExpenses());
        adapter.notifyDataSetChanged();
        calculateTotal();
    }

    private void calculateTotal() {
        double total = 0;
        for (RecurringExpense item : listData) {
            if ("Monthly".equals(item.getFrequency())) {
                total += item.getAmount();
            }
        }
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        tvTotalMonthly.setText(currencyFormat.format(total));
    }
}