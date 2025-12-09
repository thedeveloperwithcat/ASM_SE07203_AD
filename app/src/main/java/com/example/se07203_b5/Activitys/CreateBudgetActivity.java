package com.example.se07203_b5.Activitys;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.se07203_b5.Database.DatabaseHelper;
import com.example.se07203_b5.Models.Budget;
import com.example.se07203_b5.R;

import java.util.Calendar;

public class CreateBudgetActivity extends AppCompatActivity {

    EditText edtItemName, edtUnitPrice, edtStartDate, edtEndDate;
    Button btnSubmitCreate, btnBackToMain;
    DatabaseHelper db;

    long startTimestamp = 0;
    long endTimestamp = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_budget);

        db = new DatabaseHelper(this);

        initViews();
        setupDatePickers();
        setupEvents();
    }

    private void initViews() {
        edtItemName = findViewById(R.id.edtItemName);
        edtUnitPrice = findViewById(R.id.edtUnitPrice);
        edtStartDate = findViewById(R.id.edtStartDate);
        edtEndDate = findViewById(R.id.edtEndDate);
        btnSubmitCreate = findViewById(R.id.btnSubmitCreate);
        btnBackToMain = findViewById(R.id.btnBackToMain);
    }

    // 🔥 DatePicker giữ nguyên ý tưởng nhưng format chuẩn (millis)
    private void setupDatePickers() {
        edtStartDate.setOnClickListener(v -> openDatePicker(true));
        edtEndDate.setOnClickListener(v -> openDatePicker(false));
    }

    private void openDatePicker(boolean isStart) {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog dp = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    Calendar c = Calendar.getInstance();
                    c.set(year, month, dayOfMonth);

                    // RESET GIỜ → 00:00:00.000
                    c.set(Calendar.HOUR_OF_DAY, 0);
                    c.set(Calendar.MINUTE, 0);
                    c.set(Calendar.SECOND, 0);
                    c.set(Calendar.MILLISECOND, 0);

                    String dateText = dayOfMonth + "/" + (month + 1) + "/" + year;

                    if (isStart) {
                        startTimestamp = c.getTimeInMillis();
                        edtStartDate.setText(dateText);
                    } else {
                        endTimestamp = c.getTimeInMillis();
                        edtEndDate.setText(dateText);
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        dp.show();
    }


    // 🔥 Lưu và reload danh sách Budget
    private void setupEvents() {
        btnSubmitCreate.setOnClickListener(v -> saveBudget());
        btnBackToMain.setOnClickListener(v -> finish());
    }

    private void saveBudget() {
        String name = edtItemName.getText().toString().trim();
        String priceStr = edtUnitPrice.getText().toString().trim();

        if (name.isEmpty() || priceStr.isEmpty() || startTimestamp == 0 || endTimestamp == 0) {
            Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (endTimestamp < startTimestamp) {
            Toast.makeText(this, "End date must be after start date!", Toast.LENGTH_SHORT).show();
            return;
        }

        int price = Integer.parseInt(priceStr);

        long userId = getSharedPreferences("AppData", MODE_PRIVATE).getLong("user_id", 0);

        Budget b = new Budget(name, price, startTimestamp, endTimestamp, userId);

        if (db.addBudget(b, userId)) {
            Toast.makeText(this, "Budget created", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show();
        }
    }

}
