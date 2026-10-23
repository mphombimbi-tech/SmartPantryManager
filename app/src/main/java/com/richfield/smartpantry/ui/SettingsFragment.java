package com.richfield.smartpantry.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.slider.Slider;
import com.richfield.smartpantry.R;
import com.richfield.smartpantry.data.DatabaseHelper;
import com.richfield.smartpantry.model.PantryItem;
import com.richfield.smartpantry.util.DateUtils;
import com.richfield.smartpantry.util.Prefs;

/** Settings: expiring-soon alerts, alert window, "Almost there" toggle, demo data tools. */
public class SettingsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle state) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);
        final DatabaseHelper db = DatabaseHelper.get(requireContext());

        MaterialSwitch swAlerts = v.findViewById(R.id.swAlerts);
        MaterialSwitch swAlmost = v.findViewById(R.id.swAlmost);
        Slider slider = v.findViewById(R.id.sliderDays);
        final TextView tvDays = v.findViewById(R.id.tvDays);

        swAlerts.setChecked(Prefs.alertsEnabled(requireContext()));
        swAlmost.setChecked(Prefs.showAlmost(requireContext()));
        slider.setValue(Prefs.alertDays(requireContext()));
        tvDays.setText(daysLabel(Prefs.alertDays(requireContext())));
        slider.setEnabled(swAlerts.isChecked());

        swAlerts.setOnCheckedChangeListener((b, on) -> {
            Prefs.setAlertsEnabled(requireContext(), on);
            slider.setEnabled(on);
        });
        swAlmost.setOnCheckedChangeListener((b, on) -> Prefs.setShowAlmost(requireContext(), on));
        slider.addOnChangeListener((s, value, fromUser) -> {
            Prefs.setAlertDays(requireContext(), (int) value);
            tvDays.setText(daysLabel((int) value));
        });

        v.findViewById(R.id.btnSample).setOnClickListener(x -> {
            String[][] sample = {
                    {"Egg", "6", "pcs", "2"}, {"Milk", "1000", "ml", "3"}, {"Butter", "250", "g", "20"},
                    {"Cheese", "200", "g", "9"}, {"Tomatoes", "5", "pcs", "4"}, {"Onions", "3", "pcs", ""},
                    {"Garlic", "6", "pcs", ""}, {"Pasta", "500", "g", ""}, {"Rice", "1", "kg", ""},
                    {"Bread", "8", "pcs", "1"}, {"Olive Oil", "500", "ml", ""}, {"Salt", "500", "g", ""},
                    {"Soy Sauce", "200", "ml", ""}, {"Carrot", "4", "pcs", "6"}, {"Flour", "1", "kg", ""},
                    {"Sugar", "500", "g", ""}};
            for (String[] s : sample) {
                String exp = s[3].isEmpty() ? null : DateUtils.todayPlus(Integer.parseInt(s[3]));
                db.insertItem(new PantryItem(-1, s[0], Double.parseDouble(s[1]), s[2], exp));
            }
            Toast.makeText(requireContext(), "Sample pantry loaded 🧺 — check the Cook tab!", Toast.LENGTH_LONG).show();
        });

        v.findViewById(R.id.btnClear).setOnClickListener(x ->
                new AlertDialog.Builder(requireContext())
                        .setTitle("Clear your pantry?")
                        .setMessage("This removes every ingredient. Recipes are not affected.")
                        .setPositiveButton("Clear", (d, w) -> {
                            db.clearPantry();
                            Toast.makeText(requireContext(), "Pantry cleared", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Cancel", null)
                        .show());
        return v;
    }

    private String daysLabel(int d) {
        return "Warn me " + d + (d == 1 ? " day" : " days") + " before food expires";
    }
}
