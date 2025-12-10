package com.example.se07203_b5.Utils;

import android.app.Activity;
import android.content.Intent;

import com.example.se07203_b5.Activitys.BudgetActivity;
import com.example.se07203_b5.Activitys.ExpenseActivity;
import com.example.se07203_b5.Activitys.HomeActivity;
import com.example.se07203_b5.Activitys.MonthlyPurchasesActivity;
import com.example.se07203_b5.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BottomNavHelper {

    public static void setup(Activity activity, BottomNavigationView bottomNav) {

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Intent intent = null;
            if (itemId == R.id.nav_home && !(activity instanceof HomeActivity)) {
                intent = new Intent(activity, HomeActivity.class);
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

//            if (itemId == R.id.nav_setting && !(activity instanceof SettingActivity)) {
//                intent = new Intent(activity, SettingActivity.class);
//            }

            if (intent != null) {
                activity.startActivity(intent);
                activity.overridePendingTransition(0, 0);
                // activity.finish(); → muốn thì để, không thì bỏ
                return true;
            }

            return true;
        });
    }
}
