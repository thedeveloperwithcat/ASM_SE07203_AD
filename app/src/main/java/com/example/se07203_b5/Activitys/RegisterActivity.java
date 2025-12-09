package com.example.se07203_b5.Activitys;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.se07203_b5.Database.DatabaseHelper;
import com.example.se07203_b5.R;
import com.example.se07203_b5.Models.User;

public class RegisterActivity extends AppCompatActivity {

    Button btnRegisterSubmit, btnBackToLogin;
    EditText edtRegisterUsername, edtRegisterPassword, edtRegisterFullname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        btnRegisterSubmit = findViewById(R.id.btnRegisterSubmit);
        edtRegisterUsername = findViewById(R.id.edtRegisterUsername);
        edtRegisterPassword = findViewById(R.id.edtRegisterPassword);
        edtRegisterFullname = findViewById(R.id.edtRegisterFullname);
        btnBackToLogin = findViewById(R.id.btnBackToLogin);

        btnBackToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });

        btnRegisterSubmit.setOnClickListener(v -> {
            String username = edtRegisterUsername.getText().toString().trim();
            String password = edtRegisterPassword.getText().toString().trim();
            String fullname = edtRegisterFullname.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty() || fullname.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            DatabaseHelper db = new DatabaseHelper(this);

            // 🔥 CHECK USERNAME EXISTS
            if (db.isUsernameExists(username)) {
                Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show();
                return;
            }

            User user = new User(0, username, password, fullname);

            try {
                long resultId = db.addUser(user);
                if (resultId > 0) {
                    Toast.makeText(this, "Register Success", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                } else {
                    Toast.makeText(this, "Register Failed", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}