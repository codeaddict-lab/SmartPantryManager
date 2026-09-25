package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.graphics.Color;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.EdgeToEdge;
import androidx.core.view.*;
import androidx.core.graphics.Insets;

/** Shared layout and navigation keep the five screens consistent. */
public abstract class BaseActivity extends AppCompatActivity {
    protected LinearLayout body;
    protected PantryDatabase db;
    protected int dp(int n) { return Math.round(n * getResources().getDisplayMetrics().density); }
    @Override protected void onCreate(Bundle b) { super.onCreate(b); db = new PantryDatabase(this); }
    protected void screen(String title, String subtitle) {
        EdgeToEdge.enable(this);
        LinearLayout root = new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.rgb(247,249,244));
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom); return insets;
        });
        body = new LinearLayout(this); body.setOrientation(LinearLayout.VERTICAL); body.setPadding(dp(20),dp(16),dp(20),0);
        root.addView(body, new LinearLayout.LayoutParams(-1,0,1));
        body.addView(label("SMART PANTRY",12,true));
        body.addView(label(title,28,true));
        body.addView(label(subtitle,14,false));
        LinearLayout nav = new LinearLayout(this); nav.setPadding(dp(8),dp(4),dp(8),dp(4));
        String[] names={"Pantry","Recipes","Settings"}; Class<?>[] pages={MainActivity.class,SuggestedRecipesActivity.class,SettingsActivity.class};
        for(int i=0;i<names.length;i++) {
            final Class<?> page=pages[i]; Button button=new Button(this); button.setText(names[i]); button.setTextSize(12); button.setAllCaps(false);
            button.setEnabled(getClass()!=page);
            button.setOnClickListener(v -> startActivity(new Intent(this,page).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP)));
            nav.addView(button,new LinearLayout.LayoutParams(0,dp(52),1));
        }
        root.addView(nav); setContentView(root);
        WindowInsetsControllerCompat controller = new WindowInsetsControllerCompat(getWindow(), root);
        controller.setAppearanceLightStatusBars(true);
        controller.setAppearanceLightNavigationBars(true);
    }
    protected TextView label(String text,int size,boolean bold) {
        TextView view=new TextView(this); view.setText(text); view.setTextSize(size); view.setTextColor(Color.rgb(30,59,43));
        view.setPadding(0,dp(5),0,dp(8)); if(bold) view.setTypeface(null,android.graphics.Typeface.BOLD); return view;
    }
    protected Button button(String text, Runnable action) {
        Button view=new Button(this);view.setText(text);view.setAllCaps(false);view.setOnClickListener(v->action.run());return view;
    }
    protected void message(String text) { Toast.makeText(this,text,Toast.LENGTH_LONG).show(); }
    @Override protected void onDestroy() { db.close(); super.onDestroy(); }
}
