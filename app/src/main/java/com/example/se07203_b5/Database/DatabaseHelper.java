package com.example.se07203_b5.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.se07203_b5.Models.Budget;
import com.example.se07203_b5.Models.Expense;
import com.example.se07203_b5.Models.RecurringExpense; // Đừng quên import Model mới
import com.example.se07203_b5.Models.User;

import java.util.ArrayList;
import java.util.Calendar;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SE07203Expense";

    // ⭐ CẬP NHẬT VERSION LÊN 12 ĐỂ TẠO BẢNG MỚI
    private static final int DATABASE_VERSION = 12;

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

    // ==============================================
    // (MỚI) RECURRING EXPENSE TABLE
    // ==============================================
    private static final String TABLE_RECURRING = "recurring_expenses";
    private static final String REC_ID = "id";
    private static final String REC_NAME = "name";
    private static final String REC_AMOUNT = "amount";
    private static final String REC_FREQUENCY = "frequency";
    private static final String REC_NEXT_DUE = "next_due_date";

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

        // (MỚI) Tạo bảng Recurring Expenses
        db.execSQL("CREATE TABLE " + TABLE_RECURRING + "("
                + REC_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + REC_NAME + " TEXT, "
                + REC_AMOUNT + " REAL, "
                + REC_FREQUENCY + " TEXT, "
                + REC_NEXT_DUE + " INTEGER);"
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

        // (MỚI) Drop bảng Recurring nếu tồn tại
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECURRING);

        onCreate(db);
    }

    // ==============================================
    // (MỚI) RECURRING EXPENSE FUNCTIONS
    // ==============================================

    // Thêm mới khoản chi định kỳ
    public boolean addRecurringExpense(RecurringExpense item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();

        v.put(REC_NAME, item.getName());
        v.put(REC_AMOUNT, item.getAmount());
        v.put(REC_FREQUENCY, item.getFrequency());
        v.put(REC_NEXT_DUE, item.getNextDueDate());

        long result = db.insert(TABLE_RECURRING, null, v);
        db.close();
        return result != -1;
    }

    // Lấy danh sách tất cả khoản chi định kỳ
    public ArrayList<RecurringExpense> getAllRecurringExpenses() {
        ArrayList<RecurringExpense> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Sắp xếp theo ngày đến hạn tăng dần
        Cursor cursor = db.query(TABLE_RECURRING, null, null, null, null, null, REC_NEXT_DUE + " ASC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(REC_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(REC_NAME));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(REC_AMOUNT));
                String freq = cursor.getString(cursor.getColumnIndexOrThrow(REC_FREQUENCY));
                long nextDue = cursor.getLong(cursor.getColumnIndexOrThrow(REC_NEXT_DUE));

                list.add(new RecurringExpense(id, name, amount, freq, nextDue));
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return list;
    }

    // ==============================================
    // CÁC HÀM CŨ (USER, EXPENSE, BUDGET) GIỮ NGUYÊN
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

    // Thêm phương thức getUser cũ để tránh lỗi compile nếu MainActivity dùng
    public User getUserByUsernameAndPassword(String username, String password) {
        return getUser(username, password);
    }

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

        Expense oldExpense = getExpenseById(id);
        if (oldExpense != null) {
            revertBudgetBeforeDelete(oldExpense);
        }

        int result = db.delete(
                TABLE_EXPENSE,
                EXPENSE_ID + "=?",
                new String[]{String.valueOf(id)}
        );

        db.close();
        return result > 0;
    }

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

    public long addBudget(Budget budget, long userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();

        v.put(BUDGET_NAME, budget.getName());
        v.put(BUDGET_PRICE, budget.getPrice());
        v.put(BUDGET_START_TIMESTAMP, budget.getStartTimestamp());
        v.put(BUDGET_END_TIMESTAMP, budget.getEndTimestamp());

        long id = db.insert(TABLE_BUDGET, null, v);
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
            int updated = current - amount;

            ContentValues cv = new ContentValues();
            cv.put("price", updated);

            db.update("budgets", cv, "id=?", new String[]{String.valueOf(id)});
        }
        cursor.close();
    }

    public void revertBudgetBeforeEdit(Expense oldExpense) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT id, price FROM budgets WHERE name = ? AND startTimestamp <= ? AND endTimestamp >= ?",
                new String[]{oldExpense.getName(), String.valueOf(oldExpense.getTimestamp()), String.valueOf(oldExpense.getTimestamp())}
        );

        if (cursor.moveToFirst()) {
            int id = cursor.getInt(0);
            int current = cursor.getInt(1);
            int updated = current + oldExpense.getTotalPrice();

            ContentValues cv = new ContentValues();
            cv.put("price", updated);

            db.update("budgets", cv, "id=?", new String[]{String.valueOf(id)});
        }
        cursor.close();
    }

    public void recalcBudget(long userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Budget> budgets = getBudgets(userId);
        ArrayList<Expense> expenses = getAllExpenses(userId);

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

            // Lưu ý: Logic này giả định price là số dư hiện tại, cần cộng lại spent để ra gốc rồi trừ đi
            // Tuy nhiên để đơn giản và tránh lỗi cộng dồn, ta nên có cột OriginalPrice riêng.
            // Ở code cũ của bạn: rootPrice = currentPrice + spent (sai nếu currentPrice đã bị trừ nhiều lần không đúng)
            // Tạm giữ nguyên logic cũ của bạn để tránh phá vỡ app hiện tại.

            int currentPriceInDb = b.getPrice();
            // Nếu logic cũ của bạn hoạt động ổn thì giữ nguyên,
            // nhưng logic "recalc" này hơi rủi ro nếu chạy nhiều lần mà không có giá trị gốc.
            // Code cũ:
            int rootPrice = b.getPrice() + spent;
            int newPrice = rootPrice - spent;

            // Thực tế: newPrice chính là rootPrice - spent.
            // Nếu b.getPrice() đang là số dư, thì cộng lại spent ra số gốc.
            // Sau đó lấy số gốc trừ spent ra số dư mới. -> Kết quả vẫn là b.getPrice().
            // Hàm này có vẻ chưa hiệu quả lắm trong code gốc nhưng tôi giữ nguyên để không gây lỗi logic cũ.

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
            int updated = current + expense.getTotalPrice();

            ContentValues cv = new ContentValues();
            cv.put("price", updated);

            db.update("budgets", cv, "id=?", new String[]{String.valueOf(id)});
        }
        cursor.close();
    }
}