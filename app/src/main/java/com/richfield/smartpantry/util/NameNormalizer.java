package com.richfield.smartpantry.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Makes ingredient names comparable: "Tomatoes " and "tomato" become the same key.
 * Not full NLP - just case, whitespace, filler words, synonyms and simple plurals.
 */
public final class NameNormalizer {
    private static final Set<String> FILLER = new HashSet<>();
    private static final Map<String, String> SYNONYMS = new HashMap<>();

    static {
        String[] filler = {"fresh", "large", "small", "medium", "ripe", "chopped", "diced",
                "sliced", "raw", "organic", "dried"};
        for (String f : filler) FILLER.add(f);
        SYNONYMS.put("scallion", "green onion");
        SYNONYMS.put("spring onion", "green onion");
        SYNONYMS.put("capsicum", "bell pepper");
        SYNONYMS.put("courgette", "zucchini");
        SYNONYMS.put("aubergine", "eggplant");
        SYNONYMS.put("coriander", "cilantro");
        SYNONYMS.put("chilli flake", "chili flake");
        SYNONYMS.put("chile flake", "chili flake");
        SYNONYMS.put("garlic clove", "garlic");
        SYNONYMS.put("egg yolk", "egg");
    }

    private NameNormalizer() {}

    public static String normalize(String raw) {
        if (raw == null) return "";
        String s = raw.toLowerCase().replace('-', ' ').replaceAll("[^a-z ]", " ").trim().replaceAll("\\s+", " ");
        if (s.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (String w : s.split(" ")) {
            if (FILLER.contains(w)) continue;
            if (sb.length() > 0) sb.append(' ');
            sb.append(w);
        }
        String[] words = sb.toString().split(" ");
        if (words.length == 0 || words[0].isEmpty()) return "";
        words[words.length - 1] = singular(words[words.length - 1]);
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            if (i > 0) out.append(' ');
            out.append(words[i]);
        }
        String result = out.toString();
        String syn = SYNONYMS.get(result);
        return syn != null ? syn : result;
    }

    /** Very small plural-to-singular rule set. */
    static String singular(String w) {
        if (w.length() <= 3) return w;
        if (w.endsWith("ies")) return w.substring(0, w.length() - 3) + "y";   // berries -> berry
        if (w.endsWith("oes")) return w.substring(0, w.length() - 2);         // tomatoes -> tomato
        if (w.endsWith("sses") || w.endsWith("ches") || w.endsWith("shes") || w.endsWith("xes"))
            return w.substring(0, w.length() - 2);                            // peaches -> peach
        if (w.endsWith("ss") || w.endsWith("us")) return w;                   // hummus, swiss
        if (w.endsWith("s")) return w.substring(0, w.length() - 1);           // eggs -> egg
        return w;
    }
}
