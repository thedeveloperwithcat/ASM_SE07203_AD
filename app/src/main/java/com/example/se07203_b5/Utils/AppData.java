package com.example.se07203_b5.Utils;

import com.example.se07203_b5.Models.Budget;
import com.example.se07203_b5.Models.Expense;

import java.util.ArrayList;

public class AppData {
    public static boolean isLogin = false;
    public static ArrayList<String> ListTask = new ArrayList<String>();

    public static ArrayList<Expense> ListItemExpense = new ArrayList<Expense>();
    public static ArrayList<Budget> ListItemBudget = new ArrayList<Budget>();

    public static final int EDIT_TASK = 1;

    public static int getTotalBill(){
        int totalBill = 0;
        for (int i = 0; i < ListItemExpense.size(); i++) {
            totalBill += ListItemExpense.get(i).getQuantity() * ListItemExpense.get(i).getUnitPrice();
        }
        return totalBill;
    }
// update thành công thì mới vào appdata và nó là nơi trung gian để sử dụng
}
