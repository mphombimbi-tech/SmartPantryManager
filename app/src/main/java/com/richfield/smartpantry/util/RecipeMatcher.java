package com.richfield.smartpantry.util;

import com.richfield.smartpantry.model.PantryItem;
import com.richfield.smartpantry.model.Recipe;
import com.richfield.smartpantry.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * THE STRICT-MATCHING RULE (Assignment section 2.3).
 *
 * A recipe is "suggested" ONLY IF every single ingredient it needs is in the pantry
 * in at least the required quantity. One missing or insufficient ingredient = excluded.
 *
 * Robustness:
 *  - names are normalised ("Tomatoes" == "tomato")
 *  - several pantry rows of the same ingredient are added together
 *  - units are converted (kg->g, l/cup/tbsp/tsp->ml); mass and volume are treated as
 *    interchangeable at ~1 g = 1 ml; piece counts never convert to g/ml
 *  - "to taste" ingredients (quantity 0) only need to be present
 */
public final class RecipeMatcher {
    private RecipeMatcher() {}

    public static class Result {
        public final Recipe recipe;
        public final List<RecipeIngredient> missing = new ArrayList<>();
        public final int total;

        Result(Recipe recipe) {
            this.recipe = recipe;
            this.total = recipe.getIngredients().size();
        }

        public int have() { return total - missing.size(); }
        public boolean isStrictMatch() { return missing.isEmpty(); }
        public boolean isAlmostThere() { return missing.size() == 1; }
        public boolean isMissing(RecipeIngredient i) { return missing.contains(i); }
    }

    /** name -> [mass(g), volume(ml), count(pcs)] */
    private static Map<String, double[]> buildStock(List<PantryItem> pantry) {
        Map<String, double[]> stock = new HashMap<>();
        for (PantryItem p : pantry) {
            String key = NameNormalizer.normalize(p.getName());
            double[] arr = stock.get(key);
            if (arr == null) {
                arr = new double[3];
                stock.put(key, arr);
            }
            arr[Units.dimension(p.getUnit())] += Units.toBase(p.getQuantity(), p.getUnit());
        }
        return stock;
    }

    private static boolean hasEnough(RecipeIngredient ing, Map<String, double[]> stock) {
        double[] have = stock.get(NameNormalizer.normalize(ing.getName()));
        if (have == null) return false;                 // ingredient absent
        if (ing.getQuantity() <= 0) return true;        // "to taste": presence is enough
        int dim = Units.dimension(ing.getUnit());
        double need = Units.toBase(ing.getQuantity(), ing.getUnit());
        double available = have[dim];
        if (dim == Units.MASS) available += have[Units.VOLUME];
        if (dim == Units.VOLUME) available += have[Units.MASS];
        return available + 1e-9 >= need;
    }

    public static Result evaluateOne(Recipe r, List<PantryItem> pantry) {
        return evaluate(r, buildStock(pantry));
    }

    private static Result evaluate(Recipe r, Map<String, double[]> stock) {
        Result res = new Result(r);
        for (RecipeIngredient ing : r.getIngredients()) {
            if (!hasEnough(ing, stock)) res.missing.add(ing);
        }
        return res;
    }

    public static List<Result> evaluateAll(List<Recipe> recipes, List<PantryItem> pantry) {
        Map<String, double[]> stock = buildStock(pantry);
        List<Result> out = new ArrayList<>();
        for (Recipe r : recipes) out.add(evaluate(r, stock));
        return out;
    }

    /** The main list: ONLY recipes with zero missing ingredients. */
    public static List<Result> strictMatches(List<Result> all) {
        List<Result> out = new ArrayList<>();
        for (Result r : all) if (r.isStrictMatch() && r.total > 0) out.add(r);
        Collections.sort(out, new Comparator<Result>() {
            @Override public int compare(Result a, Result b) {
                return Integer.compare(a.recipe.getMinutes(), b.recipe.getMinutes());
            }
        });
        return out;
    }

    /** Bonus list, kept completely separate: recipes missing exactly one ingredient. */
    public static List<Result> almostThere(List<Result> all) {
        List<Result> out = new ArrayList<>();
        for (Result r : all) if (r.isAlmostThere()) out.add(r);
        return out;
    }
}
