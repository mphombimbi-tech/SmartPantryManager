package com.richfield.smartpantry.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.richfield.smartpantry.R;
import com.richfield.smartpantry.model.Recipe;
import com.richfield.smartpantry.model.RecipeIngredient;
import com.richfield.smartpantry.util.Fmt;
import com.richfield.smartpantry.util.RecipeMatcher;

import java.util.ArrayList;
import java.util.List;

/** Adapter for the Suggested Recipes list. */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.VH> {

    public interface Listener {
        void onOpen(Recipe recipe);
    }

    private final List<RecipeMatcher.Result> items = new ArrayList<>();
    private final Listener listener;

    public RecipeAdapter(Listener listener) {
        this.listener = listener;
    }

    public void submit(List<RecipeMatcher.Result> data) {
        items.clear();
        items.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        final RecipeMatcher.Result res = items.get(position);
        final Recipe r = res.recipe;
        Context ctx = h.itemView.getContext();

        h.emoji.setText(r.getEmoji());
        h.title.setText(r.getName());
        h.meta.setText("⏱ " + r.getMinutes() + " min   ·   🍽 " + r.getServings()
                + (r.getServings() == 1 ? " serving" : " servings"));

        h.progress.setMax(Math.max(res.total, 1));
        h.progress.setProgress(res.have());

        if (res.isStrictMatch()) {
            h.status.setText("✓ All " + res.total + " ingredients ready");
            h.status.setTextColor(ContextCompat.getColor(ctx, R.color.primary));
        } else {
            StringBuilder sb = new StringBuilder("Missing: ");
            for (int i = 0; i < res.missing.size(); i++) {
                RecipeIngredient m = res.missing.get(i);
                if (i > 0) sb.append(", ");
                sb.append(Fmt.title(m.getName()));
            }
            h.status.setText(sb.toString());
            h.status.setTextColor(ContextCompat.getColor(ctx, R.color.accent));
        }
        h.itemView.setOnClickListener(v -> listener.onOpen(r));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        final TextView emoji, title, meta, status;
        final LinearProgressIndicator progress;

        VH(@NonNull View v) {
            super(v);
            emoji = v.findViewById(R.id.tvEmoji);
            title = v.findViewById(R.id.tvTitle);
            meta = v.findViewById(R.id.tvMeta);
            status = v.findViewById(R.id.tvStatus);
            progress = v.findViewById(R.id.progress);
        }
    }
}
