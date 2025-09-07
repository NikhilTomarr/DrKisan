package com.dr.kisan.models;

public class Product {
    private String name;
    private String description;
    private String price;
    private boolean isOrganic;
    private String manufacturer;

    public Product(String name, String description, String price, boolean isOrganic, String manufacturer) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.isOrganic = isOrganic;
        this.manufacturer = manufacturer;
    }

    // Getters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getPrice() { return price; }
    public boolean isOrganic() { return isOrganic; }
    public String getManufacturer() { return manufacturer; }
}
