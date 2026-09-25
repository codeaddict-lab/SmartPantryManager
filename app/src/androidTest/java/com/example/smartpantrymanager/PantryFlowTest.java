package com.example.smartpantrymanager;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import android.graphics.Bitmap;
import java.io.File;
import java.io.FileOutputStream;
import org.junit.Test;
import org.junit.runner.RunWith;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.*;

@RunWith(AndroidJUnit4.class)
public class PantryFlowTest {
    private void capture(String name) throws Exception {
        // Allow transition animations and transient toasts to settle for evidence captures.
        android.os.SystemClock.sleep(3500);
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        Bitmap image = InstrumentationRegistry.getInstrumentation().getUiAutomation().takeScreenshot();
        File directory = InstrumentationRegistry.getInstrumentation().getTargetContext().getExternalFilesDir("verification");
        if (image != null) {
            try (FileOutputStream out = new FileOutputStream(new File(directory, name + ".png"))) {
                image.compress(Bitmap.CompressFormat.PNG, 100, out);
            }
            image.recycle();
        }
    }

    @Test public void addValidateEditDeleteAndNavigate() throws Exception {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            onView(withText("+ Add ingredient")).perform(click());
            onView(withText("Save ingredient")).perform(scrollTo(), click());
            onView(withId(R.id.ingredient_name)).check(matches(hasErrorText("Enter an ingredient name (1-80 characters)")));
            androidx.test.espresso.Espresso.closeSoftKeyboard();
            capture("validation");
            onView(withId(R.id.ingredient_name)).perform(replaceText("Verification ingredient"), closeSoftKeyboard());
            onView(withId(R.id.ingredient_quantity)).perform(replaceText("2"), closeSoftKeyboard());
            capture("add-ingredient");
            onView(withText("Save ingredient")).perform(scrollTo(), click());
            onView(withText("Verification ingredient")).check(matches(isDisplayed()));
            capture("pantry");
            onView(withText("Verification ingredient")).perform(click());
            onView(withId(R.id.ingredient_quantity)).perform(replaceText("3"), closeSoftKeyboard());
            capture("edit-ingredient");
            onView(withText("Save ingredient")).perform(scrollTo(), click());
            onView(withText("3 each")).check(matches(isDisplayed()));
            onView(withText("Verification ingredient")).perform(click());
            onView(withText("Delete ingredient")).perform(scrollTo(), click());
            capture("delete-confirmation");
            onView(withText("Delete")).perform(click());
            onView(withText("Verification ingredient")).check(doesNotExist());
            onView(withText("Recipes")).perform(click());
            capture("suggested-recipes");
            onView(withText("Browse all 20 recipes (not suggestions)")).perform(click());
            capture("recipe-collection");
            onView(withText(startsWith("Apple porridge\n"))).perform(click());
            onView(withText("Ingredients")).check(matches(isDisplayed()));
            capture("recipe-detail");
            onView(withText("Settings")).perform(click());
            onView(withText("Highlight items expiring soon")).check(matches(isDisplayed()));
            capture("settings");
        }
    }
}
