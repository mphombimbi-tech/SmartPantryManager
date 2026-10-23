package com.richfield.smartpantry.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.richfield.smartpantry.R;
import com.richfield.smartpantry.data.DatabaseHelper;
import com.richfield.smartpantry.model.PantryItem;
import com.richfield.smartpantry.util.DateUtils;
import com.richfield.smartpantry.util.Fmt;
import com.richfield.smartpantry.util.Prefs;

import java.util.ArrayList;
import java.util.List;

/** Pantry list screen: READ (list + search), DELETE (button / swipe + undo), navigates to Add/Edit. */
public class PantryFragment extends Fragment implements PantryAdapter.Listener {

    private DatabaseHelper db;
    private PantryAdapter adapter;
    private List<PantryItem> all = new ArrayList<>();

    private TextView tvStats, tvBanner, tvEmptyTitle, tvEmptySub;
    private View banner, emptyState, root;
    private EditText etSearch;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle state) {
        root = inflater.inflate(R.layout.fragment_pantry, container, false);
        db = DatabaseHelper.get(requireContext());

        tvStats = root.findViewById(R.id.tvStats);
        tvBanner = root.findViewById(R.id.tvBanner);
        tvEmptyTitle = root.findViewById(R.id.tvEmptyTitle);
        tvEmptySub = root.findViewById(R.id.tvEmptySub);
        banner = root.findViewById(R.id.banner);
        emptyState = root.findViewById(R.id.emptyState);
        etSearch = root.findViewById(R.id.etSearch);

        RecyclerView rv = root.findViewById(R.id.rvPantry);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PantryAdapter(this);
        rv.setAdapter(adapter);

        // swipe either direction to delete (with UNDO)
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView r, @NonNull RecyclerView.ViewHolder a, @NonNull RecyclerView.ViewHolder b) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder vh, int direction) {
                onDelete(adapter.get(vh.getBindingAdapterPosition()));
            }
        }).attachToRecyclerView(rv);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) { applyFilter(); }
            @Override public void afterTextChanged(Editable s) {}
        });

        ExtendedFloatingActionButton fab = root.findViewById(R.id.fabAdd);
        fab.setOnClickListener(v -> startActivity(new Intent(requireContext(), AddEditIngredientActivity.class)));
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        load(); // refresh after returning from Add/Edit
    }

    private void load() {
        all = db.getAllItems();
        applyFilter();
        updateHeader();
    }

    private void applyFilter() {
        String q = etSearch.getText().toString().trim().toLowerCase();
        List<PantryItem> shown = new ArrayList<>();
        for (PantryItem p : all) {
            if (q.isEmpty() || p.getName().toLowerCase().contains(q)) shown.add(p);
        }
        adapter.submit(shown);
        boolean empty = shown.isEmpty();
        emptyState.setVisibility(empty ? View.VISIBLE : View.GONE);
        if (empty && !q.isEmpty()) {
            tvEmptyTitle.setText("No matches");
            tvEmptySub.setText("Nothing in your pantry matches “" + q + "”.");
        } else if (empty) {
            tvEmptyTitle.setText("Your pantry is empty");
            tvEmptySub.setText("Tap “Add item” to list what you have at home and we'll find recipes for it.");
        }
    }

    private void updateHeader() {
        int soon = 0, expired = 0;
        int threshold = Prefs.alertDays(requireContext());
        List<String> names = new ArrayList<>();
        for (PantryItem p : all) {
            Integer d = DateUtils.daysUntil(p.getExpiry());
            if (d == null) continue;
            if (d < 0) { expired++; names.add(Fmt.title(p.getName())); }
            else if (d <= threshold) { soon++; names.add(Fmt.title(p.getName())); }
        }
        tvStats.setText(all.size() + (all.size() == 1 ? " ingredient" : " ingredients")
                + (soon + expired > 0 ? "  ·  " + (soon + expired) + " need attention" : "  ·  all fresh ✨"));

        if (Prefs.alertsEnabled(requireContext()) && (soon + expired) > 0) {
            String list = names.size() > 3 ? names.get(0) + ", " + names.get(1) + ", " + names.get(2) + " & more"
                    : android.text.TextUtils.join(", ", names);
            tvBanner.setText("⚠️ " + (soon + expired) + " item" + (soon + expired == 1 ? "" : "s")
                    + " expiring within " + threshold + " days (or already expired): " + list
                    + ". Cook them first!");
            banner.setVisibility(View.VISIBLE);
        } else {
            banner.setVisibility(View.GONE);
        }
    }

    @Override
    public void onEdit(PantryItem item) {
        Intent i = new Intent(requireContext(), AddEditIngredientActivity.class);
        i.putExtra(AddEditIngredientActivity.EXTRA_ITEM_ID, item.getId()); // pass data via Intent
        startActivity(i);
    }

    @Override
    public void onDelete(final PantryItem item) {
        db.deleteItem(item.getId());
        load();
        Snackbar.make(root, Fmt.title(item.getName()) + " removed", Snackbar.LENGTH_LONG)
                .setAction("UNDO", v -> {
                    db.insertItem(item); // re-create the row
                    load();
                }).show();
    }
}
