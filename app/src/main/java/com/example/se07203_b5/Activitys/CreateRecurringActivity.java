package com.example.se07203_b5.Activitys;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.se07203_b5.Database.DatabaseHelper;
import com.example.se07203_b5.Models.RecurringExpense;
import com.example.se07203_b5.R;
import com.google.android.material.textfield.TextInputEditText;
import java.util.Calendar;

public class CreateRecurringActivity extends AppCompatActivity {
    private TextInputEditText edtName, edtAmount, edtDate;
    private RadioGroup rgFrequency;
    private Button btnSave;
    private long selectedTimestamp = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_recurring);
        initViews();
        setupEvents();
    }

    private void initViews() {
        edtName = findViewById(R.id.edtName);
        edtAmount = findViewById(R.id.edtAmount);
        edtDate = findViewById(R.id.edtDate);
        rgFrequency = findViewById(R.id.rgFrequency);
        btnSave = findViewById(R.id.btnSave);
    }

    private void setupEvents() {
        edtDate.setOnClickListener(v -> showDatePicker());
        btnSave.setOnClickListener(v -> saveExpense());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            Calendar selectedCal = Calendar.getInstance();
            selectedCal.set(year, month, dayOfMonth);
            selectedTimestamp = selectedCal.getTimeInMillis();
            edtDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void saveExpense() {
        String name = edtName.getText().toString().trim();
        String amountStr = edtAmount.getText().toString().trim();

        if (TextUtils.isEmpty(name)) { edtName.setError("Required"); return; }
        if (TextUtils.isEmpty(amountStr)) { edtAmount.setError("Required"); return; }
        if (selectedTimestamp == -1) { Toast.makeText(this, "Select date", Toast.LENGTH_SHORT).show(); return; }

        double amount = Double.parseDouble(amountStr);
        String frequency = "Monthly";
        int selectedId = rgFrequency.getCheckedRadioButtonId();
        if (selectedId == R.id.rbWeekly) frequency = "Weekly";
        else if (selectedId == R.id.rbYearly) frequency = "Yearly";

        RecurringExpense newItem = new RecurringExpense(name, amount, frequency, selectedTimestamp);
        DatabaseHelper db = new DatabaseHelper(this);
        if (db.addRecurringExpense(newItem)) {
            Toast.makeText(this, "Added successfully!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error saving!", Toast.LENGTH_SHORT).show();
        }
    }
}