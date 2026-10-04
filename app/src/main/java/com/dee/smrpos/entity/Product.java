package com.dee.smrpos.entity;

public class Product {
    private int id;
    private String name;
    private double price;
    private String imageName;

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getImageName() { return imageName; }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}


