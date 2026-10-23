package com.richfield.smartpantry.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.richfield.smartpantry.R;
import com.richfield.smartpantry.model.PantryItem;
import com.richfield.smartpantry.util.DateUtils;
import com.richfield.smartpantry.util.EmojiMap;
import com.richfield.smartpantry.util.Fmt;
import com.richfield.smartpantry.util.Prefs;

import java.util.ArrayList;
import java.util.List;

/** Custom adapter that binds PantryItem rows from the database to item_pantry cards. */
public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.VH> {

    public interface Listener {
        void onEdit(PantryItem item);
        void onDelete(PantryItem item);
    }

    private final List<PantryItem> items = new ArrayList<>();
    private final Listener listener;

    public PantryAdapter(Listener listener) {
        this.listener = listener;
    }

    public void submit(List<PantryItem> data) {
        items.clear();
        items.addAll(data);
        notifyDataSetChanged();
    }

    public PantryItem get(int position) {
        return items.get(position);
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pantry, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        final PantryItem item = items.get(position);
        Context ctx = h.itemView.getContext();

        h.emoji.setText(EmojiMap.forIngredient(item.getName()));
        h.name.setText(Fmt.title(item.getName()));
        h.qty.setText(Fmt.amount(item.getQuantity(), item.getUnit()));

        Integer days = DateUtils.daysUntil(item.getExpiry());
        int threshold = Prefs.alertDays(ctx);
        int bg, fg;
        String label;
        if (days == null) {
            label = "No expiry date";
            bg = R.drawable.bg_chip_none;
            fg = R.color.chip_none_text;
        } else if (days < 0) {
            label = "Expired " + (-days) + (days == -1 ? " day ago" : " days ago");
            bg = R.drawable.bg_chip_expired;
            fg = R.color.chip_expired_text;
        } else if (days == 0) {
            label = "Expires today";
            bg = R.drawable.bg_chip_soon;
            fg = R.color.chip_soon_text;
        } else if (days <= threshold) {
            label = "Expires in " + days + (days == 1 ? " day" : " days");
            bg = R.drawable.bg_chip_soon;
            fg = R.color.chip_soon_text;
        } else {
            label = "Fresh · " + DateUtils.pretty(item.getExpiry());
            bg = R.drawable.bg_chip_fresh;
            fg = R.color.chip_fresh_text;
        }
        h.expiry.setText(label);
        h.expiry.setBackgroundResource(bg);
        h.expiry.setTextColor(ContextCompat.getColor(ctx, fg));

        h.itemView.setOnClickListener(v -> listener.onEdit(item));
        h.delete.setOnClickListener(v -> listener.onDelete(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        final TextView emoji, name, qty, expiry;
        final ImageButton delete;

        VH(@NonNull View v) {
            super(v);
            emoji = v.findViewById(R.id.tvEmoji);
            name = v.findViewById(R.id.tvName);
            qty = v.findViewById(R.id.tvQty);
            expiry = v.findViewById(R.id.tvExpiry);
            delete = v.findViewById(R.id.btnDelete);
        }
    }
}
