package com.example.se07203_b5;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity; // Sửa lỗi 1: Import đúng lớp Activity
import java.util.ArrayList;
import java.util.List;

// Sửa lỗi 2: Cấu trúc lại lớp, lớp này nên là Activity chính
public class MonthlyPurchases extends AppCompatActivity {

    private Spinner edtProduct; // Sửa lỗi 3: edtProduct nên là Spinner
    private EditText edtMonth, edtYear, edtQty, edtTotalPrice;
    private Button btnSaveMonthly;

    private DatabaseHelper db;
    private ArrayList<Item> products;
    // Sửa lỗi 4: Xóa biến productAdapter không cần thiết
    private long currentUserId = 1; // Giả sử ID người dùng hiện tại là 1

    public MonthlyPurchases(long aLong, long aLong1, int anInt, int anInt1, int anInt2, int anInt3) {
    }

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Thêm dòng này để tuân thủ vòng đời Activity
        setContentView(R.layout.activity_monthly_purchases);

        db = new DatabaseHelper(this);

        // Ánh xạ các thành phần từ layout XML
        edtProduct = findViewById(R.id.edtProduct);
        edtMonth = findViewById(R.id.edtMonth);
        edtYear = findViewById(R.id.edtYear);
        edtQty = findViewById(R.id.edtQuantity);
        edtTotalPrice = findViewById(R.id.edtTotalPrice);
        btnSaveMonthly = findViewById(R.id.btnSaveMonthly);

        loadProductsToSpinner();

        btnSaveMonthly.setOnClickListener(v -> saveMonthlyPurchase());
    }

    private void loadProductsToSpinner() {
        // Lấy danh sách sản phẩm của người dùng
        products = db.getProducts(currentUserId);

        List<String> names = new ArrayList<>();
        if (products != null) {
            for (Item item : products) {
                names.add(item.getName());
            }
        }

        // Sửa lỗi 5: Sử dụng `this` thay vì `requireContext()` và sửa tham chiếu adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        edtProduct.setAdapter(adapter);
    }

    private void saveMonthlyPurchase() {
        if (products == null || products.isEmpty()) {
            Toast.makeText(this, "Chưa có sản phẩm nào", Toast.LENGTH_SHORT).show();
            return;
        }

        // Sửa lỗi 6: Lấy vị trí item được chọn trong Spinner một cách chính xác
        int pos = edtProduct.getSelectedItemPosition();
        Item selectedItem = products.get(pos);

        // Thêm kiểm tra đầu vào để tránh ứng dụng bị crash
        String monthStr = edtMonth.getText().toString();
        String yearStr = edtYear.getText().toString();
        String quantityStr = edtQty.getText().toString();
        String totalPriceStr = edtTotalPrice.getText().toString();

        if (monthStr.isEmpty() || yearStr.isEmpty() || quantityStr.isEmpty() || totalPriceStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int month = Integer.parseInt(monthStr);
            int year = Integer.parseInt(yearStr);
            int quantity = Integer.parseInt(quantityStr);
            int totalPrice = Integer.parseInt(totalPriceStr);

            // Gọi phương thức để thêm dữ liệu vào database
            long id = db.addMonthlyPurchases(
                    selectedItem.getId(),
                    month,
                    year,
                    quantity,
                    totalPrice,
                    currentUserId
            );

            if (id > 0) {
                Toast.makeText(this, "Lưu thành công!", Toast.LENGTH_SHORT).show();
                // Xóa các trường sau khi lưu thành công (tùy chọn)
                edtMonth.setText("");
                edtYear.setText("");
                edtQty.setText("");
                edtTotalPrice.setText("");
            } else {
                Toast.makeText(this, "Lưu thất bại!", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Vui lòng nhập số hợp lệ", Toast.LENGTH_SHORT).show();
        }
    }
}
