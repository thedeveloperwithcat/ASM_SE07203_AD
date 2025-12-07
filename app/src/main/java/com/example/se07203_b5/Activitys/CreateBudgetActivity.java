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
import com.example.se07203_b5.Models.Budget;
import com.example.se07203_b5.R;
import com.example.se07203_b5.Utils.AppData;

import java.util.Calendar;

public class CreateBudgetActivity extends AppCompatActivity {

    private EditText edtItemName, edtUnitPrice, edtStartDate, edtEndDate;
    private TextView titlePageCreateEdit;
    private Button btnSubmitCreate, btnBackToList;

    private boolean isEditMode = false;
    private int position = -1;

    private long startTimestamp = -1, endTimestamp = -1;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_budget);

        initViews();
        initSharedPref();
        checkEditMode();
        setupEvents();
    }

    private void initViews() {
        edtItemName = findViewById(R.id.edtItemName);
        edtUnitPrice = findViewById(R.id.edtUnitPrice);
        edtStartDate = findViewById(R.id.edtStartDate);
        edtEndDate = findViewById(R.id.edtEndDate);

        btnSubmitCreate = findViewById(R.id.btnSubmitCreate);
        btnBackToList = findViewById(R.id.btnBackToMain);

        titlePageCreateEdit = findViewById(R.id.titlePageCreateEdit);
    }

    private void initSharedPref() {
        sharedPreferences = getSharedPreferences("AppData", MODE_PRIVATE);
    }

    private void checkEditMode() {
        Intent intent = getIntent();
        if (intent == null || !intent.hasExtra("position")) return;

        position = intent.getIntExtra("position", -1);

        if (position >= 0) {
            isEditMode = true;
            titlePageCreateEdit.setText("Update Budget");

            Budget b = AppData.ListItemBudget.get(position);

            edtItemName.setText(b.getName());
            edtUnitPrice.setText(String.valueOf((int) b.getPrice()));

            startTimestamp = b.getStartTimestamp();
            endTimestamp = b.getEndTimestamp();

            edtStartDate.setText(formatDate(startTimestamp));
            edtEndDate.setText(formatDate(endTimestamp));
        }
    }

    private void setupEvents() {
        btnSubmitCreate.setOnClickListener(v -> {
            if (isEditMode) editBudget();
            else createBudget();
        });

        btnBackToList.setOnClickListener(v -> finish());

        edtStartDate.setOnClickListener(v -> showDatePicker(true));
        edtEndDate.setOnClickListener(v -> showDatePicker(false));
    }

    private void showDatePicker(boolean isStartDate) {
        Calendar calendar = Calendar.getInstance();

        int y = calendar.get(Calendar.YEAR);
        int m = calendar.get(Calendar.MONTH);
        int d = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, day) -> {
                    Calendar cal = Calendar.getInstance();
                    cal.set(year, month, day, 0, 0, 0);

                    long chosen = cal.getTimeInMillis();

                    if (isStartDate) {
                        startTimestamp = chosen;
                        edtStartDate.setText(day + "/" + (month + 1) + "/" + year);
                    } else {
                        endTimestamp = chosen;
                        edtEndDate.setText(day + "/" + (month + 1) + "/" + year);
                    }
                },
                y, m, d
        );

        dialog.show();
    }

    private boolean validateInput() {
        if (edtItemName.getText().toString().trim().isEmpty()) {
            edtItemName.setError("Enter group name");
            return false;
        }

        if (edtUnitPrice.getText().toString().trim().isEmpty()) {
            edtUnitPrice.setError("Enter amount");
            return false;
        }

        if (startTimestamp == -1) {
            Toast.makeText(this, "Select start date!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (endTimestamp == -1) {
            Toast.makeText(this, "Select end date!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (endTimestamp < startTimestamp) {
            Toast.makeText(this, "End date must be AFTER start date!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void createBudget() {
        if (!validateInput()) return;

        String name = edtItemName.getText().toString().trim();
        int price = Integer.parseInt(edtUnitPrice.getText().toString());

        Budget b = new Budget(name, price, startTimestamp, endTimestamp);

        DatabaseHelper db = new DatabaseHelper(this);

        long userId = sharedPreferences.getLong("user_id", -1);
        if (userId <= 0) {
            Toast.makeText(this, "Error getting user", Toast.LENGTH_SHORT).show();
            return;
        }

        long result = db.addBudget(b, userId);

        if (result <= 0) {
            Toast.makeText(this, "Create failed!", Toast.LENGTH_SHORT).show();
            return;
        }

        AppData.ListItemBudget.add(b);

        Toast.makeText(this, "Created!", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    private void editBudget() {
        if (!validateInput()) return;

        Budget b = AppData.ListItemBudget.get(position);

        b.setName(edtItemName.getText().toString().trim());
        b.setPrice(Integer.parseInt(edtUnitPrice.getText().toString()));
        b.setStartTimestamp(startTimestamp);
        b.setEndTimestamp(endTimestamp);

        DatabaseHelper db = new DatabaseHelper(this);

        boolean result = db.updateBudget(b);

        if (!result) {
            Toast.makeText(this, "Update failed!", Toast.LENGTH_SHORT).show();
            return;
        }

        // 🔥 Recalc toàn bộ budget sau khi sửa
        long userId = sharedPreferences.getLong("user_id", -1);
        db.recalcBudget(userId);

        Toast.makeText(this, "Updated!", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }


    private String formatDate(long ts) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(ts);
        return cal.get(Calendar.DAY_OF_MONTH) + "/" +
                (cal.get(Calendar.MONTH) + 1) + "/" +
                cal.get(Calendar.YEAR);
    }
}
