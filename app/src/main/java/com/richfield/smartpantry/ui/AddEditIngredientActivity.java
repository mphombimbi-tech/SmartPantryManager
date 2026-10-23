package com.richfield.smartpantry.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.richfield.smartpantry.R;
import com.richfield.smartpantry.data.DatabaseHelper;
import com.richfield.smartpantry.model.PantryItem;
import com.richfield.smartpantry.util.DateUtils;
import com.richfield.smartpantry.util.EmojiMap;
import com.richfield.smartpantry.util.Fmt;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/** CREATE and UPDATE (and DELETE) a pantry item, with full input validation. */
public class AddEditIngredientActivity extends AppCompatActivity {

    public static final String EXTRA_ITEM_ID = "item_id";

    private DatabaseHelper db;
    private long editId = -1;
    private String expiryIso = null;

    private TextInputLayout tilName, tilQty, tilUnit;
    private AutoCompleteTextView etName, actUnit;
    private TextInputEditText etQty, etExpiry;
    private TextView tvPreview, btnClearDate;
    private List<String> units;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);
        db = DatabaseHelper.get(this);

        tilName = findViewById(R.id.tilName);
        tilQty = findViewById(R.id.tilQty);
        tilUnit = findViewById(R.id.tilUnit);
        etName = findViewById(R.id.etName);
        etQty = findViewById(R.id.etQty);
        actUnit = findViewById(R.id.actUnit);
        etExpiry = findViewById(R.id.etExpiry);
        tvPreview = findViewById(R.id.tvPreview);
        btnClearDate = findViewById(R.id.btnClearDate);

        units = Arrays.asList(getResources().getStringArray(R.array.units));
        actUnit.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, units));
        actUnit.setText(units.get(0), false);

        etName.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line,
                getResources().getStringArray(R.array.common_ingredients)));
        etName.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) {
                tvPreview.setText(EmojiMap.forIngredient(s.toString()));
                tilName.setError(null);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // date picker (optional field)
        View.OnClickListener pick = v -> openDatePicker();
        etExpiry.setOnClickListener(pick);
        ((TextInputLayout) findViewById(R.id.tilExpiry)).setEndIconOnClickListener(pick);
        btnClearDate.setOnClickListener(v -> setExpiry(null));

        findViewById(R.id.btnSave).setOnClickListener(v -> save());

        // were we started in EDIT mode? (data passed by Intent)
        editId = getIntent().getLongExtra(EXTRA_ITEM_ID, -1);
        if (editId != -1) {
            PantryItem item = db.getItem(editId);
            if (item == null) { finish(); return; }
            ((TextView) findViewById(R.id.tvScreenTitle)).setText("Edit ingredient");
            ((TextView) findViewById(R.id.btnSave)).setText("Update ingredient");
            etName.setText(Fmt.title(item.getName()));
            etName.dismissDropDown();
            etQty.setText(Fmt.qty(item.getQuantity()));
            actUnit.setText(item.getUnit(), false);
            setExpiry(item.getExpiry());
            View del = findViewById(R.id.btnDelete);
            del.setVisibility(View.VISIBLE);
            del.setOnClickListener(v -> confirmDelete());
        }
    }

    private void openDatePicker() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, (view, y, m, d) -> setExpiry(DateUtils.toIso(y, m, d)),
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void setExpiry(String iso) {
        expiryIso = iso;
        etExpiry.setText(iso == null ? "" : DateUtils.pretty(iso));
        btnClearDate.setVisibility(iso == null ? View.GONE : View.VISIBLE);
    }

    /** Validates every field; returns true only if all are valid. */
    private boolean validate() {
        boolean ok = true;
        String name = etName.getText().toString().trim();
        if (name.isEmpty()) {
            tilName.setError("Please enter an ingredient name");
            ok = false;
        } else if (name.length() < 2 || !name.matches(".*\\p{L}.*")) {
            tilName.setError("Use a real name (at least 2 characters, with letters)");
            ok = false;
        } else {
            tilName.setError(null);
        }

        String q = etQty.getText() == null ? "" : etQty.getText().toString().trim();
        double qty = 0;
        try {
            qty = Double.parseDouble(q);
        } catch (NumberFormatException ignored) { }
        if (q.isEmpty()) {
            tilQty.setError("Enter a quantity");
            ok = false;
        } else if (qty <= 0) {
            tilQty.setError("Must be more than 0");
            ok = false;
        } else if (qty > 100000) {
            tilQty.setError("That's a lot! Max 100000");
            ok = false;
        } else {
            tilQty.setError(null);
        }

        if (!units.contains(actUnit.getText().toString())) {
            tilUnit.setError("Pick a unit");
            ok = false;
        } else {
            tilUnit.setError(null);
        }
        return ok;
    }

    private void save() {
        if (!validate()) return;
        String name = etName.getText().toString().trim();
        double qty = Double.parseDouble(etQty.getText().toString().trim());
        String unit = actUnit.getText().toString();
        PantryItem item = new PantryItem(editId, name, qty, unit, expiryIso);

        if (editId == -1) {
            db.insertItem(item);                       // CREATE
            Toast.makeText(this, Fmt.title(name) + " added to your pantry ✅", Toast.LENGTH_SHORT).show();
        } else {
            db.updateItem(item);                       // UPDATE
            Toast.makeText(this, Fmt.title(name) + " updated ✅", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete ingredient?")
                .setMessage("Remove " + Fmt.title(etName.getText().toString()) + " from your pantry?")
                .setPositiveButton("Delete", (d, w) -> {
                    db.deleteItem(editId);             // DELETE
                    Toast.makeText(this, "Ingredient deleted 🗑️", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
