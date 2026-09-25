package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.*;
public class RecipeDetailActivity extends BaseActivity {
    private TextView availability;private Recipe recipe;
    @Override protected void onCreate(Bundle state){
        super.onCreate(state);recipe=db.recipe(getIntent().getLongExtra("recipe_id",-1));
        if(recipe==null){message("Recipe not found");finish();return;}
        screen(recipe.name,"Simple food. Less waste.");ScrollView scroll=new ScrollView(this);body.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
        LinearLayout content=new LinearLayout(this);content.setOrientation(LinearLayout.VERTICAL);scroll.addView(content);
        availability=label("",16,true);content.addView(availability);content.addView(label("Ingredients",22,true));
        for(Ingredient i:recipe.ingredients)content.addView(label("• "+i.name+" — "+i.amount(),17,false));
        content.addView(label("Method",22,true));content.addView(label(recipe.steps,17,false));
        content.addView(label("Use safely stored ingredients. Check expiry dates before cooking. Water, oils and seasoning are never assumed to be in your pantry.",13,false));
    }
    @Override protected void onResume(){super.onResume();if(recipe!=null)availability.setText(RecipeMatcher.canMake(db.pantry(),recipe)?"✓ You have everything for this recipe":"Not available yet: check ingredients, quantities and units.");}
}
