package com.example.se07203_b5.Activitys;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.se07203_b5.Database.DatabaseHelper;
import com.example.se07203_b5.Models.User;
import com.example.se07203_b5.R;

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
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });

        btnRegisterSubmit.setOnClickListener(v -> { // Xử lý sự kiện đăng ký tài khoản
            String username = edtRegisterUsername.getText().toString();
            String password = edtRegisterPassword.getText().toString();
            String fullname = edtRegisterFullname.getText().toString();
            User user = new User(0, username, password, fullname);
            try {
                DatabaseHelper db = new DatabaseHelper(this);
                long resultId = db.addUser(user);
                if (resultId > 0) {
                    Toast.makeText(this, "Register Success", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(this, "Register Failed", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}