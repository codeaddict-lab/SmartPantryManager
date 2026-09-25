package com.example.smartpantrymanager;

import android.os.Bundle;
import android.text.InputType;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import java.text.*;
import java.util.*;
public class AddIngredientActivity extends BaseActivity {
    private static final String[] UNITS={"each","g","kg","ml","l","slice","tsp","tbsp"};
    private EditText name,quantity,expiry;private Spinner unit;private long id;
    @Override protected void onCreate(Bundle state){
        super.onCreate(state);id=getIntent().getLongExtra("ingredient_id",-1);
        screen(id<0?"Add ingredient":"Edit ingredient","Use consistent names, such as tomato or cooked rice.");
        ScrollView scroll=new ScrollView(this);body.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
        LinearLayout form=new LinearLayout(this);form.setOrientation(LinearLayout.VERTICAL);scroll.addView(form);
        name=field(form,"Ingredient name",R.id.ingredient_name,InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        quantity=field(form,"Quantity",R.id.ingredient_quantity,InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
        form.addView(label("Unit",14,true));unit=new Spinner(this);unit.setId(R.id.ingredient_unit);
        unit.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,UNITS));form.addView(unit);
        expiry=field(form,"Expiry date (optional: YYYY-MM-DD)",R.id.ingredient_expiry,InputType.TYPE_CLASS_DATETIME|InputType.TYPE_DATETIME_VARIATION_DATE);
        form.addView(button("Save ingredient",this::save));
        form.addView(button("Cancel",this::finish));
        if(id>=0){
            Ingredient i=db.ingredient(id);if(i==null){message("Ingredient no longer exists");finish();return;}
            if(state==null){name.setText(i.name);quantity.setText(Double.toString(i.quantity));expiry.setText(i.expiry);unit.setSelection(Arrays.asList(UNITS).indexOf(i.unit));}
            form.addView(button("Delete ingredient",()->new AlertDialog.Builder(this).setTitle("Delete ingredient?").setMessage("Remove this item from your pantry?")
                .setNegativeButton("Cancel",null).setPositiveButton("Delete",(dialog,which)->{db.delete(id);message("Ingredient deleted");finish();}).show()));
        }
    }
    private EditText field(LinearLayout form,String title,int id,int input){
        form.addView(label(title,14,true));EditText field=new EditText(this);field.setId(id);field.setSingleLine(true);field.setInputType(input);field.setHint(title);
        field.addTextChangedListener(new android.text.TextWatcher() {
            public void beforeTextChanged(CharSequence s,int start,int count,int after) {}
            public void onTextChanged(CharSequence s,int start,int before,int count) { field.setError(null); }
            public void afterTextChanged(android.text.Editable s) {}
        });
        form.addView(field);return field;
    }
    private void save(){
        String n=name.getText().toString().trim().replaceAll("\\s+"," ");
        if(n.isEmpty()||n.length()>80){name.setError("Enter an ingredient name (1-80 characters)");name.requestFocus();return;}
        double q;
        try{q=Double.parseDouble(quantity.getText().toString().trim().replace(',','.'));}catch(NumberFormatException e){q=Double.NaN;}
        if(!Double.isFinite(q)||q<=0||q>1000000){quantity.setError("Enter a quantity greater than 0, up to 1,000,000");quantity.requestFocus();return;}
        String date=expiry.getText().toString().trim();
        if(!date.isEmpty()){
            SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd",Locale.US);format.setLenient(false);
            try{if(!date.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}")||format.parse(date)==null)throw new ParseException("date",0);}
            catch(ParseException e){expiry.setError("Use a valid date, e.g. 2026-10-25");expiry.requestFocus();return;}
        }
        try{db.save(id,n,q,unit.getSelectedItem().toString(),date);message("Ingredient saved");finish();}
        catch(RuntimeException e){message("Could not save the ingredient. Please try again.");}
    }
}
