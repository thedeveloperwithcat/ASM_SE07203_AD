package com.example.se07203_b5.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.se07203_b5.Models.Budget;
import com.example.se07203_b5.Models.Expense;
import com.example.se07203_b5.Models.User;

import java.util.ArrayList;
import java.util.Calendar;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SE07203Expense";
    private static final int DATABASE_VERSION = 11;

    // ==============================================
    // USER TABLE
    // ==============================================
    private static final String TABLE_USER = "users";
    private static final String USER_ID = "id";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String FULLNAME = "fullname";

    // ==============================================
    // EXPENSE TABLE
    // ==============================================
    private static final String TABLE_EXPENSE = "expenses";
    private static final String EXPENSE_ID = "id";
    private static final String EXPENSE_NAME = "name";
    private static final String EXPENSE_PRICE = "price";
    private static final String EXPENSE_QUANTITY = "quantity";
    private static final String EXPENSE_USER_ID = "user_id";
    private static final String EXPENSE_TIMESTAMP = "timestamp";

    // ==============================================
    // BUDGET TABLE
    // ==============================================
    public static final String TABLE_BUDGET = "budgets";
    public static final String BUDGET_ID = "id";
    public static final String BUDGET_NAME = "name";
    public static final String BUDGET_PRICE = "price";
    public static final String BUDGET_START_TIMESTAMP = "startTimestamp";
    public static final String BUDGET_END_TIMESTAMP = "endTimestamp";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // ==============================================
    // CREATE TABLES
    // ==============================================
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + TABLE_USER + "("
                + USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + USERNAME + " TEXT, "
                + FULLNAME + " TEXT, "
                + PASSWORD + " TEXT);"
        );

        db.execSQL("CREATE TABLE " + TABLE_EXPENSE + "("
                + EXPENSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + EXPENSE_NAME + " TEXT, "
                + EXPENSE_PRICE + " INTEGER, "
                + EXPENSE_QUANTITY + " INTEGER, "
                + EXPENSE_USER_ID + " INTEGER, "
                + EXPENSE_TIMESTAMP + " INTEGER);"
        );

        db.execSQL("CREATE TABLE " + TABLE_BUDGET + "("
                + BUDGET_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BUDGET_NAME + " TEXT NOT NULL, "
                + BUDGET_PRICE + " INTEGER NOT NULL, "
                + BUDGET_START_TIMESTAMP + " INTEGER NOT NULL, "
                + BUDGET_END_TIMESTAMP + " INTEGER NOT NULL);"
        );

    }

    // ==============================================
    // UPGRADE
    // ==============================================
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUDGET);
        onCreate(db);
    }

    // ==============================================
    // USER FUNCTIONS
    // ==============================================
    public long addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();

        v.put(USERNAME, user.getUsername());
        v.put(PASSWORD, user.getPassword());
        v.put(FULLNAME, user.getFullname());

        long id = db.insert(TABLE_USER, null, v);
        db.close();
        return id;
    }

    public User getUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_USER,
                new String[]{USER_ID, USERNAME, FULLNAME, PASSWORD},
                USERNAME + "=? AND " + PASSWORD + "=?",
                new String[]{username, password},
                null, null, null
        );

        User user = null;

        if (cursor.moveToFirst()) {
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

    // ==============================================
    // EXPENSE FUNCTIONS
    // ==============================================
    public long addProduct(Expense product, long userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();

        v.put(EXPENSE_NAME, product.getName());
        v.put(EXPENSE_QUANTITY, product.getQuantity());
        v.put(EXPENSE_PRICE, product.getUnitPrice());
        v.put(EXPENSE_USER_ID, userId);
        v.put(EXPENSE_TIMESTAMP, product.getTimestamp());

        long id = db.insert(TABLE_EXPENSE, null, v);
        db.close();
        return id;
    }

    public ArrayList<Expense> getExpenseByUserId(long userId) {
        ArrayList<Expense> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_EXPENSE,
                new String[]{EXPENSE_ID, EXPENSE_NAME, EXPENSE_QUANTITY, EXPENSE_PRICE, EXPENSE_TIMESTAMP},
                EXPENSE_USER_ID + "=?",
                new String[]{String.valueOf(userId)},
                null,
                null,
                EXPENSE_TIMESTAMP + " DESC"
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(EXPENSE_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(EXPENSE_NAME));
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(EXPENSE_QUANTITY));
                int unitPrice = cursor.getInt(cursor.getColumnIndexOrThrow(EXPENSE_PRICE));
                long timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(EXPENSE_TIMESTAMP));

                Expense expense = new Expense(id, name, quantity, unitPrice, timestamp);
                list.add(expense);

            } while (cursor.moveToNext());

            cursor.close();
        }

        db.close();
        return list;
    }

    public Expense getExpenseById(long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_EXPENSE,
                new String[]{EXPENSE_ID, EXPENSE_NAME, EXPENSE_QUANTITY, EXPENSE_PRICE, EXPENSE_TIMESTAMP},
                EXPENSE_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null
        );

        Expense e = null;

        if (cursor.moveToFirst()) {
            e = new Expense(
                    cursor.getLong(0),
                    cursor.getString(1),
                    cursor.getInt(2),
                    cursor.getInt(3),
                    cursor.getLong(4)
            );
        }

        cursor.close();
        return e;
    }




    // ⭐ NEW: LẤY TẤT CẢ EXPENSE CỦA USER
    public ArrayList<Expense> getAllExpenses(long userId) {
        ArrayList<Expense> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_EXPENSE,
                new String[]{EXPENSE_ID, EXPENSE_NAME, EXPENSE_QUANTITY, EXPENSE_PRICE, EXPENSE_TIMESTAMP},
                EXPENSE_USER_ID + "=?",
                new String[]{String.valueOf(userId)},
                null, null,
                EXPENSE_TIMESTAMP + " DESC"
        );

        if (cursor.moveToFirst()) {
            do {
                list.add(new Expense(
                        cursor.getLong(0),
                        cursor.getString(1),
                        cursor.getInt(2),
                        cursor.getInt(3),
                        cursor.getLong(4)
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public boolean updateProduct(Expense item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();

        v.put(EXPENSE_NAME, item.getName());
        v.put(EXPENSE_QUANTITY, item.getQuantity());
        v.put(EXPENSE_PRICE, item.getUnitPrice());
        v.put(EXPENSE_TIMESTAMP, item.getTimestamp());

        int rows = db.update(
                TABLE_EXPENSE,
                v,
                EXPENSE_ID + "=?",
                new String[]{String.valueOf(item.getId())}
        );

        db.close();
        return rows > 0;
    }

    public boolean removeProductById(long id) {
        SQLiteDatabase db = this.getWritableDatabase();

        // 1) Lấy expense cũ
        Expense oldExpense = getExpenseById(id);
        if (oldExpense != null) {
            // 2) Khôi phục budget
            revertBudgetBeforeDelete(oldExpense);
        }

        // 3) Xóa expense
        int result = db.delete(
                TABLE_EXPENSE,
                EXPENSE_ID + "=?",
                new String[]{String.valueOf(id)}
        );

        db.close();
        return result > 0;
    }


    // ==============================================
    // GET EXPENSE BY MONTH + YEAR
    // ==============================================
    public ArrayList<Expense> getExpensesByMonth(long userId, int month, int year) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Expense> list = new ArrayList<>();

        Calendar c = Calendar.getInstance();
        c.set(year, month - 1, 1, 0, 0, 0);
        long start = c.getTimeInMillis();

        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        long end = c.getTimeInMillis();

        Cursor cursor = db.query(
                TABLE_EXPENSE,
                new String[]{EXPENSE_ID, EXPENSE_NAME, EXPENSE_QUANTITY, EXPENSE_PRICE, EXPENSE_TIMESTAMP},
                EXPENSE_USER_ID + "=? AND " + EXPENSE_TIMESTAMP + " BETWEEN ? AND ?",
                new String[]{String.valueOf(userId), String.valueOf(start), String.valueOf(end)},
                null, null,
                EXPENSE_TIMESTAMP + " DESC"
        );

        if (cursor.moveToFirst()) {
            do {
                list.add(new Expense(
                        cursor.getLong(0),
                        cursor.getString(1),
                        cursor.getInt(2),
                        cursor.getInt(3),
                        cursor.getLong(4)
                ));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return list;
    }

    // ==============================================
    // BUDGET FUNCTIONS
    // ==============================================
    public long addBudget(Budget budget, long userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();

        v.put(BUDGET_NAME, budget.getName());
        v.put(BUDGET_PRICE, budget.getPrice());
        v.put(BUDGET_START_TIMESTAMP, budget.getStartTimestamp());
        v.put(BUDGET_END_TIMESTAMP, budget.getEndTimestamp());

        long id = db.insert(TABLE_BUDGET, null, v);

        // ⭐⭐ TỰ ĐỘNG ÁP DỤNG EXPENSE CŨ VÀ TÍNH LẠI TẤT CẢ BUDGET ⭐⭐
        recalcBudget(userId);

        db.close();
        return id;
    }



    public ArrayList<Budget> getBudgets(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Budget> list = new ArrayList<>();

        Cursor cursor = db.query(
                TABLE_BUDGET,
                new String[]{BUDGET_ID, BUDGET_NAME, BUDGET_PRICE, BUDGET_START_TIMESTAMP, BUDGET_END_TIMESTAMP},
                null, null, null, null,
                BUDGET_START_TIMESTAMP + " DESC"
        );

        if (cursor.moveToFirst()) {
            do {
                list.add(new Budget(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getInt(2),
                        cursor.getLong(3),
                        cursor.getLong(4)
                ));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return list;
    }

    public boolean updateBudget(Budget item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();

        v.put(BUDGET_NAME, item.getName());
        v.put(BUDGET_PRICE, item.getPrice());
        v.put(BUDGET_START_TIMESTAMP, item.getStartTimestamp());
        v.put(BUDGET_END_TIMESTAMP, item.getEndTimestamp());

        int rows = db.update(TABLE_BUDGET, v, BUDGET_ID + "=?", new String[]{String.valueOf(item.getId())});
        db.close();
        return rows > 0;
    }

    public boolean removeBudgettById(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_BUDGET, BUDGET_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return result > 0;
    }

    // ==============================================
    // UPDATE BUDGET AFTER ADDING EXPENSE
    // ==============================================
    public void updateBudgetAfterExpense(Expense expense, long userId) {
        SQLiteDatabase db = this.getWritableDatabase();

        long expTime = expense.getTimestamp();
        int amount = expense.getTotalPrice();

        Cursor cursor = db.rawQuery(
                "SELECT id, price FROM budgets WHERE name = ? AND startTimestamp <= ? AND endTimestamp >= ?",
                new String[]{expense.getName(), String.valueOf(expTime), String.valueOf(expTime)}
        );

        if (cursor.moveToFirst()) {
            int id = cursor.getInt(0);
            int current = cursor.getInt(1);

            int updated = current - amount; // Cho phép âm

            ContentValues cv = new ContentValues();
            cv.put("price", updated);

            db.update("budgets", cv, "id=?", new String[]{String.valueOf(id)});
        }
        cursor.close();
    }

    // ==============================================
    // REVERT (KHÔI PHỤC) BUDGET TRƯỚC KHI EDIT
    // ==============================================
    public void revertBudgetBeforeEdit(Expense oldExpense) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT id, price FROM budgets WHERE name = ? AND startTimestamp <= ? AND endTimestamp >= ?",
                new String[]{oldExpense.getName(), String.valueOf(oldExpense.getTimestamp()), String.valueOf(oldExpense.getTimestamp())}
        );

        if (cursor.moveToFirst()) {
            int id = cursor.getInt(0);
            int current = cursor.getInt(1);

            int updated = current + oldExpense.getTotalPrice(); // Hoàn lại tiền

            ContentValues cv = new ContentValues();
            cv.put("price", updated);

            db.update("budgets", cv, "id=?", new String[]{String.valueOf(id)});
        }

        cursor.close();
    }

    public void recalcBudget(long userId) {
        SQLiteDatabase db = this.getWritableDatabase();

        // 1. Lấy danh sách budgets
        ArrayList<Budget> budgets = getBudgets(userId);

        // 2. Lấy danh sách tất cả expense của user
        ArrayList<Expense> expenses = getAllExpenses(userId);

        // 3. Recalc từng budget
        for (Budget b : budgets) {

            long start = b.getStartTimestamp();
            long end = b.getEndTimestamp();

            int spent = 0;

            for (Expense e : expenses) {
                long time = e.getTimestamp();

                if (time >= start && time <= end) {
                    spent += e.getTotalPrice();
                }
            }

            // Giá mới = giá gốc = price hiện tại + tổng đã tiêu
            int rootPrice = b.getPrice() + spent;

            // Giá còn lại = giá gốc - tổng tiêu
            int newPrice = rootPrice - spent;

            ContentValues cv = new ContentValues();
            cv.put("price", newPrice);

            db.update("budgets", cv, "id=?", new String[]{String.valueOf(b.getId())});
        }
    }

    public void revertBudgetBeforeDelete(Expense expense) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT id, price FROM budgets WHERE name = ? AND startTimestamp <= ? AND endTimestamp >= ?",
                new String[]{
                        expense.getName(),
                        String.valueOf(expense.getTimestamp()),
                        String.valueOf(expense.getTimestamp())
                }
        );

        if (cursor.moveToFirst()) {
            int id = cursor.getInt(0);
            int current = cursor.getInt(1);

            int updated = current + expense.getTotalPrice();  // cộng lại tiền đã tiêu

            ContentValues cv = new ContentValues();
            cv.put("price", updated);

            db.update("budgets", cv, "id=?", new String[]{String.valueOf(id)});
        }

        cursor.close();
    }

    public void applyPastExpensesToBudget(long budgetId, long userId) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Lấy budget vừa tạo
        Cursor c = db.query(
                TABLE_BUDGET,
                new String[]{BUDGET_NAME, BUDGET_PRICE, BUDGET_START_TIMESTAMP, BUDGET_END_TIMESTAMP},
                "id=?",
                new String[]{String.valueOf(budgetId)},
                null, null, null
        );

        if (!c.moveToFirst()) {
            c.close();
            return;
        }

        String name = c.getString(0);
        int price = c.getInt(1);
        long start = c.getLong(2);
        long end = c.getLong(3);

        c.close();

        // Lấy expense khớp name + trong khoảng
        Cursor e = db.rawQuery(
                "SELECT price, quantity FROM expenses " +
                        "WHERE user_id = ? AND name = ? AND timestamp BETWEEN ? AND ?",
                new String[]{String.valueOf(userId), name, String.valueOf(start), String.valueOf(end)}
        );

        int totalSpent = 0;

        if (e.moveToFirst()) {
            do {
                int p = e.getInt(0);
                int q = e.getInt(1);
                totalSpent += p * q;
            } while (e.moveToNext());
        }

        e.close();

        // Trừ ngân sách
        int updated = price - totalSpent;

        ContentValues cv = new ContentValues();
        cv.put("price", updated);

        SQLiteDatabase wdb = this.getWritableDatabase();
        wdb.update("budgets", cv, "id=?", new String[]{String.valueOf(budgetId)});
    }

}
