package com.example.smartpantrymanager;

public class Ingredient {
    public final long id;
    public final String name, unit, expiry;
    public final double quantity;
    public Ingredient(long id, String name, double quantity, String unit, String expiry) {
        this.id = id; this.name = name; this.quantity = quantity;
        this.unit = unit; this.expiry = expiry;
    }
    public String amount() { return String.format(java.util.Locale.getDefault(), "%s %s", new java.math.BigDecimal(Double.toString(quantity)).stripTrailingZeros().toPlainString(), unit); }
}
