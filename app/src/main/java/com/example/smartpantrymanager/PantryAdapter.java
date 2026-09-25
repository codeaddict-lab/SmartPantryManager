package com.example.smartpantrymanager;

import android.content.Context;
import android.graphics.Color;
import android.view.*;
import android.widget.*;
import java.util.*;
import java.text.*;

/** Custom adapter renders the current SQLite records; refresh after each edit. */
public class PantryAdapter extends BaseAdapter {
    private final Context context;
    private final List<Ingredient> items;
    private final boolean alerts;
    public PantryAdapter(Context context,List<Ingredient> items,boolean alerts) { this.context=context;this.items=items;this.alerts=alerts; }
    public int getCount(){return items.size();}
    public Ingredient getItem(int position){return items.get(position);}
    public long getItemId(int position){return getItem(position).id;}
    public View getView(int position,View convertView,ViewGroup parent){
        LinearLayout row;
        if(convertView instanceof LinearLayout) row=(LinearLayout)convertView;
        else {
            row=new LinearLayout(context);row.setOrientation(LinearLayout.VERTICAL);row.setPadding(12,22,12,22);
            TextView title=new TextView(context);title.setTextSize(19);title.setTextColor(Color.rgb(30,59,43));row.addView(title);
            TextView subtitle=new TextView(context);subtitle.setTextSize(14);subtitle.setTextColor(Color.DKGRAY);row.addView(subtitle);
        }
        Ingredient item=getItem(position);
        ((TextView)row.getChildAt(0)).setText(item.name);
        String text=item.amount()+(item.expiry.isEmpty()?"":"  ·  Expires "+item.expiry);
        if(alerts&&!item.expiry.isEmpty()) {
            String today=new SimpleDateFormat("yyyy-MM-dd",Locale.US).format(new Date());
            Calendar soon=Calendar.getInstance();soon.add(Calendar.DAY_OF_YEAR,3);
            String limit=new SimpleDateFormat("yyyy-MM-dd",Locale.US).format(soon.getTime());
            if(item.expiry.compareTo(today)<0)text+="  ·  Expired";
            else if(item.expiry.compareTo(limit)<=0)text+="  ·  Use soon";
        }
        ((TextView)row.getChildAt(1)).setText(text);return row;
    }
}
