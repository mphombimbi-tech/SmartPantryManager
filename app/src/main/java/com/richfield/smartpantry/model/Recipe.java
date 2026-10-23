package com.richfield.smartpantry.model;

import java.util.ArrayList;
import java.util.List;

public class Recipe {
    private final long id;
    private final String name;
    private final String emoji;
    private final int minutes;
    private final int servings;
    private final String steps; // steps separated by \n
    private final List<RecipeIngredient> ingredients = new ArrayList<>();

    public Recipe(long id, String name, String emoji, int minutes, int servings, String steps) {
        this.id = id;
        this.name = name;
        this.emoji = emoji;
        this.minutes = minutes;
        this.servings = servings;
        this.steps = steps;
    }

    public long getId() { return id; }
    public String getName() { return name; }
    public String getEmoji() { return emoji; }
    public int getMinutes() { return minutes; }
    public int getServings() { return servings; }
    public String getSteps() { return steps; }
    public List<RecipeIngredient> getIngredients() { return ingredients; }
}
