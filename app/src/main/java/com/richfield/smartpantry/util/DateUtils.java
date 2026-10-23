package com.richfield.smartpantry.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public final class DateUtils {
    private DateUtils() {}

    private static SimpleDateFormat iso() {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        f.setLenient(false);
        return f;
    }

    /** Whole days from today until the ISO date (negative = already expired); null if no date. */
    public static Integer daysUntil(String isoDate) {
        if (isoDate == null || isoDate.isEmpty()) return null;
        try {
            Date d = iso().parse(isoDate);
            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);
            return (int) Math.round((d.getTime() - today.getTimeInMillis()) / 86400000.0);
        } catch (ParseException e) {
            return null;
        }
    }

    public static String pretty(String isoDate) {
        try {
            return new SimpleDateFormat("d MMM yyyy", Locale.getDefault()).format(iso().parse(isoDate));
        } catch (Exception e) {
            return isoDate;
        }
    }

    public static String toIso(int year, int month0, int day) {
        return String.format(Locale.US, "%04d-%02d-%02d", year, month0 + 1, day);
    }

    public static String todayPlus(int days) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, days);
        return iso().format(c.getTime());
    }
}
