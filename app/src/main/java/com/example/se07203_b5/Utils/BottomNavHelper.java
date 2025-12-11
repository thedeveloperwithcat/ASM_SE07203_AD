package com.example.se07203_b5.Utils;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import com.example.se07203_b5.Activitys.*;
import com.example.se07203_b5.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BottomNavHelper {
    public static void setup(Activity activity, BottomNavigationView bottomNav) {
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Intent intent = null;

            // KIỂM TRA ĐÚNG ID: nav_recurring
            if (itemId == R.id.nav_recurring && !(activity instanceof RecurringListActivity)) {
                intent = new Intent(activity, RecurringListActivity.class);
            }
            if (itemId == R.id.nav_expense && !(activity instanceof ExpenseActivity)) {
                intent = new Intent(activity, ExpenseActivity.class);
            }
            if (itemId == R.id.nav_budget && !(activity instanceof BudgetActivity)) {
                intent = new Intent(activity, BudgetActivity.class);
            }
            if (itemId == R.id.nav_report && !(activity instanceof MonthlyPurchasesActivity)) {
                intent = new Intent(activity, MonthlyPurchasesActivity.class);
            }

            if (intent != null) {
                activity.startActivity(intent);
                activity.overridePendingTransition(0, 0);
                return true;
            }

            if (itemId == R.id.nav_logout) {
                SharedPreferences prefs = activity.getSharedPreferences("AppData", Activity.MODE_PRIVATE);
                prefs.edit().clear().apply();
                Intent logoutIntent = new Intent(activity, LoginActivity.class);
                logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                activity.startActivity(logoutIntent);
                activity.overridePendingTransition(0, 0);
                activity.finish();
                return true;
            }
            return true;
        });
    }
}