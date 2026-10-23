package com.richfield.smartpantry.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.richfield.smartpantry.R;
import com.richfield.smartpantry.data.DatabaseHelper;
import com.richfield.smartpantry.model.PantryItem;
import com.richfield.smartpantry.model.Recipe;
import com.richfield.smartpantry.util.Prefs;
import com.richfield.smartpantry.util.RecipeMatcher;

import java.util.ArrayList;
import java.util.List;

/**
 * Runs the STRICT matching rule against the pantry. The default tab shows ONLY recipes
 * where every ingredient is available. "Almost there" (bonus) is a separate tab.
 */
public class SuggestedFragment extends Fragment implements RecipeAdapter.Listener {

    private DatabaseHelper db;
    private RecipeAdapter adapter;
    private MaterialButtonToggleGroup toggle;
    private MaterialButton btnReady, btnAlmost;
    private TextView tvSubtitle, tvHint, tvEmptyTitle, tvEmptySub;
    private View emptyState;

    private List<RecipeMatcher.Result> strict = new ArrayList<>();
    private List<RecipeMatcher.Result> almost = new ArrayList<>();
    private int pantrySize = 0;
    private boolean almostEnabled = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle state) {
        View v = inflater.inflate(R.layout.fragment_suggested, container, false);
        db = DatabaseHelper.get(requireContext());

        toggle = v.findViewById(R.id.toggleMode);
        btnReady = v.findViewById(R.id.btnReady);
        btnAlmost = v.findViewById(R.id.btnAlmost);
        tvSubtitle = v.findViewById(R.id.tvSubtitle);
        tvHint = v.findViewById(R.id.tvModeHint);
        tvEmptyTitle = v.findViewById(R.id.tvEmptyTitle);
        tvEmptySub = v.findViewById(R.id.tvEmptySub);
        emptyState = v.findViewById(R.id.emptyState);

        RecyclerView rv = v.findViewById(R.id.rvRecipes);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RecipeAdapter(this);
        rv.setAdapter(adapter);

        toggle.check(R.id.btnReady);
        toggle.addOnButtonCheckedListener((group, id, checked) -> {
            if (checked) render();
        });
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        reload(); // pantry may have changed -> re-run the strict matching
    }

    private void reload() {
        List<Recipe> recipes = db.getAllRecipes();
        List<PantryItem> pantry = db.getAllItems();
        pantrySize = pantry.size();

        List<RecipeMatcher.Result> all = RecipeMatcher.evaluateAll(recipes, pantry);
        strict = RecipeMatcher.strictMatches(all);
        almost = RecipeMatcher.almostThere(all);

        almostEnabled = Prefs.showAlmost(requireContext());
        toggle.setVisibility(almostEnabled ? View.VISIBLE : View.GONE);
        if (!almostEnabled) toggle.check(R.id.btnReady);

        btnReady.setText("Ready (" + strict.size() + ")");
        btnAlmost.setText("Almost there (" + almost.size() + ")");
        tvSubtitle.setText(strict.size() == 0
                ? "Nothing cookable yet — let's stock up!"
                : "You can cook " + strict.size() + (strict.size() == 1 ? " recipe" : " recipes") + " right now 🎉");
        render();
    }

    private void render() {
        boolean almostMode = almostEnabled && toggle.getCheckedButtonId() == R.id.btnAlmost;
        List<RecipeMatcher.Result> list = almostMode ? almost : strict;
        adapter.submit(list);

        tvHint.setText(almostMode
                ? "These are each missing exactly ONE ingredient. They are not suggestions yet."
                : "Only recipes where you have every ingredient, in the quantity needed.");
        tvHint.setVisibility(almostEnabled ? View.VISIBLE : View.GONE);

        emptyState.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
        if (list.isEmpty()) {
            if (almostMode) {
                tvEmptyTitle.setText("Nothing is one step away");
                tvEmptySub.setText("Add a few more ingredients to your pantry and check back.");
            } else {
                tvEmptyTitle.setText("No recipes match your pantry yet");
                tvEmptySub.setText(pantrySize == 0
                        ? "Add more ingredients on the Pantry tab and delicious ideas will appear here."
                        : "No recipes match your pantry yet - add more ingredients.");
            }
        }
    }

    @Override
    public void onOpen(Recipe recipe) {
        Intent i = new Intent(requireContext(), RecipeDetailActivity.class);
        i.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.getId()); // pass data via Intent
        startActivity(i);
    }
}
