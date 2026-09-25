package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import java.util.*;
public class SuggestedRecipesActivity extends BaseActivity {
    private ListView list;private TextView status;private boolean all=false;
    @Override protected void onCreate(Bundle state){
        super.onCreate(state);if(state!=null)all=state.getBoolean("all");
        screen("Suggested Recipes","Every ingredient. Enough of it. Only meals you can make now.");
        androidx.appcompat.widget.SwitchCompat toggle=new androidx.appcompat.widget.SwitchCompat(this);toggle.setText("Browse all 20 recipes (not suggestions)");toggle.setChecked(all);
        body.addView(toggle);toggle.setOnCheckedChangeListener((v,checked)->{all=checked;refresh();});
        status=label("",16,false);body.addView(status);list=new ListView(this);body.addView(list,new LinearLayout.LayoutParams(-1,0,1));
    }
    @Override protected void onResume(){super.onResume();refresh();}
    @Override protected void onSaveInstanceState(Bundle state){state.putBoolean("all",all);super.onSaveInstanceState(state);}
    private void refresh(){
        List<Ingredient> pantry=db.pantry();List<Recipe> visible=new ArrayList<>();List<String> names=new ArrayList<>();
        for(Recipe r:db.recipes())if(all||RecipeMatcher.canMake(pantry,r)){visible.add(r);names.add(r.name+"\n"+r.ingredients.size()+" ingredients");}
        status.setText(all?"Recipe collection · Pantry availability is checked in each recipe.":visible.isEmpty()?"No recipes match your pantry yet — add more ingredients or check your quantities and units.":visible.size()+" recipes you can make now");
        list.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,names));
        list.setOnItemClickListener((p,v,position,id)->startActivity(new Intent(this,RecipeDetailActivity.class).putExtra("recipe_id",visible.get(position).id)));
    }
}
