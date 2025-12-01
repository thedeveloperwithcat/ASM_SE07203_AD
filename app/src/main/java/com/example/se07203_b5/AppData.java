package com.example.se07203_b5;

import android.os.Bundle;

import java.util.ArrayList;

public abstract class AppData {
    static boolean isLogin = false;
    static ArrayList<String> ListTask = new ArrayList<String>();

    static ArrayList<Item> ListItem = new ArrayList<Item>();

    static final int EDIT_TASK = 1;

    static int getTotalBill(){
        int totalBill = 0;
        for (int i = 0; i < ListItem.size(); i++) {
            totalBill += ListItem.get(i).getQuantity() * ListItem.get(i).getUnitPrice();
        }
        return totalBill;
    }

    protected abstract void onCreate(Bundle savedInstanceState);
// update thành công thì mới vào appdata và nó là nơi trung gian để sử dụng
}
