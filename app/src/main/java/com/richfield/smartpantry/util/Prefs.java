package com.richfield.smartpantry.util;

import android.content.Context;
import android.content.SharedPreferences;

/** Thin wrapper around SharedPreferences for the Settings screen. */
public final class Prefs {
    private Prefs() {}

    private static SharedPreferences sp(Context c) {
        return c.getSharedPreferences("smart_pantry_prefs", Context.MODE_PRIVATE);
    }

    public static boolean alertsEnabled(Context c) { return sp(c).getBoolean("alerts", true); }
    public static void setAlertsEnabled(Context c, boolean v) { sp(c).edit().putBoolean("alerts", v).apply(); }

    public static int alertDays(Context c) { return sp(c).getInt("alert_days", 3); }
    public static void setAlertDays(Context c, int d) { sp(c).edit().putInt("alert_days", d).apply(); }

    public static boolean showAlmost(Context c) { return sp(c).getBoolean("show_almost", true); }
    public static void setShowAlmost(Context c, boolean v) { sp(c).edit().putBoolean("show_almost", v).apply(); }
}
