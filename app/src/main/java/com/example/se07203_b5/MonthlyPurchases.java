package com.example.se07203_b5;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MonthlyPurchases extends AppCompatActivity {

    private EditText edtProduct, edtMonth, edtYear, edtQty, edtTotalPrice;
    private Button btnSaveMonthly, btnBack;

    private DatabaseHelper db;
    private long currentUserId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_purchases);

        // Khởi tạo DB
        db = new DatabaseHelper(this);

        // Lấy user id từ SharedPreferences (giống MainActivity)
        SharedPreferences sharedPreferences = getSharedPreferences("AppData", MODE_PRIVATE);
        currentUserId = sharedPreferences.getLong("user_id", 0);

        // Ánh xạ view đúng với XML
        edtProduct = findViewById(R.id.edtProduct);
        edtMonth = findViewById(R.id.edtMonth);
        edtYear = findViewById(R.id.edtYear);
        edtQty = findViewById(R.id.edtQty);               // ID đúng trong XML
        edtTotalPrice = findViewById(R.id.edtTotalPrice);
        btnSaveMonthly = findViewById(R.id.btnSaveMonthly);
        btnBack = findViewById(R.id.btnBack);

        // Nút quay lại
        btnBack.setOnClickListener(v -> finish());

        // Nút lưu theo tháng
        btnSaveMonthly.setOnClickListener(v -> saveMonthlyPurchase());
    }

    private void saveMonthlyPurchase() {
        String productName = edtProduct.getText().toString().trim();
        String monthStr = edtMonth.getText().toString().trim();
        String yearStr = edtYear.getText().toString().trim();
        String qtyStr = edtQty.getText().toString().trim();
        String totalStr = edtTotalPrice.getText().toString().trim();

        if (productName.isEmpty() || monthStr.isEmpty() || yearStr.isEmpty()
                || qtyStr.isEmpty() || totalStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        int month, year, quantity, totalPrice;
        try {
            month = Integer.parseInt(monthStr);
            year = Integer.parseInt(yearStr);
            quantity = Integer.parseInt(qtyStr);
            totalPrice = Integer.parseInt(totalStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Tháng / năm / số lượng / giá phải là số", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy danh sách sản phẩm của user để tìm productId theo tên
        ArrayList<Item> products = db.getProducts(currentUserId);
        Item selectedItem = null;
        for (Item item : products) {
            if (item.getName().equalsIgnoreCase(productName)) {
                selectedItem = item;
                break;
            }
        }

        if (selectedItem == null) {
            Toast.makeText(this,
                    "Không tìm thấy sản phẩm có tên này trong danh sách đồ cần mua",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Lưu vào bảng monthly_purchases
        long id = db.addMonthlyPurchases(
                selectedItem.getId(),  // product_id
                month,
                year,
                quantity,
                totalPrice,
                currentUserId
        );

        if (id > 0) {
            Toast.makeText(this, "Lưu thành công!", Toast.LENGTH_SHORT).show();
            // Clear form
            // edtProduct.setText(""); // nếu không muốn xóa tên sản phẩm thì comment lại
            edtMonth.setText("");
            edtYear.setText("");
            edtQty.setText("");
            edtTotalPrice.setText("");
        } else {
            Toast.makeText(this, "Lưu thất bại!", Toast.LENGTH_SHORT).show();
        }
    }
}
