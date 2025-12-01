package com.example.se07203_b5;

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

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Button btnCreate, btnLogout, btnBack, btnMonthlyPurchases;
    ListView lvListItem;
    int count = 0;
    TextView tvListTitle, tvReport;
    ArrayAdapter<Item> adapter;

    DatabaseHelper dbHelper;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        sharedPreferences = getSharedPreferences("AppData", MODE_PRIVATE);

        // Nếu CHƯA đăng nhập thì chuyển sang LoginActivity và đóng MainActivity
        if (!sharedPreferences.getBoolean("isLogin", false)) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // ĐÃ đăng nhập -> load giao diện chính
        setContentView(R.layout.activity_main);

        // Ánh xạ view
        btnCreate = findViewById(R.id.btnCreate);
        btnLogout = findViewById(R.id.btnLogout);
        btnMonthlyPurchases = findViewById(R.id.btnMonthlyPurchases); // nút chuyển sang màn tháng
        lvListItem = findViewById(R.id.lvItem);
        tvListTitle = findViewById(R.id.tvListTitle);
        tvReport = findViewById(R.id.tvReport);

        dbHelper = new DatabaseHelper(this);

        long userId = sharedPreferences.getLong("user_id", 0);

        ArrayList<Item> _items = dbHelper.getProducts(userId);
        AppData.ListItem.clear();
        AppData.ListItem = _items;

        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                AppData.ListItem
        );
        lvListItem.setAdapter(adapter);
        showReport();
        adapter.notifyDataSetChanged();

        // Nút tạo item mới
        btnCreate.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CreateNewTaskActivity.class);
            startActivity(intent);
        });

        // Nút MUA THEO THÁNG -> mở activity_monthly_purchases.xml
        btnMonthlyPurchases.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MonthlyPurchases.class);
            startActivity(intent);
        });

        // Click vào item trong list
        lvListItem.setOnItemClickListener((parent, view, position, id) -> {
            Toast.makeText(
                    this,
                    "Bạn chọn item thứ " + (position + 1) + ", món đồ " + AppData.ListItem.get(position),
                    Toast.LENGTH_LONG
            ).show();
            showOptionsDialog(position);
        });

        // Nút logout
        btnLogout.setOnClickListener(v -> {
            SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
            sharedPreferencesEditor.putBoolean("isLogin", false);
            sharedPreferencesEditor.apply();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void showOptionsDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Lựa chọn hành động!");
        String[] options = {"Sửa", "Xóa"};
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) { // Sửa
                Intent intent = new Intent(MainActivity.this, CreateNewTaskActivity.class);
                intent.putExtra("position", position);
                startActivityForResult(intent, AppData.EDIT_TASK);
            } else { // Xóa
                Item _item = AppData.ListItem.get(position);
                long itemId = _item.getId();
                boolean result = dbHelper.removeProductById(itemId);
                if (result) {
                    AppData.ListItem.remove(position);
                    showReport();
                    adapter.notifyDataSetChanged();
                    Toast.makeText(this, "Xóa thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Xóa thất bại", Toast.LENGTH_SHORT).show();
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppData.EDIT_TASK && resultCode == RESULT_OK) {
            showReport();
            adapter.notifyDataSetChanged();
        }
    }

    private void showReport() {
        tvReport.setText("Số đồ cần mua: " + AppData.ListItem.size()
                + " - Tổng tiền: " + AppData.getTotalBill());
    }
}
