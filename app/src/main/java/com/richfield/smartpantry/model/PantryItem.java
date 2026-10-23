package com.richfield.smartpantry.model;

/** One ingredient the user currently has at home. */
public class PantryItem {
    private long id;
    private String name;
    private double quantity;
    private String unit;
    private String expiry; // ISO date yyyy-MM-dd, or null when not set

    public PantryItem(long id, String name, double quantity, String unit, String expiry) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.expiry = expiry;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getName() { return name; }
    public double getQuantity() { return quantity; }
    public String getUnit() { return unit; }
    public String getExpiry() { return expiry; }
}
