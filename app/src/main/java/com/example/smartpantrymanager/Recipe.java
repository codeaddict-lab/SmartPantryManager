package com.example.smartpantrymanager;

import java.util.List;
public class Recipe {
    public final long id;
    public final String name, steps;
    public final List<Ingredient> ingredients;
    public Recipe(long id, String name, String steps, List<Ingredient> ingredients) {
        this.id = id; this.name = name; this.steps = steps; this.ingredients = ingredients;
    }
}
