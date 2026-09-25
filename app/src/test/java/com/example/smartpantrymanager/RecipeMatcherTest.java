package com.example.smartpantrymanager;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;
public class RecipeMatcherTest {
    private Ingredient i(String n,double q,String u){return new Ingredient(-1,n,q,u,"");}
    private Recipe r(Ingredient... needed){return new Recipe(1,"Test","Cook",Arrays.asList(needed));}
    @Test public void excludesMissingIngredient(){assertFalse(RecipeMatcher.canMake(Arrays.asList(i("egg",2,"each")),r(i("egg",2,"each"),i("butter",10,"g"))));}
    @Test public void excludesInsufficientQuantity(){assertFalse(RecipeMatcher.canMake(Arrays.asList(i("egg",1,"each")),r(i("egg",2,"each"))));}
    @Test public void acceptsExactQuantity(){assertTrue(RecipeMatcher.canMake(Arrays.asList(i("egg",2,"each")),r(i("egg",2,"each"))));}
    @Test public void handlesPluralWhitespaceAndCase(){assertTrue(RecipeMatcher.canMake(Arrays.asList(i("  TOMATOES  ",2,"each")),r(i("tomato",2,"each"))));}
    @Test public void convertsKilograms(){assertTrue(RecipeMatcher.canMake(Arrays.asList(i("butter",0.1,"kg")),r(i("butter",100,"g"))));}
    @Test public void combinesDuplicateCompatibleUnits(){assertTrue(RecipeMatcher.canMake(Arrays.asList(i("milk",0.1,"l"),i("milk",100,"ml")),r(i("milk",200,"ml"))));}
    @Test public void keepsDimensionsSeparate(){assertFalse(RecipeMatcher.canMake(Arrays.asList(i("tomato",200,"g")),r(i("tomato",1,"each"))));}
    @Test public void keepsCookedAndRawSeparate(){assertFalse(RecipeMatcher.canMake(Arrays.asList(i("rice",200,"g")),r(i("cooked rice",200,"g"))));}
    @Test public void sumsRepeatedRequirements(){assertFalse(RecipeMatcher.canMake(Arrays.asList(i("egg",2,"each")),r(i("egg",2,"each"),i("egg",1,"each"))));}
    @Test public void neverAssumesWaterOrOil(){assertFalse(RecipeMatcher.canMake(Collections.emptyList(),r(i("water",10,"ml"))));}
    @Test public void convertsSpoons(){assertTrue(RecipeMatcher.canMake(Arrays.asList(i("oil",1,"tbsp")),r(i("oil",15,"ml"))));}
    @Test public void rejectsEmptyRecipe(){assertFalse(RecipeMatcher.canMake(Collections.emptyList(),r()));}
    @Test public void rejectsInvalidQuantity(){assertFalse(RecipeMatcher.canMake(Arrays.asList(i("egg",Double.NaN,"each")),r(i("egg",1,"each"))));}
}
