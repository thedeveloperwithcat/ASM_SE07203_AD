package com.example.se07203_b5.Activitys;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.example.se07203_b5.Database.DatabaseHelper;
import com.example.se07203_b5.Models.Budget;
import com.example.se07203_b5.Models.Expense;
import com.example.se07203_b5.R;
import com.example.se07203_b5.Utils.AppData;

import java.util.ArrayList;
import java.util.Calendar;

public class CreateExpenseActivity extends AppCompatActivity {

    private EditText etQty, etPrice, etDate;
    private TextView titlePage;
    private Spinner spBudget;
    private Button btnCreate, btnBackToList;
    private long selectedDateMillis = System.currentTimeMillis();
    private DatabaseHelper DBHelper;
    private ArrayList<Budget> budgets;

    private boolean isUpdate = false;
    private Expense oldExpense;
    private int position = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_expense);

        DBHelper = new DatabaseHelper(this);

        mappingViews();
        loadBudgetSpinner();
        detectUpdateMode();   // <--- NEW
        setupDatePicker();
        setupCreate();
        setupEventBack();
    }

    private void mappingViews() {
        etQty   = findViewById(R.id.edtQuantity);
        etPrice = findViewById(R.id.edtUnitPrice);
        etDate  = findViewById(R.id.edtTime);
        spBudget = findViewById(R.id.spBudget);
        btnCreate = findViewById(R.id.btnSubmitCreate);
        btnBackToList = findViewById(R.id.btnBackToList);
        titlePage = findViewById(R.id.titlePageCreateEdit);
    }

    // ===================== DETECT UPDATE MODE =====================
    private void detectUpdateMode() {
        if (getIntent().hasExtra("position") && budgets != null && !budgets.isEmpty()) {
            isUpdate = true;
            position = getIntent().getIntExtra("position", -1);
            oldExpense = AppData.ListItemExpense.get(position);

            // Fill old data
            etQty.setText(String.valueOf(oldExpense.getQuantity()));
            etPrice.setText(String.valueOf(oldExpense.getUnitPrice()));

            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(oldExpense.getTimestamp());
            selectedDateMillis = oldExpense.getTimestamp();
            String show = cal.get(Calendar.DAY_OF_MONTH) + "/" +
                    (cal.get(Calendar.MONTH) + 1) + "/" +
                    cal.get(Calendar.YEAR);
            etDate.setText(show);

            titlePage.setText("Update Expense");
            btnCreate.setText("Update");

            // Set budget spinner cũ và disable
            for (int i = 0; i < budgets.size(); i++) {
                if (budgets.get(i).getId() == oldExpense.getBudgetId()) {
                    spBudget.setSelection(i + 1); // +1 vì hint
                    break;
                }
            }
            spBudget.setEnabled(false);
        }
    }



    // ===================== LOAD SPINNER =====================
    private void loadBudgetSpinner() {
        budgets = AppData.ListItemBudget;
        ArrayList<String> names = new ArrayList<>();

        names.add("Select budget");
        for (Budget b : budgets) names.add(b.getName());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                names
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spBudget.setAdapter(adapter);

        if (isUpdate) {
            // set đúng budget cũ
            for (int i = 0; i < budgets.size(); i++) {
                if (budgets.get(i).getId() == oldExpense.getBudgetId()) {
                    spBudget.setSelection(i + 1);
                    break;
                }
            }
        } else {
            spBudget.setSelection(0);
        }
    }

    // ===================== DATE PICKER =====================
    private void setupDatePicker() {
        etDate.setOnClickListener(v -> openDateDialog());
    }

    private void openDateDialog() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(selectedDateMillis);

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (DatePicker view, int year, int monthOfYear, int dayOfMonth) -> {
                    Calendar c = Calendar.getInstance();
                    c.set(year, monthOfYear, dayOfMonth, 0, 0);
                    c.set(Calendar.SECOND, 0);
                    c.set(Calendar.MILLISECOND, 0);

                    selectedDateMillis = c.getTimeInMillis();

                    String show = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                    etDate.setText(show);
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
    }

    // ===================== CREATE / UPDATE =====================
    private void setupCreate() {
        btnCreate.setOnClickListener(v -> {

            if (!validateInputs()) return;

            int qty = Integer.parseInt(etQty.getText().toString().trim());
            int price = Integer.parseInt(etPrice.getText().toString().trim());

            if (spBudget.getSelectedItemPosition() == 0) {
                Toast.makeText(this, "Please choose a budget!", Toast.LENGTH_SHORT).show();
                return;
            }

            Budget selectedBudget = budgets.get(spBudget.getSelectedItemPosition() - 1);
            long userId = getSharedPreferences("AppData", MODE_PRIVATE).getLong("user_id", 0);

            if (isUpdate) {
                Expense updated = new Expense(
                        oldExpense.getId(),        // 👈 gán id cũ
                        selectedBudget.getName(),
                        qty,
                        price,
                        selectedDateMillis,
                        userId,
                        oldExpense.getBudgetId()   // spinner đang frozen, nên giữ budget cũ
                );

                boolean ok = DBHelper.updateExpense(updated, oldExpense.getBudgetId(), userId);

                if (!ok) {
                    Toast.makeText(this, "Update failed! Check budget/time", Toast.LENGTH_LONG).show();
                    return;
                }

                AppData.ListItemExpense.clear();
                AppData.ListItemExpense.addAll(DBHelper.getAllExpenses(userId));

                Toast.makeText(this, "Updated successfully!", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            }
            else {
                // ================== CREATE ==================
                Expense newExpense = new Expense(
                        selectedBudget.getName(),
                        qty,
                        price,
                        selectedDateMillis,
                        userId,
                        selectedBudget.getId() // thêm budgetId
                );
                boolean ok = DBHelper.addExpense(newExpense, selectedBudget.getId(), userId);

                if (!ok) {
                    if (selectedDateMillis < selectedBudget.getStartTimestamp() ||
                            selectedDateMillis > selectedBudget.getEndTimestamp()) {

                        Toast.makeText(this, "Date not in budget time!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Not enough budget!", Toast.LENGTH_LONG).show();
                    }
                    return;
                } else {

                    // Kiểm tra cảnh báo 80%
                    float percent = DBHelper.getBudgetUsagePercentage(selectedBudget.getId()); // Hoặc newBudgetId nếu là update
                    if (percent >= 80) {
                        showBudgetWarningNotification(selectedBudget.getName(), percent);
                    }
                }

                AppData.ListItemExpense.clear();
                AppData.ListItemExpense.addAll(DBHelper.getAllExpenses(userId));

                Toast.makeText(this, "Expense Added!", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            }
        });
    }
    private void showBudgetWarningNotification(String budgetName, float percent) {
        android.app.NotificationManager manager = (android.app.NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "budget_warning_channel";

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            android.app.NotificationChannel channel = new android.app.NotificationChannel(channelId, "Budget Warnings", android.app.NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }
        androidx.core.app.NotificationCompat.Builder builder = new androidx.core.app.NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Cảnh báo chi tiêu!")
                .setContentText("Ngân sách '" + budgetName + "' đã dùng " + String.format("%.1f", percent) + "%. Sắp hết hạn mức!")
                .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH);

        manager.notify((int) System.currentTimeMillis(), builder.build());
    }
    private boolean validateInputs() {
        if (etQty.getText().toString().trim().isEmpty()
                || etPrice.getText().toString().trim().isEmpty()
                || etDate.getText().toString().trim().isEmpty()) {

            Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            Integer.parseInt(etQty.getText().toString().trim());
            Integer.parseInt(etPrice.getText().toString().trim());
        } catch (Exception e) {
            Toast.makeText(this, "Quantity & Price must be number!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    // ===================== BACK =====================
    private void setupEventBack() {
        btnBackToList.setOnClickListener(v -> {
            startActivity(new Intent(this, ExpenseActivity.class));
            finish();
        });
    }

}
