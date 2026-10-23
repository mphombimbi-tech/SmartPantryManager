# 🧺 Smart Pantry Manager

Smart Pantry Manager is an Android app that helps you keep track of what food you actually have at home and suggests recipes you can cook with it. Instead of guessing what's in the fridge or cupboard, you add your ingredients into the app, and it matches them against a list of recipes to show you what you can make right now.
The app was built for the Mobile App Development 700 module, using Java and a local SQLite database.

 # Why SQLite
I chose SQLite because it's built into Android, 
so there's no need to set up or connect to an external server or database service. 
It stores all the pantry items and recipes directly on the device, 
which **makes the app fast**, works offline, and keeps everything simple for a project of this size. 
Since the app only needs to manage one user's data at a time, 
a lightweight local database like SQLite is a natural fit rather than something more complex like a cloud database.

> Mobile App Development 700 - Practical Assignment (Richfield Graduate Institute of Technology)

## Features
- **Pantry management** - add, edit and delete ingredients (name, quantity, unit, optional expiry date)
- **Pantry list** - RecyclerView + custom adapter, live search, swipe-to-delete with UNDO, colour-coded expiry chips
- **20 pre-loaded recipes** seeded into the database on first run
- **Suggested Recipes** - only recipes where *every* ingredient is in the pantry in the required quantity
- **Almost there (bonus)** - separate tab for recipes missing exactly one ingredient, never mixed with the strict list
- **Recipe detail** - full ingredient list (in pantry / missing) and numbered method
- **Settings** - expiring-soon alerts + alert window, toggle "Almost there", load sample / clear pantry
- Friendly empty states (e.g. "No recipes match your pantry yet - add more ingredients")
- Input validation on the Add/Edit form; no Maps, GPS or location features

## Database choice: SQLite (`SQLiteOpenHelper`)
Local, on-device, zero setup and works offline - ideal for a personal pantry. Data genuinely persists
after the app is closed and re-opened. Tables: `pantry`, `recipes`, `recipe_ingredients`
(foreign key with `ON DELETE CASCADE`).

## The strict-matching rule (`util/RecipeMatcher.java`)
A recipe is suggested **only if every ingredient** is present in at least the required quantity.
5 ingredients needed + 4 in pantry = **not suggested**. To survive real-world messiness:
- names are normalised: `Tomatoes` == `tomato`, `Garlic cloves` == `garlic`, case/whitespace ignored
- multiple pantry rows of one ingredient are summed
- units convert: kg->g, l / cup / tbsp / tsp -> ml. Mass and volume are treated as ~1 g = 1 ml; piece counts never convert to g/ml
- "to taste" ingredients (quantity 0, e.g. salt) only need to exist

## Project structure
```
app/src/main/java/com/richfield/smartpantry/
  data/   DatabaseHelper (CRUD), RecipeSeeder (20 recipes)
  model/  PantryItem, Recipe, RecipeIngredient
  util/   RecipeMatcher, NameNormalizer, Units, DateUtils, Fmt, EmojiMap, Prefs
  ui/     MainActivity (bottom nav host), PantryFragment, SuggestedFragment, SettingsFragment,
          AddEditIngredientActivity, RecipeDetailActivity, PantryAdapter, RecipeAdapter
```
Navigation: `BottomNavigationView` + Fragments; **Intents** carry `item_id` / `recipe_id` to the
Add/Edit and Recipe Detail activities.

## Setup / run
1. Install **Android Studio** (Hedgehog or newer) with JDK 17.
2. `git clone <your-repo-url>` then **File > Open** the `SmartPantryManager` folder.
3. Let Gradle sync (it downloads Gradle 8.2 automatically).
4. Run on an emulator (API 24+) or a physical device. 
5. Tip: Settings > **Load sample pantry** fills the pantry so you can see suggestions straight away.

Min SDK 24 · Target SDK 34 · Java 8 · Material 3
