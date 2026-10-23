package com.richfield.smartpantry.data;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

/**
 * Pre-loads 20 recipes the first time the database is created.
 * Ingredient format: "name:quantity:unit,..." (quantity 0 = to taste).
 * Steps are separated with "|".
 */
final class RecipeSeeder {
    private RecipeSeeder() {}

    static void seed(SQLiteDatabase db) {
        add(db, "Fluffy Cheese Omelette", "🍳", 10, 1,
                "Beat the eggs with a pinch of salt.|Melt the butter in a pan over medium heat.|Pour in the eggs and let them set for a minute.|Add the cheese, fold the omelette in half and serve hot.",
                "egg:3:pcs,butter:1:tbsp,cheese:30:g,salt:0:pcs");
        add(db, "Simple Tomato Pasta", "🍝", 20, 2,
                "Boil the pasta in salted water until al dente.|Fry the sliced garlic in olive oil for 1 minute.|Add the chopped tomatoes and simmer for 10 minutes.|Toss the pasta through the sauce and season to taste.",
                "pasta:200:g,tomato:4:pcs,garlic:2:pcs,olive oil:2:tbsp,salt:0:pcs");
        add(db, "Veggie Fried Rice", "🍚", 20, 2,
                "Dice the onion and carrot finely.|Heat the oil and stir-fry the vegetables for 4 minutes.|Push to the side, scramble the eggs in the same pan.|Add the cooked rice and soy sauce, then toss on high heat for 3 minutes.",
                "rice:300:g,egg:2:pcs,carrot:1:pcs,onion:1:pcs,soy sauce:2:tbsp,olive oil:1:tbsp");
        add(db, "Classic Pancakes", "🥞", 20, 3,
                "Whisk the flour, sugar, milk and egg into a smooth batter.|Heat a little butter in a non-stick pan.|Pour in a ladle of batter and cook until bubbles form.|Flip and cook for another minute. Stack and serve.",
                "flour:200:g,milk:300:ml,egg:1:pcs,sugar:2:tbsp,butter:1:tbsp");
        add(db, "Creamy Potato Soup", "🥣", 35, 4,
                "Peel and cube the potatoes, chop the onion.|Soften the onion in butter for 5 minutes.|Add the potatoes and milk, simmer for 20 minutes.|Mash roughly, season with salt and serve warm.",
                "potato:4:pcs,onion:1:pcs,butter:2:tbsp,milk:200:ml,salt:0:pcs");
        add(db, "Garlic Butter Toast", "🍞", 10, 2,
                "Mash the butter with finely chopped garlic.|Spread generously over the bread.|Toast in a hot pan or oven until golden and crisp.",
                "bread:4:pcs,butter:3:tbsp,garlic:2:pcs");
        add(db, "Cheese Toastie", "🧀", 10, 1,
                "Butter the outside of each slice of bread.|Fill with the cheese.|Grill in a pan for 3 minutes per side until melted and golden.",
                "bread:2:pcs,cheese:60:g,butter:1:tbsp");
        add(db, "Chicken Stir-Fry", "🍗", 25, 2,
                "Slice the chicken, pepper and onion into strips.|Sear the chicken in hot oil until golden.|Add the vegetables and garlic and stir-fry for 5 minutes.|Pour in the soy sauce, toss and serve.",
                "chicken breast:300:g,bell pepper:1:pcs,onion:1:pcs,soy sauce:2:tbsp,garlic:2:pcs,olive oil:1:tbsp");
        add(db, "Fresh Guacamole", "🥑", 10, 2,
                "Scoop the avocados into a bowl and mash.|Finely dice the tomato and onion and stir in.|Squeeze in the lime juice and season with salt.",
                "avocado:2:pcs,lime:1:pcs,tomato:1:pcs,onion:1:pcs,salt:0:pcs");
        add(db, "Banana Honey Porridge", "🍌", 10, 1,
                "Simmer the oats and milk for 5 minutes, stirring.|Slice the banana on top.|Drizzle with honey and serve.",
                "oats:50:g,milk:250:ml,banana:1:pcs,honey:1:tbsp");
        add(db, "Comforting Lentil Curry", "🍛", 40, 4,
                "Fry the chopped onion and garlic in olive oil.|Stir in the curry powder for 30 seconds.|Add lentils, chopped tomatoes and 500 ml water.|Simmer for 25 minutes until soft and thick.",
                "lentils:250:g,onion:1:pcs,tomato:2:pcs,garlic:3:pcs,curry powder:2:tsp,olive oil:1:tbsp");
        add(db, "Scrambled Eggs on Toast", "🥚", 10, 1,
                "Whisk the eggs with salt.|Scramble gently in melted butter over low heat.|Serve piled on buttered toast.",
                "egg:3:pcs,bread:2:pcs,butter:1:tbsp,salt:0:pcs");
        add(db, "Greek Salad", "🥗", 10, 2,
                "Chop the cucumber and tomatoes into chunks.|Add the olives and crumble over the feta.|Drizzle with olive oil and toss lightly.",
                "cucumber:1:pcs,tomato:3:pcs,feta cheese:100:g,olives:50:g,olive oil:2:tbsp");
        add(db, "Spaghetti Aglio e Olio", "🍝", 15, 2,
                "Cook the spaghetti until al dente.|Gently warm sliced garlic in olive oil until fragrant.|Add the chilli flakes, then toss in the spaghetti.|Serve immediately.",
                "spaghetti:200:g,garlic:4:pcs,olive oil:4:tbsp,chilli flakes:1:tsp");
        add(db, "Mushroom Risotto", "🍄", 40, 3,
                "Soften the onion and mushrooms in butter.|Add the rice and stir for 1 minute.|Add hot stock a ladle at a time, stirring, for 20 minutes.|Finish with cheese and serve.",
                "rice:250:g,mushroom:200:g,onion:1:pcs,butter:2:tbsp,stock:750:ml,cheese:40:g");
        add(db, "Tuna Sandwich", "🥪", 5, 1,
                "Mix the tuna with the mayonnaise.|Pile onto the bread with the lettuce leaves.|Close the sandwich and slice in half.",
                "bread:2:pcs,tuna:1:pcs,mayonnaise:2:tbsp,lettuce:2:pcs");
        add(db, "Black Bean Burrito", "🌯", 15, 2,
                "Warm the beans with the chopped onion in a pan.|Spoon onto the tortillas with the tomato and cheese.|Roll up tightly and toast seam-side down.",
                "tortilla:2:pcs,black beans:200:g,cheese:50:g,tomato:1:pcs,onion:1:pcs");
        add(db, "Cinnamon French Toast", "🍞", 15, 2,
                "Whisk the eggs, milk, cinnamon and sugar.|Soak the bread slices in the mixture.|Fry in butter until golden on both sides.",
                "bread:4:pcs,egg:2:pcs,milk:100:ml,cinnamon:1:tsp,sugar:1:tbsp,butter:1:tbsp");
        add(db, "Two-Ingredient Banana Pancakes", "🍌", 15, 2,
                "Mash the bananas until smooth.|Mix in the eggs and oats.|Cook small spoonfuls in a pan for 2 minutes per side.",
                "banana:2:pcs,egg:2:pcs,oats:60:g");
        add(db, "Honey Yogurt Bowl", "🍓", 5, 1,
                "Spoon the yogurt into a bowl.|Slice the banana over the top.|Add the oats and drizzle with honey.",
                "yogurt:250:g,banana:1:pcs,honey:1:tbsp,oats:30:g");
    }

    private static void add(SQLiteDatabase db, String name, String emoji, int minutes, int servings,
                            String steps, String ingredients) {
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("emoji", emoji);
        cv.put("minutes", minutes);
        cv.put("servings", servings);
        cv.put("steps", steps.replace("|", "\n"));
        long recipeId = db.insert("recipes", null, cv);

        for (String part : ingredients.split(",")) {
            String[] p = part.split(":");
            ContentValues ic = new ContentValues();
            ic.put("recipe_id", recipeId);
            ic.put("name", p[0]);
            ic.put("quantity", Double.parseDouble(p[1]));
            ic.put("unit", p[2]);
            db.insert("recipe_ingredients", null, ic);
        }
    }
}
