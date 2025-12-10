package com.example.se07203_b5.Activitys;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.se07203_b5.R;
import com.example.se07203_b5.Utils.BottomNavHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);  // CHECK: Layout này phải chứa BottomNavigationView

        initViews();
        setupBottomNav();

    }

    private void initViews() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);

    }
    private void setupBottomNav() {
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
        BottomNavHelper.setup(this, bottomNavigationView);
    }

}