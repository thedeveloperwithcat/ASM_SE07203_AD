package com.example.se07203_b5;

public class MonthlyPurchase {
    private long id;
    private long productId;
    private int month;
    private int year;
    private int quantity;
    private int totalPrice;
    private String productName; // Thêm tên sản phẩm để hiển thị

    public MonthlyPurchase(long id, long productId, int month, int year,
                           int quantity, int totalPrice, String productName) {
        this.id = id;
        this.productId = productId;
        this.month = month;
        this.year = year;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.productName = productName;
    }

    public long getId() { return id; }
    public long getProductId() { return productId; }
    public int getMonth() { return month; }
    public int getYear() { return year; }
    public int getQuantity() { return quantity; }
    public int getTotalPrice() { return totalPrice; }
    public String getProductName() { return productName; }

    @Override
    public String toString() {
        return productName + " - SL: " + quantity + " - Tổng tiền: " + totalPrice;
    }
}