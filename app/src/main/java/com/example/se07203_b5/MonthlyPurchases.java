package com.example.se07203_b5;

public class MonthlyPurchases {
    public class MonthlyPurchase {
        private long id;
        private long productId;
        private int month;
        private int year;
        private int quantity;
        private int totalPrice;

        public MonthlyPurchase(long id, long productId, int month, int year,
                               int quantity, int totalPrice) {
            this.id = id;
            this.productId = productId;
            this.month = month;
            this.year = year;
            this.quantity = quantity;
            this.totalPrice = totalPrice;
        }

    }
}

