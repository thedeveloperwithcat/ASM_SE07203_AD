package com.example.se07203_b5.Activitys;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.se07203_b5.Database.DatabaseHelper;
import com.example.se07203_b5.Models.User;
import com.example.se07203_b5.R;
import com.example.se07203_b5.Utils.AppData;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {
    Button btnSubmitLogin, btnGoToRegister;
    EditText edtUsername, edtPassword;
    DatabaseHelper databaseHelper;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        databaseHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("AppData", MODE_PRIVATE);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();

        btnSubmitLogin = findViewById(R.id.btnSubmitLogin);
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnGoToRegister = findViewById(R.id.btnGoToRegister);

        // Xin quyền cho Android 13+ (Máy Android < 9 sẽ tự bỏ qua)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        btnGoToRegister.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });

        btnSubmitLogin.setOnClickListener(v -> {
            String username = edtUsername.getText().toString();
            String password = edtPassword.getText().toString();

            try {
                DatabaseHelper db = new DatabaseHelper(this);
                User user = db.getUser(username, password);

                if (user != null && user.getFullname() != null) {
                    sharedPreferencesEditor.putString("username", username);
                    sharedPreferencesEditor.putString("fullname", user.getFullname());
                    sharedPreferencesEditor.putLong("user_id", user.getId());
                    sharedPreferencesEditor.putBoolean("isLogin", true);
                    sharedPreferencesEditor.apply();

                    AppData.ListItemExpense = db.getExpenseByUserId(user.getId());
                    AppData.ListItemBudget = db.getBudgetByUserId(user.getId());

                    // Lên lịch
                    scheduleWeeklyReport();

                    Intent intent = new Intent(this, HomeActivity.class);
                    startActivity(intent);
                    Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Username or Password is incorrect", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void scheduleWeeklyReport() {
        try {
            WorkManager workManager = WorkManager.getInstance(this);

            Calendar currentDate = Calendar.getInstance();
            Calendar dueDate = Calendar.getInstance();
            dueDate.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            dueDate.set(Calendar.HOUR_OF_DAY, 9);
            dueDate.set(Calendar.MINUTE, 0);
            dueDate.set(Calendar.SECOND, 0);

            if (dueDate.before(currentDate)) {
                dueDate.add(Calendar.HOUR_OF_DAY, 24 * 7);
            }

            long timeDiff = dueDate.getTimeInMillis() - currentDate.getTimeInMillis();

            PeriodicWorkRequest weeklyRequest =
                    new PeriodicWorkRequest.Builder(WeeklyReportActivity.class, 7, TimeUnit.DAYS)
                            .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
                            .addTag("weekly_report")
                            .build();

            workManager.enqueueUniquePeriodicWork(
                    "weekly_report_job",
                    ExistingPeriodicWorkPolicy.UPDATE,
                    weeklyRequest
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}