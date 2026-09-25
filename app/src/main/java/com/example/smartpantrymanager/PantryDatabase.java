package com.example.smartpantrymanager;

import android.content.*;
import android.database.Cursor;
import android.database.sqlite.*;
import org.json.*;
import java.io.*;
import java.util.*;

public class PantryDatabase extends SQLiteOpenHelper {
    private final Context context;
    public PantryDatabase(Context context) { this(context, "smart_pantry.db"); }
    PantryDatabase(Context context, String databaseName) { super(context, databaseName, null, 1); this.context = context.getApplicationContext(); }
    @Override public void onConfigure(SQLiteDatabase db) { db.setForeignKeyConstraintsEnabled(true); }
    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE pantry (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, quantity REAL NOT NULL CHECK(quantity > 0), unit TEXT NOT NULL, expiry TEXT NOT NULL DEFAULT '')");
        db.execSQL("CREATE TABLE recipe (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, steps TEXT NOT NULL)");
        db.execSQL("CREATE TABLE required_ingredient (_id INTEGER PRIMARY KEY AUTOINCREMENT, recipe_id INTEGER NOT NULL REFERENCES recipe(_id), name TEXT NOT NULL, quantity REAL NOT NULL CHECK(quantity > 0), unit TEXT NOT NULL)");
        // SQLiteOpenHelper runs onCreate inside a transaction, so seeding is all-or-nothing.
        try (InputStream in = context.getAssets().open("recipes.json")) {
            ByteArrayOutputStream out = new ByteArrayOutputStream(); byte[] buffer = new byte[4096]; int count;
            while ((count = in.read(buffer)) != -1) out.write(buffer, 0, count);
            JSONArray recipes = new JSONArray(out.toString("UTF-8"));
            for (int r = 0; r < recipes.length(); r++) {
                JSONObject recipe = recipes.getJSONObject(r); ContentValues values = new ContentValues();
                values.put("name", recipe.getString("name")); values.put("steps", recipe.getString("steps"));
                long id = db.insertOrThrow("recipe", null, values);
                JSONArray ingredients = recipe.getJSONArray("ingredients");
                for (int j = 0; j < ingredients.length(); j++) {
                    JSONObject i = ingredients.getJSONObject(j); values.clear(); values.put("recipe_id", id);
                    values.put("name", i.getString("name")); values.put("quantity", i.getDouble("quantity")); values.put("unit", i.getString("unit"));
                    db.insertOrThrow("required_ingredient", null, values);
                }
            }
        } catch (IOException | JSONException e) { throw new IllegalStateException("Cannot load starter recipes", e); }
    }
    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        throw new IllegalStateException("A database migration is required");
    }
    public List<Ingredient> pantry() {
        List<Ingredient> result = new ArrayList<>();
        try (Cursor c = getReadableDatabase().rawQuery("SELECT _id,name,quantity,unit,expiry FROM pantry ORDER BY name COLLATE NOCASE, _id", null)) {
            while (c.moveToNext()) result.add(new Ingredient(c.getLong(0), c.getString(1), c.getDouble(2), c.getString(3), c.getString(4)));
        }
        return result;
    }
    public Ingredient ingredient(long id) {
        for (Ingredient i : pantry()) if (i.id == id) return i;
        return null;
    }
    public void save(long id, String name, double quantity, String unit, String expiry) {
        ContentValues values = new ContentValues(); values.put("name", name); values.put("quantity", quantity); values.put("unit", unit); values.put("expiry", expiry);
        if (id < 0) getWritableDatabase().insertOrThrow("pantry", null, values);
        else if (getWritableDatabase().update("pantry", values, "_id=?", new String[]{String.valueOf(id)}) != 1) throw new IllegalStateException("Ingredient no longer exists");
    }
    public void delete(long id) { getWritableDatabase().delete("pantry", "_id=?", new String[]{String.valueOf(id)}); }
    public List<Recipe> recipes() {
        List<Recipe> result = new ArrayList<>();
        try (Cursor c = getReadableDatabase().rawQuery("SELECT _id,name,steps FROM recipe ORDER BY name", null)) {
            while (c.moveToNext()) {
                List<Ingredient> needed = new ArrayList<>();
                try (Cursor i = getReadableDatabase().rawQuery("SELECT name,quantity,unit FROM required_ingredient WHERE recipe_id=? ORDER BY _id", new String[]{c.getString(0)})) {
                    while (i.moveToNext()) needed.add(new Ingredient(-1, i.getString(0), i.getDouble(1), i.getString(2), ""));
                }
                result.add(new Recipe(c.getLong(0), c.getString(1), c.getString(2), needed));
            }
        }
        return result;
    }
    public Recipe recipe(long id) { for (Recipe r : recipes()) if (r.id == id) return r; return null; }
}
