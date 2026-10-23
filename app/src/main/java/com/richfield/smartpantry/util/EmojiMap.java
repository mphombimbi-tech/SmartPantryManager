package com.richfield.smartpantry.util;

import java.util.LinkedHashMap;
import java.util.Map;

/** Gives every ingredient a friendly emoji so the UI feels alive without image assets. */
public final class EmojiMap {
    private static final Map<String, String> MAP = new LinkedHashMap<>();

    static {
        MAP.put("eggplant", "🍆"); MAP.put("egg", "🥚"); MAP.put("milk", "🥛");
        MAP.put("butter", "🧈"); MAP.put("cheese", "🧀"); MAP.put("tomato", "🍅");
        MAP.put("onion", "🧅"); MAP.put("garlic", "🧄"); MAP.put("potato", "🥔");
        MAP.put("carrot", "🥕"); MAP.put("rice", "🍚"); MAP.put("spaghetti", "🍝");
        MAP.put("pasta", "🍝"); MAP.put("bread", "🍞"); MAP.put("chicken", "🍗");
        MAP.put("avocado", "🥑"); MAP.put("banana", "🍌"); MAP.put("lime", "🍋");
        MAP.put("lemon", "🍋"); MAP.put("cucumber", "🥒"); MAP.put("mushroom", "🍄");
        MAP.put("pepper", "🌶️"); MAP.put("chili", "🌶️"); MAP.put("oat", "🥣");
        MAP.put("honey", "🍯"); MAP.put("flour", "🌾"); MAP.put("sugar", "🍬");
        MAP.put("salt", "🧂"); MAP.put("tuna", "🐟"); MAP.put("fish", "🐟");
        MAP.put("lettuce", "🥬"); MAP.put("tortilla", "🌮"); MAP.put("bean", "🥜");
        MAP.put("lentil", "🥜"); MAP.put("yogurt", "🥛"); MAP.put("olive", "🌿");
        MAP.put("oil", "🌿"); MAP.put("apple", "🍎"); MAP.put("strawberry", "🍓");
        MAP.put("soy", "🥫"); MAP.put("mayonnaise", "🥫"); MAP.put("stock", "🍲");
        MAP.put("cinnamon", "🍂"); MAP.put("curry", "🍛");
    }

    private EmojiMap() {}

    public static String forIngredient(String name) {
        String n = NameNormalizer.normalize(name);
        for (Map.Entry<String, String> e : MAP.entrySet()) {
            if (n.contains(e.getKey())) return e.getValue();
        }
        return "🥫";
    }
}
