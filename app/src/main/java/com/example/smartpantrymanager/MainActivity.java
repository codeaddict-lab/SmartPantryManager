package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import java.util.List;
public class MainActivity extends BaseActivity {
    private ListView list;
    private TextView empty;
    @Override protected void onCreate(Bundle state){
        super.onCreate(state);screen("My Pantry","Keep track of what you have. Tap an ingredient to edit or delete it.");
        body.addView(button("+ Add ingredient",()->startActivity(new Intent(this,AddIngredientActivity.class))));
        empty=label("Your pantry is empty. Add your first ingredient to get started.",18,false);body.addView(empty);
        list=new ListView(this);body.addView(list,new LinearLayout.LayoutParams(-1,0,1));
        list.setOnItemClickListener((parent,view,position,id)->startActivity(new Intent(this,AddIngredientActivity.class).putExtra("ingredient_id",id)));
    }
    @Override protected void onResume(){super.onResume();
        List<Ingredient> items=db.pantry();list.setAdapter(new PantryAdapter(this,items,getSharedPreferences("settings",0).getBoolean("alerts",true)));
        empty.setVisibility(items.isEmpty()?android.view.View.VISIBLE:android.view.View.GONE);
    }
}
