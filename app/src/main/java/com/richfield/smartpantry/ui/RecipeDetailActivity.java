package com.richfield.smartpantry.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.richfield.smartpantry.R;
import com.richfield.smartpantry.data.DatabaseHelper;
import com.richfield.smartpantry.model.Recipe;
import com.richfield.smartpantry.model.RecipeIngredient;
import com.richfield.smartpantry.util.EmojiMap;
import com.richfield.smartpantry.util.Fmt;
import com.richfield.smartpantry.util.RecipeMatcher;

/** Full ingredient list (with have / missing status) and method for one recipe. */
public class RecipeDetailActivity extends AppCompatActivity {

    public static final String EXTRA_RECIPE_ID = "recipe_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        DatabaseHelper db = DatabaseHelper.get(this);
        Recipe recipe = db.getRecipe(getIntent().getLongExtra(EXTRA_RECIPE_ID, -1));
        if (recipe == null) { finish(); return; }

        RecipeMatcher.Result res = RecipeMatcher.evaluateOne(recipe, db.getAllItems());

        ((TextView) findViewById(R.id.tvEmoji)).setText(recipe.getEmoji());
        ((TextView) findViewById(R.id.tvTitle)).setText(recipe.getName());
        ((TextView) findViewById(R.id.tvTime)).setText("⏱ " + recipe.getMinutes() + " min");
        ((TextView) findViewById(R.id.tvServings)).setText("🍽 " + recipe.getServings()
                + (recipe.getServings() == 1 ? " serving" : " servings"));

        TextView status = findViewById(R.id.tvStatus);
        if (res.isStrictMatch()) {
            status.setText("✓ You have everything you need");
            status.setTextColor(ContextCompat.getColor(this, R.color.primary));
        } else {
            status.setText("Missing " + res.missing.size() + " of " + res.total + " ingredients");
            status.setTextColor(ContextCompat.getColor(this, R.color.accent));
        }

        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout llIng = findViewById(R.id.llIngredients);
        for (RecipeIngredient ing : recipe.getIngredients()) {
            View row = inflater.inflate(R.layout.item_ingredient_line, llIng, false);
            ((TextView) row.findViewById(R.id.tvIEmoji)).setText(EmojiMap.forIngredient(ing.getName()));
            ((TextView) row.findViewById(R.id.tvIText)).setText(Fmt.ingredientLine(ing));
            TextView st = row.findViewById(R.id.tvIStatus);
            if (res.isMissing(ing)) {
                st.setText("Missing");
                st.setBackgroundResource(R.drawable.bg_chip_expired);
                st.setTextColor(ContextCompat.getColor(this, R.color.chip_expired_text));
            } else {
                st.setText("In pantry");
                st.setBackgroundResource(R.drawable.bg_chip_fresh);
                st.setTextColor(ContextCompat.getColor(this, R.color.chip_fresh_text));
            }
            llIng.addView(row);
        }

        LinearLayout llSteps = findViewById(R.id.llSteps);
        String[] steps = recipe.getSteps().split("\n");
        for (int i = 0; i < steps.length; i++) {
            View row = inflater.inflate(R.layout.item_step, llSteps, false);
            ((TextView) row.findViewById(R.id.tvStepNum)).setText(String.valueOf(i + 1));
            ((TextView) row.findViewById(R.id.tvStepText)).setText(steps[i]);
            llSteps.addView(row);
        }
    }
}
