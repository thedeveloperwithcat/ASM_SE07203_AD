package com.example.se07203_b5;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;
import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "SE07203Expense";
    // Sửa lỗi 5: Tăng phiên bản DB để kích hoạt onUpgrade()
    private static final int DATABASE_VERSION = 4;

    // Bảng lưu mua theo tháng
    private static final String TABLE_MONTHLY = "monthly_purchases";
    private static final String TABLE_MONTHLY_COLUMN_ID = "id";
    private static final String TABLE_MONTHLY_COLUMN_PRODUCT_ID = "product_id";
    private static final String TABLE_MONTHLY_COLUMN_MONTH = "month";
    private static final String TABLE_MONTHLY_COLUMN_YEAR = "year";
    private static final String TABLE_MONTHLY_COLUMN_QUANTITY = "quantity";
    private static final String TABLE_MONTHLY_COLUMN_TOTAL_PRICE = "total_price";
    private static final String TABLE_MONTHLY_COLUMN_USER_ID = "user_id";

    // Bảng User
    private static final String TABLE_USER = "users";
    private static final String TABLE_USER_COLUMN_ID = "id";
    private static final String TABLE_USER_COLUMN_USERNAME = "username";
    private static final String TABLE_USER_COLUMN_PASSWORD = "password";
    private static final String TABLE_USER_COLUMN_FULLNAME = "fullname";

    // Bảng Product
    private static final String TABLE_PRODUCT = "products";
    private static final String TABLE_PRODUCT_COLUMN_ID = "id";
    private static final String TABLE_PRODUCT_COLUMN_NAME = "name";
    private static final String TABLE_PRODUCT_COLUMN_PRICE = "price";
    private static final String TABLE_PRODUCT_COLUMN_QUANTITY = "quantity";
    private static final String TABLE_PRODUCT_COLUMN_USER_ID = "user_id";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Sửa lỗi 1: Xóa các lệnh DROP TABLE không cần thiết khỏi onCreate
        String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USER + "("
                + TABLE_USER_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TABLE_USER_COLUMN_USERNAME + " TEXT, "
                + TABLE_USER_COLUMN_FULLNAME + " TEXT, "
                + TABLE_USER_COLUMN_PASSWORD + " TEXT);";
        db.execSQL(CREATE_TABLE_USERS);

        String CREATE_TABLE_PRODUCTS = "CREATE TABLE " + TABLE_PRODUCT + "("
                + TABLE_PRODUCT_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TABLE_PRODUCT_COLUMN_NAME + " TEXT, "
                + TABLE_PRODUCT_COLUMN_PRICE + " INTEGER, "
                + TABLE_PRODUCT_COLUMN_QUANTITY + " INTEGER, "
                + TABLE_PRODUCT_COLUMN_USER_ID + " INTEGER);";
        db.execSQL(CREATE_TABLE_PRODUCTS);

        String CREATE_TABLE_MONTHLY = "CREATE TABLE " + TABLE_MONTHLY + "("
                + TABLE_MONTHLY_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TABLE_MONTHLY_COLUMN_PRODUCT_ID + " INTEGER, "
                + TABLE_MONTHLY_COLUMN_MONTH + " INTEGER, "
                + TABLE_MONTHLY_COLUMN_YEAR + " INTEGER, "
                + TABLE_MONTHLY_COLUMN_QUANTITY + " INTEGER, "
                + TABLE_MONTHLY_COLUMN_TOTAL_PRICE + " INTEGER, "
                + TABLE_MONTHLY_COLUMN_USER_ID + " INTEGER);";
        db.execSQL(CREATE_TABLE_MONTHLY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Sửa lỗi 1 & 4: Di chuyển DROP TABLE vào đây và thêm bảng monthly
        // Cảnh báo: Việc này sẽ xóa toàn bộ dữ liệu khi nâng cấp phiên bản.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MONTHLY);
        // Gọi lại onCreate để tạo lại cấu trúc bảng từ đầu
        onCreate(db);
    }

    // Sửa lỗi 2: Viết lại hoàn chỉnh phương thức addMonthlyPurchases
    public long addMonthlyPurchases(long productId, int month, int year, int quantity, int totalPrice, long currentUserId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TABLE_MONTHLY_COLUMN_PRODUCT_ID, productId);
        values.put(TABLE_MONTHLY_COLUMN_MONTH, month);
        values.put(TABLE_MONTHLY_COLUMN_YEAR, year);
        values.put(TABLE_MONTHLY_COLUMN_QUANTITY, quantity);
        values.put(TABLE_MONTHLY_COLUMN_TOTAL_PRICE, totalPrice);
        values.put(TABLE_MONTHLY_COLUMN_USER_ID, currentUserId);

        long id = db.insert(TABLE_MONTHLY, null, values);
        db.close();
        return id; // Trả về ID của dòng mới được chèn, hoặc -1 nếu có lỗi
    }


    public ArrayList<Item> getProducts(long UserId) {
        ArrayList<Item> items = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PRODUCT,
                new String[]{TABLE_PRODUCT_COLUMN_ID, TABLE_PRODUCT_COLUMN_NAME, TABLE_PRODUCT_COLUMN_QUANTITY, TABLE_PRODUCT_COLUMN_PRICE},
                TABLE_PRODUCT_COLUMN_USER_ID + "=?",
                new String[]{String.valueOf(UserId)},
                null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Item item = new Item(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getInt(2),
                        cursor.getInt(3)
                );
                items.add(item);
            } while (cursor.moveToNext());
        }

        // Sửa lỗi 3: Đóng con trỏ và database để tránh rò rỉ tài nguyên
        cursor.close();
        db.close();

        return items;
    }

    // --- Các phương thức khác cũng được sửa để đảm bảo đóng tài nguyên ---

    public long addProduct(Item product, long UserId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TABLE_PRODUCT_COLUMN_NAME, product.getName());
        values.put(TABLE_PRODUCT_COLUMN_QUANTITY, product.getQuantity());
        values.put(TABLE_PRODUCT_COLUMN_PRICE, product.getUnitPrice());
        values.put(TABLE_PRODUCT_COLUMN_USER_ID, UserId);
        long id = db.insert(TABLE_PRODUCT, null, values);
        db.close(); // Đảm bảo db được đóng
        return id;
    }

    public ArrayList<MonthlyPurchases> getMonthlyPurchasesByMonth(long userId, int month, int year) {
        ArrayList<MonthlyPurchases> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_MONTHLY,
                new String[]{
                        TABLE_MONTHLY_COLUMN_ID,
                        TABLE_MONTHLY_COLUMN_PRODUCT_ID,
                        TABLE_MONTHLY_COLUMN_MONTH,
                        TABLE_MONTHLY_COLUMN_YEAR,
                        TABLE_MONTHLY_COLUMN_QUANTITY,
                        TABLE_MONTHLY_COLUMN_TOTAL_PRICE
                },
                TABLE_MONTHLY_COLUMN_USER_ID + "=? AND " +
                        TABLE_MONTHLY_COLUMN_MONTH + "=? AND " +
                        TABLE_MONTHLY_COLUMN_YEAR + "=?",
                new String[]{String.valueOf(userId), String.valueOf(month), String.valueOf(year)},
                null, null, null);

        if (cursor.moveToFirst()) {
            do {
                // Sửa ở đây: Sử dụng getInt() hoặc getLong() cho cột thứ 5 (total_price)
                MonthlyPurchases mp = new MonthlyPurchases(
                        cursor.getLong(0),      // id
                        cursor.getLong(1),      // product_id
                        cursor.getInt(2),       // month
                        cursor.getInt(3),       // year
                        cursor.getInt(4),       // quantity
                        cursor.getInt(5)        // total_price (SỬA Ở ĐÂY)
                );
                list.add(mp);
            } while (cursor.moveToNext());
        }
        cursor.close(); // Đảm bảo cursor được đóng
        db.close();     // Đảm bảo db được đóng
        return list;
    }

    public long addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TABLE_USER_COLUMN_USERNAME, user.getUsername());
        values.put(TABLE_USER_COLUMN_PASSWORD, user.getPassword());
        values.put(TABLE_USER_COLUMN_FULLNAME, user.getFullname());
        long id = db.insert(TABLE_USER, null, values);
        db.close(); // Đảm bảo db được đóng
        return id;
    }

    public User getUserByUsernameAndPassword(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USER,
                new String[]{TABLE_USER_COLUMN_ID, TABLE_USER_COLUMN_USERNAME, TABLE_USER_COLUMN_FULLNAME, TABLE_USER_COLUMN_PASSWORD},
                TABLE_USER_COLUMN_USERNAME + "=? AND " + TABLE_USER_COLUMN_PASSWORD + "=?",
                new String[]{username, password},
                null, null, null);
        User user = null;
        if (cursor.moveToFirst()) {
            user = new User(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3)
            );
        }
        cursor.close(); // Đảm bảo cursor được đóng
        db.close();     // Đảm bảo db được đóng
        return user;
    }

    public boolean removeProductById(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_PRODUCT,
                TABLE_PRODUCT_COLUMN_ID + "= ?",
                new String[]{String.valueOf(id)}
        );
        db.close(); // Đảm bảo db được đóng
        return result > 0;
    }
}
