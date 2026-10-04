package com.dee.smrpos.entity;

public class CartItem {

    public int id;
    public String name;
    public double price;
    public int quantity;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }
//    public CartItem(int id, String name, double price, int quantity) {
//        this.id = id;
//        this.name = name;
//        this.price = price;
//        this.quantity = quantity;
//    }
    public double getTotal() {
        return price * quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

}
