package com.example.se07203_b5.Models;

import androidx.annotation.NonNull;

public class Expense {
    private int id;
    private String itemName;
    private int quantity;
    private int unitPrice;
    private long timestamp;
    private long userId;
    private int budgetId;

    // Constructors

    // Dành cho tạo mới
    public Expense(String itemName, int quantity, int unitPrice, long timestamp, long userId, int budgetId) {
        this.itemName = itemName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.timestamp = timestamp;
        this.userId = userId;
        this.budgetId = budgetId;
    }

    // Dành cho update
    public Expense(int id, String itemName, int quantity, int unitPrice, long timestamp, long userId, int budgetId) {
        this.id = id;
        this.itemName = itemName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.timestamp = timestamp;
        this.userId = userId;
        this.budgetId = budgetId;
    }


    // toString
    @NonNull
    @Override
    public String toString() {
        return "Expense: " + itemName +
                " - Quantity: " + quantity +
                " - Price: " + unitPrice + " vnd";
    }

    // GETTERS & SETTERS
    public int getId() { return id; }
    public String getName() { return itemName; }
    public int getQuantity() { return quantity; }
    public int getUnitPrice() { return unitPrice; }
    public long getTimestamp() { return timestamp; }
    public int getTotalPrice() {
        return quantity * unitPrice;
    }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.itemName = name; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setUnitPrice(int price) { this.unitPrice = price; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public long getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getBudgetId() { return budgetId; }
    public void setBudgetId(int budgetId) { this.budgetId = budgetId; }
}
