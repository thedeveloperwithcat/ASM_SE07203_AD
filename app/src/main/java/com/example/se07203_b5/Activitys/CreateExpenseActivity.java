package com.example.se07203_b5.Activitys;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.se07203_b5.Database.DatabaseHelper;
import com.example.se07203_b5.Models.Expense;
import com.example.se07203_b5.R;
import com.example.se07203_b5.Utils.AppData;

import java.util.Calendar;
import java.util.Collections;

public class CreateExpenseActivity extends AppCompatActivity {
    private Button btnSubmitCreate, btnBackToList;
    private TextView titlePageCreateEdit;
    private EditText edtItemName, edtQuantity, edtUnitPrice, edtCalendar;
    private boolean isEditMode = false;
    private int position = -1;
    private SharedPreferences sharedPreferences;
    private int selectedDay = -1, selectedMonth = -1, selectedYear = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_expense);

        initViews();
        initSharedPref();
        checkEditMode();
        setupEvents();
    }

    private void initViews() {
        btnSubmitCreate = findViewById(R.id.btnSubmitCreate);
        btnBackToList = findViewById(R.id.btnBackToList);
        edtItemName = findViewById(R.id.edtItemName);
        edtQuantity = findViewById(R.id.edtQuantity);
        edtUnitPrice = findViewById(R.id.edtUnitPrice);
        edtCalendar = findViewById(R.id.edtCalender);
        titlePageCreateEdit = findViewById(R.id.titlePageCreateEdit);
    }

    private void initSharedPref() {
        sharedPreferences = getSharedPreferences("AppData", MODE_PRIVATE);
    }

    private void checkEditMode() {
        Intent intent = getIntent();
        if (intent == null || intent.getExtras() == null) {
            isEditMode = false;
            return;
        }

        position = intent.getIntExtra("position", -1);

        if (position > -1) {
            isEditMode = true;
            titlePageCreateEdit.setText("Update Expense");

            Expense item = AppData.ListItemExpense.get(position);

            edtItemName.setText(item.getName());
            edtQuantity.setText(String.valueOf(item.getQuantity()));
            edtUnitPrice.setText(String.valueOf(item.getUnitPrice()));

            // ⭐ CHUYỂN TIMESTAMP → NGÀY ĐỂ HIỂN THỊ
            long timestamp = item.getTimestamp();
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(timestamp);

            selectedDay = cal.get(Calendar.DAY_OF_MONTH);
            selectedMonth = cal.get(Calendar.MONTH) + 1;
            selectedYear = cal.get(Calendar.YEAR);

            edtCalendar.setText(
                    selectedDay + "/" + selectedMonth + "/" + selectedYear
            );
        }
    }


    private void setupEvents() {
        btnSubmitCreate.setOnClickListener(v -> {
            if (isEditMode) editItem();
            else createItem();
        });

        btnBackToList.setOnClickListener(v -> backToMain());
        edtCalendar.setOnClickListener(v -> showDatePicker());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();

        if (selectedYear != -1) {
            calendar.set(selectedYear, selectedMonth - 1, selectedDay);
        }

        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, y, m, d) -> {
                    selectedDay = d;
                    selectedMonth = m + 1;
                    selectedYear = y;

                    edtCalendar.setText(d + "/" + (m + 1) + "/" + y);
                },
                year, month, day
        );

        dialog.show();
    }


    private void backToMain() {
        startActivity(new Intent(this, ExpenseActivity.class));
        finish();
    }

    private void editItem() {

        if (!validateInput()) return;

        DatabaseHelper db = new DatabaseHelper(this);

        // Lấy expense cũ
        Expense old = AppData.ListItemExpense.get(position);

        // 1. Hoàn lại budget cũ
        db.revertBudgetBeforeEdit(old);

        // 2. Update dữ liệu mới
        int quantity = Integer.parseInt(edtQuantity.getText().toString());
        int unitPrice = Integer.parseInt(edtUnitPrice.getText().toString());

        old.setName(edtItemName.getText().toString());
        old.setQuantity(quantity);
        old.setUnitPrice(unitPrice);

        Calendar cal = Calendar.getInstance();
        cal.set(selectedYear, selectedMonth - 1, selectedDay, 0, 0, 0);
        old.setTimestamp(cal.getTimeInMillis());

        // 3. Update vào SQLite
        boolean result = db.updateProduct(old);
        if (!result) {
            Toast.makeText(this, "Update failed!", Toast.LENGTH_SHORT).show();
            return;
        }

        // 4. Trừ ngân sách mới
        long userId = sharedPreferences.getLong("user_id", -1);
        db.updateBudgetAfterExpense(old, userId);

        // 5. Reload list & sort
        AppData.ListItemExpense = db.getAllExpenses(userId);
        Collections.sort(AppData.ListItemExpense, (a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()));

        Toast.makeText(this, "Updated!", Toast.LENGTH_SHORT).show();
        finish();
    }



    private void createItem() {

        if (!validateInput()) return;

        if (selectedYear == -1 || selectedMonth == -1 || selectedDay == -1) {
            Toast.makeText(this, "Please select date of year!", Toast.LENGTH_SHORT).show();
            return;
        }

        String itemName = edtItemName.getText().toString();
        int quantity = Integer.parseInt(edtQuantity.getText().toString());
        int unitPrice = Integer.parseInt(edtUnitPrice.getText().toString());

        Calendar cal = Calendar.getInstance();
        cal.set(selectedYear, selectedMonth - 1, selectedDay, 0, 0, 0);
        long timestamp = cal.getTimeInMillis();

        Expense item = new Expense(itemName, quantity, unitPrice, timestamp);

        DatabaseHelper db = new DatabaseHelper(this);
        long userId = sharedPreferences.getLong("user_id", -1);

        if (userId <= 0) {
            Toast.makeText(this, "Error get userId", Toast.LENGTH_SHORT).show();
            return;
        }

        long productId = db.addProduct(item, userId);

        if (productId <= 0) {
            Toast.makeText(this, "Insert failed!", Toast.LENGTH_SHORT).show();
            return;
        }

        // GỌI HÀM UPDATE BUDGET bên DBHELPER
        db.updateBudgetAfterExpense(item, userId);
        AppData.ListItemExpense.add(item);

        Toast.makeText(this, "Added!", Toast.LENGTH_SHORT).show();
        backToMain();
    }



    private boolean validateInput() {

        if (edtItemName.getText().toString().trim().isEmpty()) {
            edtItemName.setError("Cannot be left blank");
            return false;
        }

        try {
            int quantity = Integer.parseInt(edtQuantity.getText().toString());
            int price = Integer.parseInt(edtUnitPrice.getText().toString());

            if (quantity < 1) {
                edtQuantity.setError("Quatity > 0");
                return false;
            }

            if (price < 1) {
                edtUnitPrice.setError("Unit price > 0");
                return false;
            }

        } catch (Exception e) {
            edtQuantity.setError("Error format");
            edtUnitPrice.setError("Error format");
            return false;
        }

        return true;
    }
}
