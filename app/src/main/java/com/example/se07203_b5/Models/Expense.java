package com.example.se07203_b5.Models;

import androidx.annotation.NonNull;

public class Expense {
    private long id;
    private String itemName;
    private int quantity;
    private int unitPrice;
    private long timestamp; // epoch millis – ngày chi tiêu

    // Constructors

    public Expense(String itemName, int quantity, int unitPrice, long timestamp) {
        this.itemName = itemName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.timestamp = timestamp;
    }

    public Expense(long id, String name, int quantity, int unitPrice, long timestamp) {
        this.id = id;
        this.itemName = name;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.timestamp = timestamp;
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
    public long getId() { return id; }
    public String getName() { return itemName; }
    public int getQuantity() { return quantity; }
    public int getUnitPrice() { return unitPrice; }
    public long getTimestamp() { return timestamp; }
    public int getTotalPrice() {
        return quantity * unitPrice;
    }


    public void setName(String name) { this.itemName = name; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setUnitPrice(int price) { this.unitPrice = price; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
