package com.example.se07203_b5.Models;

import androidx.annotation.NonNull;

public class Budget {
    private int id;
    private String name;
    private int price;
    private long startTimestamp;
    private long endTimestamp;
    private long userId;

    public Budget() {}

    public Budget(int id, String name, int price, long startTimestamp, long endTimestamp, long userId) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        this.userId = userId;
    }

    public Budget(String name, int price, long startTimestamp, long endTimestamp,long userId) {
        this.name = name;
        this.price = price;
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        this.userId = userId;
    }

    @NonNull
    @Override
    public String toString() {
        return "Budget: " + name + " - Price: " + price + " vnd";
    }

    // GETTER - SETTER

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }

    public long getStartTimestamp() { return startTimestamp; }
    public void setStartTimestamp(long startTimestamp) { this.startTimestamp = startTimestamp; }

    public long getEndTimestamp() { return endTimestamp; }
    public void setEndTimestamp(long endTimestamp) { this.endTimestamp = endTimestamp; }

    public long getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
}
