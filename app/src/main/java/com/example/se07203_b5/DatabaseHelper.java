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
    private static final int DATABASE_VERSION = 4;

    //    Table User

    // Bảng lưu mua theo tháng
    private static final String TABLE_MONTHLY = "monthly_purchases";
    private static final String TABLE_MONTHLY_COLUMN_ID = "id";
    private static final String TABLE_MONTHLY_COLUMN_PRODUCT_ID = "product_id";
    private static final String TABLE_MONTHLY_COLUMN_MONTH = "month";
    private static final String TABLE_MONTHLY_COLUMN_YEAR = "year";
    private static final String TABLE_MONTHLY_COLUMN_QUANTITY = "quantity";
    private static final String TABLE_MONTHLY_COLUMN_TOTAL_PRICE = "total_price";
    private static final String TABLE_MONTHLY_COLUMN_USER_ID = "user_id";

    private static final String TABLE_USER = "users";
    private static final String TABLE_USER_COLUMN_ID = "id";
    private static final String TABLE_USER_COLUMN_USERNAME = "username";
    private static final String TABLE_USER_COLUMN_PASSWORD = "password";
    private static final String TABLE_USER_COLUMN_FULLNAME = "fullname";

    // Table Product
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
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCT);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_MONTHLY);


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

    public long addProduct(Item product, long UserId) {
        SQLiteDatabase db = this.getWritableDatabase(); // Khai báo kết nối database với quyền ghi
        ContentValues values = new ContentValues(); // Khai báo giá trị cần lưu vào database
        values.put(TABLE_PRODUCT_COLUMN_NAME, product.getName());
        values.put(TABLE_PRODUCT_COLUMN_QUANTITY, product.getQuantity());
        values.put(TABLE_PRODUCT_COLUMN_PRICE, product.getUnitPrice());
        values.put(TABLE_PRODUCT_COLUMN_USER_ID, UserId);
        long id = db.insert(TABLE_PRODUCT, null, values); // Thêm dữ liệu vào bảng (return id)
        db.close();
        return id;
    }

    public ArrayList<Item> getProducts(long UserId){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PRODUCT, // Truy vấn dữ liệu trong bảng TABLE_USER
                // Các cột cần truy vấn (cột nào cần lấy ra) (thứ tự index 0-1-2-3)
                new String[]{TABLE_PRODUCT_COLUMN_ID, TABLE_PRODUCT_COLUMN_NAME, TABLE_PRODUCT_COLUMN_QUANTITY, TABLE_PRODUCT_COLUMN_PRICE},
                // Điều kiện lấy (WHERE)
                TABLE_PRODUCT_COLUMN_USER_ID + "=?",
                // Giá trị của điều kiện lấy (được fill lần lượt vào dấu ?)
                new String[]{String.valueOf(UserId)},
                // Nhóm theo (groupBy), Having, OrderBy
                null, null, null);
        ArrayList<Item> items = new ArrayList<Item>();
        if(cursor.moveToFirst()) {
            do {
                Item item = new Item(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getInt(2),
                        cursor.getInt(3)
                );
                items.add(item); // Thêm vào danh sách
            } while (cursor.moveToNext());
        }
        return items;
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCT);
        onCreate(db);
    }

    public long addUser(User user){
        SQLiteDatabase db = this.getWritableDatabase(); // Khai báo kết nối database với quyền ghi
        ContentValues values = new ContentValues(); // Khai báo giá trị cần lưu vào database
        values.put(TABLE_USER_COLUMN_USERNAME, user.getUsername());
        values.put(TABLE_USER_COLUMN_PASSWORD, user.getPassword());
        values.put(TABLE_USER_COLUMN_FULLNAME, user.getFullname());
        long id = db.insert(TABLE_USER, null, values); // Thêm dữ liệu vào bảng (return id)
        db.close();
        return id;
    }

    public User getUserByUsernameAndPassword(String username, String password)
    {
        SQLiteDatabase db = this.getReadableDatabase(); // Khai báo kết nối database với quyền đọc
        Cursor cursor = db.query(TABLE_USER, // Truy vấn dữ liệu trong bảng TABLE_USER
                // Các cột cần truy vấn (cột nào cần lấy ra) (thứ tự index 0-1-2-3)
                new String[]{TABLE_USER_COLUMN_ID, TABLE_USER_COLUMN_USERNAME, TABLE_USER_COLUMN_FULLNAME, TABLE_USER_COLUMN_PASSWORD},
                // Điều kiện lấy (WHERE)
                TABLE_USER_COLUMN_USERNAME + "=? AND " + TABLE_USER_COLUMN_PASSWORD +"=?",
                // Giá trị của điều kiện lấy (được fill lần lượt vào dấu ?)
                new String[]{username, password},
                // Nhóm theo (groupBy), Having, OrderBy
                null, null, null);
        User user = null;
        if(cursor.moveToFirst()){
            user = new User(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3)
            );
        }
        cursor.close();
        db.close();
        return user;
    }
    public boolean removeProductById(long id){
        SQLiteDatabase db = this.getWritableDatabase(); // Khai báo kết nối database với quyền ghi
        int result = db.delete(TABLE_PRODUCT, // xóa dữ liệu bảng products
                TABLE_PRODUCT_COLUMN_ID + "= ?", // điều kiện theo id
                new String[]{String.valueOf(id)} // giá trị id cần xóa
        );
        db.close(); // close connection với databasse
        return result > 0; // result là số lượng record được xóa lớn hơn 0 tức là thành công
    }
    // Trong class DatabaseHelper.java
    public long addMonthlyPurchase(long productId, int month, int year, int quantity, int totalPrice, long userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TABLE_MONTHLY_COLUMN_PRODUCT_ID, productId);
        values.put(TABLE_MONTHLY_COLUMN_MONTH, month);
        values.put(TABLE_MONTHLY_COLUMN_YEAR, year);
        values.put(TABLE_MONTHLY_COLUMN_QUANTITY, quantity);
        values.put(TABLE_MONTHLY_COLUMN_TOTAL_PRICE, totalPrice);
        values.put(TABLE_MONTHLY_COLUMN_USER_ID, userId);
        long id = db.insert(TABLE_MONTHLY, null, values);
        db.close();
        return id;
    }

    // Trong class DatabaseHelper.java
    public ArrayList<MonthlyPurchase> getMonthlyPurchases(long UserId, int month, int year){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<MonthlyPurchase> monthlyItems = new ArrayList<>();

        String query = "SELECT T1." + TABLE_MONTHLY_COLUMN_ID + ", " +
                "T1." + TABLE_MONTHLY_COLUMN_PRODUCT_ID + ", " +
                "T1." + TABLE_MONTHLY_COLUMN_MONTH + ", " +
                "T1." + TABLE_MONTHLY_COLUMN_YEAR + ", " +
                "T1." + TABLE_MONTHLY_COLUMN_QUANTITY + ", " +
                "T1." + TABLE_MONTHLY_COLUMN_TOTAL_PRICE + ", " +
                "T2." + TABLE_PRODUCT_COLUMN_NAME +
                " FROM " + TABLE_MONTHLY + " T1 " +
                " INNER JOIN " + TABLE_PRODUCT + " T2 ON T1." + TABLE_MONTHLY_COLUMN_PRODUCT_ID + " = T2." + TABLE_PRODUCT_COLUMN_ID +
                " WHERE T1." + TABLE_MONTHLY_COLUMN_USER_ID + " = ? AND T1." + TABLE_MONTHLY_COLUMN_MONTH + " = ? AND T1." + TABLE_MONTHLY_COLUMN_YEAR + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(UserId), String.valueOf(month), String.valueOf(year)});

        if (cursor.moveToFirst()) {
            do {
                MonthlyPurchase item = new MonthlyPurchase(
                        cursor.getLong(0), // id Monthly
                        cursor.getLong(1), // product_id
                        cursor.getInt(2), // month
                        cursor.getInt(3), // year
                        cursor.getInt(4), // quantity
                        cursor.getInt(5), // total_price
                        cursor.getString(6) // product_name
                );
                monthlyItems.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return monthlyItems;
    }
}
