package com.example.se07203_b5;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;
public class CreateNewTaskActivity extends AppCompatActivity {

    Button btnSubmitCreate, btnBackToMain;
    TextView titlePageCreateEdit;
    Boolean isEditMode = false;
    int position = -1;

    SharedPreferences sharedPreferences;

    EditText edtItemName, edtQuantity, edtUnitPrice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_new_task);

        sharedPreferences = getSharedPreferences("AppData", MODE_PRIVATE);

        btnSubmitCreate = findViewById(R.id.btnSubmitCreate);
        btnBackToMain = findViewById(R.id.btnBackToMain);
        edtItemName = findViewById(R.id.edtItemName);
        edtQuantity = findViewById(R.id.edtQuantity);
        edtUnitPrice = findViewById(R.id.edtUnitPrice);
        titlePageCreateEdit = findViewById(R.id.titlePageCreateEdit);

        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            position = intent.getIntExtra("position", -1);
            if (position > -1) {
                isEditMode = true;
                titlePageCreateEdit.setText("Sửa thông tin");
                // set các thông tin item cần sửa lên EditText
                edtItemName.setText(AppData.ListItem.get(position).getName());
                edtQuantity.setText(String.valueOf(AppData.ListItem.get(position).getQuantity()));
                edtUnitPrice.setText(String.valueOf(AppData.ListItem.get(position).getUnitPrice()));
            }else{
                isEditMode = false;
            }
        }else{
            isEditMode = false;
        }

        btnSubmitCreate.setOnClickListener(v -> {
            if (isEditMode){
                editAnItem();
            }else{
                createNewItem();
            }
        });

        btnBackToMain.setOnClickListener(v -> {
            backToMain();
        });

    }

    private void backToMain(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void editAnItem() {
        String itemName = edtItemName.getText().toString().trim();
        int quantity = 0, unitPrice = 0;

        // Validate
        try {
            quantity = Integer.parseInt(edtQuantity.getText().toString());
            unitPrice = Integer.parseInt(edtUnitPrice.getText().toString());
        } catch (NumberFormatException e) {
            edtQuantity.setError("Số lượng và đơn giá phải là số hợp lệ");
            return;
        }

        if (quantity < 1) {
            edtQuantity.setError("Số lượng phải lớn hơn 0");
            return;
        }

        // Lấy item cần sửa
        Item item = AppData.ListItem.get(position);

        // Cập nhật vào object (RAM)
        item.setName(itemName);
        item.setQuantity(quantity);
        item.setUnitPrice(unitPrice);

        // Cập nhật vào Database
        DatabaseHelper db = new DatabaseHelper(this);
        boolean result = db.updateProduct(item);

        if (!result) {
            Toast.makeText(this, "Update product failed!", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Update product successfully!", Toast.LENGTH_SHORT).show();

        // Trả kết quả và đóng Activity
        setResult(RESULT_OK);
        finish();
    }

    // Trong CreateNewTaskActivity.java
    private void createNewItem(){
        String itemName = edtItemName.getText().toString();
        int quantity = 0, unitPrice = 0;
        try {
            quantity = Integer.parseInt(edtQuantity.getText().toString());
            unitPrice = Integer.parseInt(edtUnitPrice.getText().toString());
        }catch (NumberFormatException e){
            edtQuantity.setError("Số lượng phải lớn hơn 0");
            return;
        }

        if (quantity < 1){
            edtQuantity.setError("Số lượng phải lớn hơn 0");
            return;
        }else {
            Item item = new Item(itemName, quantity, unitPrice);
            DatabaseHelper databaseHelper = new DatabaseHelper(this);
            long userId = sharedPreferences.getLong("user_id", -1);
            if (userId > 0){
                // 1. Thêm sản phẩm vào bảng products
                long resultProductId = databaseHelper.addProduct(item, userId); // resultId ở đây là ProductId
                if (resultProductId <= 0){
                    Toast.makeText(this, "Error add product (item) to database", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 2. Ghi log giao dịch vào bảng monthly_purchases
                Calendar calendar = Calendar.getInstance();
                int month = calendar.get(Calendar.MONTH) + 1; // Tháng (0-11, nên +1)
                int year = calendar.get(Calendar.YEAR);
                int totalPrice = quantity * unitPrice;

                long resultLogId = databaseHelper.addMonthlyPurchase(
                        resultProductId, month, year, quantity, totalPrice, userId);

                if (resultLogId <= 0) {
                    Toast.makeText(this, "Error logging monthly purchase", Toast.LENGTH_SHORT).show();
                    // Dù thêm sản phẩm thành công, log thất bại thì vẫn tiếp tục
                }

                AppData.ListItem.add(item);
                Toast.makeText(this, "Add product successfully (Id = " + resultProductId + ")", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }else{
                Toast.makeText(this, "Error get userId", Toast.LENGTH_SHORT).show();
            }

        }
    }

}