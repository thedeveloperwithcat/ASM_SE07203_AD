package com.example.se07203_b5.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.se07203_b5.Models.Budget;
import com.example.se07203_b5.Models.Expense;
import com.example.se07203_b5.Models.RecurringExpense; // Đừng quên import Model mới
import com.example.se07203_b5.Models.User;

import java.util.ArrayList;
import java.util.Calendar;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SE07203Expense";

    private static final int DATABASE_VERSION = 24;


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
    private static final String EXPENSE_BUDGET_ID = "budget_id";
    private static final String EXPENSE_TIMESTAMP = "timestamp";

    // ==============================================
    // BUDGET TABLE
    // ==============================================
    public static final String TABLE_BUDGET = "budgets";
    public static final String BUDGET_ID = "id";
    public static final String BUDGET_NAME = "name";
    public static final String BUDGET_PRICE = "price";
    private static final String BUDGET_USER_ID = "user_id";
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

        db.execSQL("CREATE TABLE " + TABLE_BUDGET + "("
                + BUDGET_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BUDGET_NAME + " TEXT NOT NULL, "
                + BUDGET_PRICE + " INTEGER NOT NULL, "
                + BUDGET_START_TIMESTAMP + " INTEGER NOT NULL, "
                + BUDGET_END_TIMESTAMP + " INTEGER NOT NULL, "
                + BUDGET_USER_ID + " INTEGER NOT NULL)"
        );


        // (MỚI) Tạo bảng Recurring Expenses
        db.execSQL("CREATE TABLE " + TABLE_RECURRING + "("
                + REC_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + REC_NAME + " TEXT, "
                + REC_AMOUNT + " REAL, "
                + REC_FREQUENCY + " TEXT, "
                + REC_NEXT_DUE + " INTEGER);"
        );


        db.execSQL("CREATE TABLE " + TABLE_EXPENSE + "("
                + EXPENSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + EXPENSE_NAME + " TEXT NOT NULL, "
                + EXPENSE_PRICE + " INTEGER NOT NULL, "
                + EXPENSE_QUANTITY + " INTEGER NOT NULL, "
                + EXPENSE_TIMESTAMP + " INTEGER NOT NULL, "
                + EXPENSE_BUDGET_ID + " INTEGER NOT NULL, "
                + EXPENSE_USER_ID + " INTEGER NOT NULL, "
                + "FOREIGN KEY (" + EXPENSE_BUDGET_ID + ") REFERENCES " + TABLE_BUDGET + "(" + BUDGET_ID + ") ON DELETE CASCADE)"
        );


        Log.d("DB", "Database created successfully!");


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








    // ==============================================
    // EXPENSE FUNCTIONS
    // ==============================================
    public boolean addExpense(Expense e, int budgetId, long userId) {


        SQLiteDatabase db = this.getWritableDatabase();

        // ==========================
        // 1. Lấy thông tin Budget
        // ==========================
        Cursor c = db.rawQuery(
                "SELECT " + BUDGET_PRICE + ", " + BUDGET_START_TIMESTAMP + ", " + BUDGET_END_TIMESTAMP +
                        " FROM " + TABLE_BUDGET +
                        " WHERE " + BUDGET_ID + " = ?",
                new String[]{String.valueOf(budgetId)}
        );

        if (!c.moveToFirst()) {
            c.close();
            db.close();
            return false; // Budget không tồn tại
        }

        int remaining = c.getInt(0);
        long startTime = c.getLong(1);
        long endTime = c.getLong(2);
        c.close();

        long expenseTime = e.getTimestamp();

        // ==========================
        // 1) Check TIME RANGE FIRST
        // ==========================
        if (expenseTime < startTime || expenseTime > endTime) {
            db.close();
            return false; // Không nằm trong khoảng thời gian
        }

        // ==========================
        // 2) Check MONEY SECOND
        // ==========================
        int spend = e.getTotalPrice();
        if (spend > remaining) {
            db.close();
            return false; // Vượt quá ngân sách còn lại
        }

        // ==========================
        // 3) INSERT EXPENSE
        // ==========================
        ContentValues v = new ContentValues();
        v.put(EXPENSE_NAME, e.getName());
        v.put(EXPENSE_QUANTITY, e.getQuantity());
        v.put(EXPENSE_PRICE, e.getUnitPrice());
        v.put(EXPENSE_TIMESTAMP, e.getTimestamp());
        v.put(EXPENSE_USER_ID, userId);
        v.put(EXPENSE_BUDGET_ID, budgetId);

        long id = db.insert(TABLE_EXPENSE, null, v);
        if (id <= 0) {
            db.close();
            return false;
        }

        // ==========================
        // 4) TRỪ NGÂN SÁCH
        // ==========================
        db.execSQL(
                "UPDATE " + TABLE_BUDGET +
                        " SET " + BUDGET_PRICE + " = " + BUDGET_PRICE + " - ? " +
                        " WHERE " + BUDGET_ID + " = ?",
                new Object[]{spend, budgetId}
        );

        db.close();
        return true;
    }

    public ArrayList<Expense> getAllExpenses(long userId) {
        ArrayList<Expense> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_EXPENSE,
                new String[]{EXPENSE_ID, EXPENSE_NAME, EXPENSE_QUANTITY, EXPENSE_PRICE, EXPENSE_TIMESTAMP, EXPENSE_USER_ID, EXPENSE_BUDGET_ID},
                EXPENSE_USER_ID + "=?",
                new String[]{String.valueOf(userId)},
                null, null,
                EXPENSE_TIMESTAMP + " DESC"
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Expense expense = new Expense(
                        cursor.getInt(0),       // id
                        cursor.getString(1),    // name
                        cursor.getInt(2),       // quantity
                        cursor.getInt(3),       // price
                        cursor.getLong(4),      // timestamp
                        cursor.getLong(5),      // userId
                        cursor.getInt(6)        // budgetId
                );
                list.add(expense);
            } while (cursor.moveToNext());
            cursor.close();
        }

        db.close();
        return list;
    }

    public ArrayList<Expense> getExpenseByUserId(long userId) {
        ArrayList<Expense> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_EXPENSE,
                new String[]{EXPENSE_ID, EXPENSE_NAME, EXPENSE_QUANTITY, EXPENSE_PRICE, EXPENSE_TIMESTAMP, EXPENSE_USER_ID, EXPENSE_BUDGET_ID},
                EXPENSE_USER_ID + "=?",
                new String[]{String.valueOf(userId)},
                null,
                null,
                EXPENSE_TIMESTAMP + " DESC"
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Expense expense = new Expense(
                        cursor.getInt(cursor.getColumnIndexOrThrow(EXPENSE_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(EXPENSE_NAME)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(EXPENSE_QUANTITY)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(EXPENSE_PRICE)),
                        cursor.getLong(cursor.getColumnIndexOrThrow(EXPENSE_TIMESTAMP)),
                        cursor.getLong(cursor.getColumnIndexOrThrow(EXPENSE_USER_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(EXPENSE_BUDGET_ID))
                );
                list.add(expense);
            } while (cursor.moveToNext());
            cursor.close();
        }


        db.close(); // đóng để check data trong inspector
        return list;
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
                new String[]{EXPENSE_ID, EXPENSE_NAME, EXPENSE_QUANTITY, EXPENSE_PRICE, EXPENSE_TIMESTAMP, EXPENSE_USER_ID, EXPENSE_BUDGET_ID},
                EXPENSE_USER_ID + "=? AND " + EXPENSE_TIMESTAMP + " BETWEEN ? AND ?",
                new String[]{String.valueOf(userId), String.valueOf(start), String.valueOf(end)},
                null, null,
                EXPENSE_TIMESTAMP + " DESC"
        );

        if (cursor.moveToFirst()) {
            do {
                list.add(new Expense(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getInt(2),
                        cursor.getInt(3),
                        cursor.getLong(4),
                        cursor.getLong(5),
                        cursor.getInt(6)
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
//        db.close();
        return list;
    }


    public boolean updateExpense(Expense newExpense, int newBudgetId, long userId) {

        SQLiteDatabase db = this.getWritableDatabase();

        // 1) Lấy expense cũ từ DB
        Cursor c = db.rawQuery(
                "SELECT quantity, price, budget_id, timestamp FROM expenses WHERE id = ?",
                new String[]{String.valueOf(newExpense.getId())}
        );

        if (!c.moveToFirst()) {
            c.close();
            return false;
        }

        int oldQuantity = c.getInt(0);
        int oldUnitPrice = c.getInt(1);
        int oldBudgetId = c.getInt(2);
        long oldTimestamp = c.getLong(3);
        c.close();

        int oldTotal = oldQuantity * oldUnitPrice;
        int newTotal = newExpense.getQuantity() * newExpense.getUnitPrice();
        long newTime = newExpense.getTimestamp();


        // 2) Lấy thông tin budget mới (luôn cần)
        Cursor bc = db.rawQuery(
                "SELECT price, startTimestamp, endTimestamp FROM budgets WHERE id = ?",
                new String[]{String.valueOf(newBudgetId)}
        );

        if (!bc.moveToFirst()) {
            bc.close();
            return false;
        }

        int remainingNewBudget = bc.getInt(0);
        long startTime = bc.getLong(1);
        long endTime = bc.getLong(2);
        bc.close();


        // ================================
        // TRƯỜNG HỢP 1: UPDATE TRONG CÙNG 1 BUDGET
        // ================================
        if (newBudgetId == oldBudgetId) {

            // check khoảng thời gian mới có nằm trong range của budget không
            if (newTime < startTime || newTime > endTime) {
                return false;
            }

            // remaining = remainingReal + oldTotal (vì trước đó đã trừ)
            int remainingReal = remainingNewBudget + oldTotal;

            // check tiền
            if (newTotal > remainingReal) {
                return false; // vượt quá budget
            }

            // cập nhật lại budget mới (cùng budget)
            int finalRemaining = remainingReal - newTotal;

            db.execSQL("UPDATE budgets SET price = ? WHERE id = ?",
                    new Object[]{finalRemaining, newBudgetId});

        } else {
            // ================================
            // TRƯỜNG HỢP 2: CHUYỂN SANG BUDGET KHÁC
            // ================================

            // 2.1 trả lại tiền cho budget cũ
            db.execSQL(
                    "UPDATE budgets SET price = price + ? WHERE id = ?",
                    new Object[]{oldTotal, oldBudgetId}
            );

            // check time
            if (newTime < startTime || newTime > endTime) {
                return false;
            }

            // check money
            if (newTotal > remainingNewBudget) {
                return false;
            }

            // trừ budget mới
            db.execSQL(
                    "UPDATE budgets SET price = price - ? WHERE id = ?",
                    new Object[]{newTotal, newBudgetId}
            );
        }


        // 3) Update expense
        ContentValues v = new ContentValues();
        v.put(EXPENSE_NAME, newExpense.getName());
        v.put(EXPENSE_QUANTITY, newExpense.getQuantity());
        v.put(EXPENSE_PRICE, newExpense.getUnitPrice());
        v.put(EXPENSE_TIMESTAMP, newExpense.getTimestamp());
        v.put(EXPENSE_BUDGET_ID, newBudgetId);

        int result = db.update(TABLE_EXPENSE, v, "id=?",
                new String[]{String.valueOf(newExpense.getId())});

        return result > 0;
    }

    public boolean removeExpenseById(long id) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Lấy lại expense trước khi xoá
        Cursor c = db.rawQuery(
                "SELECT quantity, price, budget_id FROM expenses WHERE id = ?",
                new String[]{String.valueOf(id)}
        );

        if (!c.moveToFirst()) {
            c.close();
            db.close();
            return false;
        }

        int quantity = c.getInt(0);
        int unitPrice = c.getInt(1);
        int budgetId = c.getInt(2);
        c.close();

        int totalReturnMoney = quantity * unitPrice;

        // Hoàn tiền cho Budget
        db.execSQL("UPDATE budgets SET price = price + ? WHERE id = ?",
                new Object[]{totalReturnMoney, budgetId});

        // Xoá expense
        boolean success = db.delete(TABLE_EXPENSE, "id=?", new String[]{String.valueOf(id)}) > 0;

        db.close();
        return success;
    }


    // ==============================================
    // BUDGET FUNCTIONS
    // ==============================================
    public boolean addBudget(Budget budget, long userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();

        v.put(BUDGET_NAME, budget.getName());
        v.put(BUDGET_PRICE, budget.getPrice());
        v.put(BUDGET_START_TIMESTAMP, budget.getStartTimestamp());
        v.put(BUDGET_END_TIMESTAMP, budget.getEndTimestamp());
        v.put(BUDGET_USER_ID, userId); // 👈 Thêm dòng này

        boolean ok = db.insert(TABLE_BUDGET, null, v) > 0;

        db.close();
        return ok;
    }


    public ArrayList<Budget> getBudgetByUserId(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Budget> list = new ArrayList<>();

        Cursor cursor = db.query(
                TABLE_BUDGET,
                new String[]{BUDGET_ID, BUDGET_NAME, BUDGET_PRICE, BUDGET_START_TIMESTAMP, BUDGET_END_TIMESTAMP, BUDGET_USER_ID},
                BUDGET_USER_ID + "=?",
                new String[]{String.valueOf(userId)},
                null, null,
                BUDGET_START_TIMESTAMP + " DESC"
        );

        if (cursor.moveToFirst()) {
            do {
                list.add(new Budget(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getInt(2),
                        cursor.getLong(3),
                        cursor.getLong(4),
                        cursor.getInt(5)
                ));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return list;
    }

    public boolean removeBudgettById(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean ok = db.delete(TABLE_BUDGET, BUDGET_ID + "=?", new String[]{String.valueOf(id)}) > 0;
        db.close();
        return ok;
    }


    public ArrayList<Budget> getAllBudgets(long userId) {
        ArrayList<Budget> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery("SELECT * FROM budgets WHERE user_id = ?",
                new String[]{String.valueOf(userId)});

        if (c.moveToFirst()) {
            do {
                Budget b = new Budget(
                        c.getInt(0),   // id
                        c.getString(1),// name
                        c.getInt(2),   // price
                        c.getLong(3),  // start
                        c.getLong(4),  // end
                        c.getInt(5)    // user_id
                );
                list.add(b);
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return list;
    }

    //  barchart
    public int getTotalExpenseOfMonth(long userId, int month, int year) {
        SQLiteDatabase db = this.getReadableDatabase();
        int total = 0;

        String start = month + "/1/" + year;
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, 1);
        long startTime = cal.getTimeInMillis();

        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        long endTime = cal.getTimeInMillis();

        String query = "SELECT SUM(price) FROM expenses WHERE user_id=? AND timestamp BETWEEN ? AND ?";
        Cursor cursor = db.rawQuery(query, new String[]{
                String.valueOf(userId),
                String.valueOf(startTime),
                String.valueOf(endTime)
        });

        if (cursor.moveToFirst()) {
            total = cursor.getInt(0);
        }
        cursor.close();
        return total;
    }

    //Piechart
    public int getTotalBudgetOfMonth(long userId, int month, int year) {
        long startMonth = getStartOfMonthTimestamp(month, year);
        long endMonth = getEndOfMonthTimestamp(month, year);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(price) FROM budgets WHERE user_id=? AND startTimestamp BETWEEN ? AND ?",
                new String[]{String.valueOf(userId), String.valueOf(startMonth), String.valueOf(endMonth)}
        );

        int total = 0;
        if (cursor.moveToFirst()) total = cursor.getInt(0);

        cursor.close();
        return total;
    }
    public long getStartOfMonthTimestamp(int month, int year) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    public long getEndOfMonthTimestamp(int month, int year) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTimeInMillis();
    }


    // thông báo
    public long getTotalExpenseInRange(long userId, long startTime, long endTime) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(" + EXPENSE_PRICE + " * " + EXPENSE_QUANTITY + ") FROM " + TABLE_EXPENSE +
                        " WHERE " + EXPENSE_USER_ID + " = ? AND " + EXPENSE_TIMESTAMP + " BETWEEN ? AND ?",
                new String[]{String.valueOf(userId), String.valueOf(startTime), String.valueOf(endTime)}
        );

        long total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getLong(0);
        }
        cursor.close();
        return total;
    }

    // Hàm lấy tổng số dư hiện tại của tất cả ngân sách (cho báo cáo tuần)
    public long getTotalRemainingBudget(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(" + BUDGET_PRICE + ") FROM " + TABLE_BUDGET + " WHERE " + BUDGET_USER_ID + " = ?",
                new String[]{String.valueOf(userId)}
        );

        long total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getLong(0);
        }
        cursor.close();
        return total;
    }

    // Hàm tính % đã chi tiêu của một Budget cụ thể (cho cảnh báo 80%)
    public float getBudgetUsagePercentage(int budgetId) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Lấy số dư hiện tại
        Cursor cBudget = db.rawQuery("SELECT " + BUDGET_PRICE + " FROM " + TABLE_BUDGET + " WHERE " + BUDGET_ID + " = ?", new String[]{String.valueOf(budgetId)});
        if (!cBudget.moveToFirst()) { cBudget.close(); return 0; }
        long currentRemaining = cBudget.getLong(0);
        cBudget.close();

        // Lấy tổng tiền đã chi cho budget này
        Cursor cExpense = db.rawQuery(
                "SELECT SUM(" + EXPENSE_PRICE + " * " + EXPENSE_QUANTITY + ") FROM " + TABLE_EXPENSE + " WHERE " + EXPENSE_BUDGET_ID + " = ?",
                new String[]{String.valueOf(budgetId)}
        );
        long totalSpent = 0;
        if (cExpense.moveToFirst()) {
            totalSpent = cExpense.getLong(0);
        }
        cExpense.close();

        // Tổng ban đầu = Số dư hiện tại + Đã chi
        long originalTotal = currentRemaining + totalSpent;

        if (originalTotal == 0) return 0;

        return ((float) totalSpent / originalTotal) * 100;
    }

    public boolean isUsernameExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM users WHERE username = ?", new String[]{username});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }
}

