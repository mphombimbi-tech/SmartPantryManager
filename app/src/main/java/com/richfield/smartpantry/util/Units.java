package com.richfield.smartpantry.util;

/** Unit canonicalisation + conversion to a base unit (g, ml or pieces). */
public final class Units {
    public static final int MASS = 0, VOLUME = 1, COUNT = 2;

    private Units() {}

    /** Maps spellings like "Grams", "tablespoon", "cloves" to one of: g kg ml l cup tbsp tsp pcs. */
    public static String canon(String u) {
        if (u == null) return "pcs";
        switch (u.trim().toLowerCase()) {
            case "g": case "gram": case "grams": return "g";
            case "kg": case "kilogram": case "kilograms": return "kg";
            case "ml": case "millilitre": case "milliliter": case "millilitres": case "milliliters": return "ml";
            case "l": case "litre": case "liter": case "litres": case "liters": return "l";
            case "cup": case "cups": return "cup";
            case "tbsp": case "tablespoon": case "tablespoons": return "tbsp";
            case "tsp": case "teaspoon": case "teaspoons": return "tsp";
            default: return "pcs"; // pcs, piece(s), clove(s), can(s), slice(s), pinch, whole ...
        }
    }

    public static int dimension(String unit) {
        switch (canon(unit)) {
            case "g": case "kg": return MASS;
            case "ml": case "l": case "cup": case "tbsp": case "tsp": return VOLUME;
            default: return COUNT;
        }
    }

    /** Quantity expressed in g / ml / pieces. */
    public static double toBase(double qty, String unit) {
        switch (canon(unit)) {
            case "kg": return qty * 1000;
            case "l": return qty * 1000;
            case "cup": return qty * 240;
            case "tbsp": return qty * 15;
            case "tsp": return qty * 5;
            default: return qty;
        }
    }
}
