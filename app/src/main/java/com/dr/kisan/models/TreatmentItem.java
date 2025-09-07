package com.dr.kisan.models;

public class TreatmentItem {
    private String name;
    private String description;
    private String price;
    private String category;
    private boolean isOrganic;
    private String manufacturer;

    public TreatmentItem(String name, String description, String price, String category, boolean isOrganic, String manufacturer) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.isOrganic = isOrganic;
        this.manufacturer = manufacturer;
    }

    // Getters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getPrice() { return price; }
    public String getCategory() { return category; }
    public boolean isOrganic() { return isOrganic; }
    public String getManufacturer() { return manufacturer; }
}
