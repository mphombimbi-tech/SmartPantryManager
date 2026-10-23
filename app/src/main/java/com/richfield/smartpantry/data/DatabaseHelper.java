package com.richfield.smartpantry.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.richfield.smartpantry.model.PantryItem;
import com.richfield.smartpantry.model.Recipe;
import com.richfield.smartpantry.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.List;

/**
 * Local SQLite storage (SQLiteOpenHelper).
 *  - pantry              : the user's ingredients  (full CRUD)
 *  - recipes             : 20 recipes seeded on first run
 *  - recipe_ingredients  : one row per ingredient line of a recipe
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "smart_pantry.db";
    private static final int DB_VERSION = 1;
    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper get(Context ctx) {
        if (instance == null) instance = new DatabaseHelper(ctx.getApplicationContext());
        return instance;
    }

    private DatabaseHelper(Context ctx) {
        super(ctx, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE pantry (id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL, quantity REAL NOT NULL, unit TEXT NOT NULL, expiry TEXT)");
        db.execSQL("CREATE TABLE recipes (id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL, emoji TEXT, minutes INTEGER, servings INTEGER, steps TEXT)");
        db.execSQL("CREATE TABLE recipe_ingredients (id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "recipe_id INTEGER NOT NULL REFERENCES recipes(id) ON DELETE CASCADE, "
                + "name TEXT NOT NULL, quantity REAL NOT NULL, unit TEXT NOT NULL)");
        RecipeSeeder.seed(db); // pre-load the recipe collection on first run
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS recipe_ingredients");
        db.execSQL("DROP TABLE IF EXISTS recipes");
        db.execSQL("DROP TABLE IF EXISTS pantry");
        onCreate(db);
    }

    // ------------------------------------------------------------ CREATE
    public long insertItem(PantryItem item) {
        long id = getWritableDatabase().insert("pantry", null, toValues(item));
        item.setId(id);
        return id;
    }

    // ------------------------------------------------------------ READ
    public List<PantryItem> getAllItems() {
        List<PantryItem> list = new ArrayList<>();
        try (Cursor c = getReadableDatabase().query("pantry", null, null, null, null, null, "name COLLATE NOCASE ASC")) {
            while (c.moveToNext()) list.add(fromCursor(c));
        }
        return list;
    }

    public PantryItem getItem(long id) {
        try (Cursor c = getReadableDatabase().query("pantry", null, "id=?", new String[]{String.valueOf(id)}, null, null, null)) {
            return c.moveToFirst() ? fromCursor(c) : null;
        }
    }

    // ------------------------------------------------------------ UPDATE
    public int updateItem(PantryItem item) {
        return getWritableDatabase().update("pantry", toValues(item), "id=?", new String[]{String.valueOf(item.getId())});
    }

    // ------------------------------------------------------------ DELETE
    public int deleteItem(long id) {
        return getWritableDatabase().delete("pantry", "id=?", new String[]{String.valueOf(id)});
    }

    public void clearPantry() {
        getWritableDatabase().delete("pantry", null, null);
    }

    // ------------------------------------------------------------ RECIPES
    public List<Recipe> getAllRecipes() {
        List<Recipe> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor c = db.query("recipes", null, null, null, null, null, "name ASC")) {
            while (c.moveToNext()) {
                Recipe r = recipeFromCursor(c);
                loadIngredients(db, r);
                list.add(r);
            }
        }
        return list;
    }

    public Recipe getRecipe(long id) {
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor c = db.query("recipes", null, "id=?", new String[]{String.valueOf(id)}, null, null, null)) {
            if (!c.moveToFirst()) return null;
            Recipe r = recipeFromCursor(c);
            loadIngredients(db, r);
            return r;
        }
    }

    private void loadIngredients(SQLiteDatabase db, Recipe r) {
        try (Cursor c = db.query("recipe_ingredients", null, "recipe_id=?",
                new String[]{String.valueOf(r.getId())}, null, null, "id ASC")) {
            while (c.moveToNext()) {
                r.getIngredients().add(new RecipeIngredient(
                        c.getString(c.getColumnIndexOrThrow("name")),
                        c.getDouble(c.getColumnIndexOrThrow("quantity")),
                        c.getString(c.getColumnIndexOrThrow("unit"))));
            }
        }
    }

    // ------------------------------------------------------------ helpers
    private ContentValues toValues(PantryItem i) {
        ContentValues cv = new ContentValues();
        cv.put("name", i.getName());
        cv.put("quantity", i.getQuantity());
        cv.put("unit", i.getUnit());
        if (i.getExpiry() == null) cv.putNull("expiry"); else cv.put("expiry", i.getExpiry());
        return cv;
    }

    private PantryItem fromCursor(Cursor c) {
        return new PantryItem(
                c.getLong(c.getColumnIndexOrThrow("id")),
                c.getString(c.getColumnIndexOrThrow("name")),
                c.getDouble(c.getColumnIndexOrThrow("quantity")),
                c.getString(c.getColumnIndexOrThrow("unit")),
                c.getString(c.getColumnIndexOrThrow("expiry")));
    }

    private Recipe recipeFromCursor(Cursor c) {
        return new Recipe(
                c.getLong(c.getColumnIndexOrThrow("id")),
                c.getString(c.getColumnIndexOrThrow("name")),
                c.getString(c.getColumnIndexOrThrow("emoji")),
                c.getInt(c.getColumnIndexOrThrow("minutes")),
                c.getInt(c.getColumnIndexOrThrow("servings")),
                c.getString(c.getColumnIndexOrThrow("steps")));
    }
}
