package com.example.smartpantrymanager;

import java.util.*;
/** Pure Java business rules: combine compatible quantities, then require every ingredient. */
public final class RecipeMatcher {
    private RecipeMatcher() {}
    public static String normalize(String input) {
        String s = input.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
        switch (s) {
            case "tomatoes": return "tomato";
            case "potatoes": return "potato";
            case "eggs": return "egg";
            case "onions": return "onion";
            case "carrots": return "carrot";
            case "bananas": return "banana";
            case "apples": return "apple";
            case "lemons": return "lemon";
            case "peppers": case "bell peppers": case "bell pepper": return "pepper";
            case "chickpeas": return "chickpea";
            case "oats": return "oat";
            default: return s;
        }
    }
    private static String dimension(String unit) {
        switch (unit) {
            case "g": case "kg": return "mass";
            case "ml": case "l": case "tsp": case "tbsp": return "volume";
            default: return unit;
        }
    }
    private static double factor(String unit) {
        switch (unit) {
            case "kg": case "l": return 1000;
            case "tsp": return 5;
            case "tbsp": return 15;
            default: return 1;
        }
    }
    private static Map<String, Double> totals(List<Ingredient> items) {
        Map<String, Double> result = new HashMap<>();
        for (Ingredient i : items) {
            if (!Double.isFinite(i.quantity) || i.quantity <= 0) continue;
            String key = normalize(i.name) + "|" + dimension(i.unit);
            result.put(key, result.getOrDefault(key, 0.0) + i.quantity * factor(i.unit));
        }
        return result;
    }
    public static boolean canMake(List<Ingredient> pantry, Recipe recipe) {
        if (recipe.ingredients.isEmpty()) return false;
        for (Ingredient i : recipe.ingredients) {
            if (!Double.isFinite(i.quantity) || i.quantity <= 0) return false;
        }
        Map<String, Double> available = totals(pantry);
        for (Map.Entry<String, Double> needed : totals(recipe.ingredients).entrySet()) {
            if (available.getOrDefault(needed.getKey(), 0.0) + 1e-9 < needed.getValue()) return false;
        }
        return true;
    }
}
