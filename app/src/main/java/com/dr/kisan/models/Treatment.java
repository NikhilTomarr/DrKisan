package com.dr.kisan.models;

public class Treatment {
    private int id;
    private String disease;
    private String pesticide;
    private String dosage;
    private String applicationMethod;
    private String precautions;
    private boolean isOrganic;
    private String price;
    private String availability;

    public Treatment() {
        // Default constructor
    }

    public Treatment(int id, String disease, String pesticide, String dosage,
                     String applicationMethod, String precautions, boolean isOrganic) {
        this.id = id;
        this.disease = disease;
        this.pesticide = pesticide;
        this.dosage = dosage;
        this.applicationMethod = applicationMethod;
        this.precautions = precautions;
        this.isOrganic = isOrganic;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDisease() { return disease; }
    public void setDisease(String disease) { this.disease = disease; }

    public String getPesticide() { return pesticide; }
    public void setPesticide(String pesticide) { this.pesticide = pesticide; }

    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }

    public String getApplicationMethod() { return applicationMethod; }
    public void setApplicationMethod(String applicationMethod) { this.applicationMethod = applicationMethod; }

    public String getPrecautions() { return precautions; }
    public void setPrecautions(String precautions) { this.precautions = precautions; }

    public boolean isOrganic() { return isOrganic; }
    public void setOrganic(boolean organic) { isOrganic = organic; }

    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }

    public String getAvailability() { return availability; }
    public void setAvailability(String availability) { this.availability = availability; }
}
