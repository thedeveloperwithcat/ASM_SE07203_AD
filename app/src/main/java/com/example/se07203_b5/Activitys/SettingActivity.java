package com.example.se07203_b5.Activitys;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.se07203_b5.R;
import com.example.se07203_b5.Utils.BottomNavHelper;

public class SettingActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private Button btnLogout;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setting);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupBottomNav();
        setupEvents();
    }

    private void setupEvents() {
        btnLogout.setOnClickListener(v -> {
            SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
            sharedPreferencesEditor.putBoolean("isLogin", false);
            sharedPreferencesEditor.apply();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(this, "Logout successfully!", Toast.LENGTH_SHORT).show();
        });
    }

    private void initViews() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        btnLogout = findViewById(R.id.btnLogout);
        sharedPreferences = getSharedPreferences("AppData", MODE_PRIVATE);
    }

    private void setupBottomNav() {
        bottomNavigationView.setSelectedItemId(R.id.nav_setting );
        BottomNavHelper.setup(this, bottomNavigationView);
    }
}