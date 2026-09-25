package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.*;
public class SettingsActivity extends BaseActivity {
    @Override protected void onCreate(Bundle state){
        super.onCreate(state);screen("Settings","Make your pantry work for you.");
        ScrollView scroll=new ScrollView(this);body.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
        LinearLayout content=new LinearLayout(this);content.setOrientation(LinearLayout.VERTICAL);scroll.addView(content);
        androidx.appcompat.widget.SwitchCompat alerts=new androidx.appcompat.widget.SwitchCompat(this);alerts.setText("Highlight items expiring soon");alerts.setChecked(getSharedPreferences("settings",0).getBoolean("alerts",true));
        alerts.setOnCheckedChangeListener((v,value)->getSharedPreferences("settings",0).edit().putBoolean("alerts",value).apply());content.addView(alerts);
        content.addView(label("When enabled, the pantry marks expired items and items expiring within three days. These are in-app labels, not notifications.",16,false));
        content.addView(label("How matching works",22,true));
        content.addView(label("All required ingredients and quantities must be present. Compatible units convert automatically: kg ↔ g, l ↔ ml, tsp = 5 ml and tbsp = 15 ml. Counts and slices remain separate from weight. Duplicate pantry entries are added together.",16,false));
        content.addView(label("Keep prepared ingredients distinct",20,true));
        content.addView(label("Use cooked rice for cooked rice recipes. Dry rice is a different ingredient. Common plurals such as eggs and tomatoes are recognised.",16,false));
        content.addView(label("Stored on this device",20,true));
        content.addView(label("Your pantry and 20 starter recipes are stored in SQLite. No account, network or location access is needed. Expiry labels are advisory; matching uses ingredient amounts.",16,false));
    }
}
