package com.example.se07203_b5.Models;

public class RecurringExpense {
    private int id;
    private String name;
    private double amount;
    private String frequency;
    private long nextDueDate;

    public RecurringExpense(String name, double amount, String frequency, long nextDueDate) {
        this.name = name;
        this.amount = amount;
        this.frequency = frequency;
        this.nextDueDate = nextDueDate;
    }

    public RecurringExpense(int id, String name, double amount, String frequency, long nextDueDate) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.frequency = frequency;
        this.nextDueDate = nextDueDate;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public double getAmount() { return amount; }
    public String getFrequency() { return frequency; }
    public long getNextDueDate() { return nextDueDate; }
}