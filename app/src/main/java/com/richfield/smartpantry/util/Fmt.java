package com.richfield.smartpantry.util;

import com.richfield.smartpantry.model.RecipeIngredient;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public final class Fmt {
    private static final DecimalFormat DF = new DecimalFormat("0.##", DecimalFormatSymbols.getInstance(Locale.US));

    private Fmt() {}

    public static String qty(double q) { return DF.format(q); }

    public static String title(String s) {
        if (s == null || s.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (String w : s.trim().split("\\s+")) {
            if (sb.length() > 0) sb.append(' ');
            sb.append(Character.toUpperCase(w.charAt(0))).append(w.substring(1));
        }
        return sb.toString();
    }

    public static String amount(double q, String unit) {
        return "pcs".equals(unit) ? qty(q) : qty(q) + " " + unit;
    }

    public static String ingredientLine(RecipeIngredient i) {
        if (i.getQuantity() <= 0) return title(i.getName()) + " (to taste)";
        return amount(i.getQuantity(), i.getUnit()) + "  " + title(i.getName());
    }
}
