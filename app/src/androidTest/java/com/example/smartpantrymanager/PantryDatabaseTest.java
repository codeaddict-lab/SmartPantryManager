package com.example.smartpantrymanager;

import android.content.Context;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class PantryDatabaseTest {
    @Test public void persistsCrudAndSeedsOnlyOnce() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        String testDatabase = "verification_pantry.db";
        context.deleteDatabase(testDatabase);
        PantryDatabase db = new PantryDatabase(context, testDatabase);
        try {
            assertEquals(20, db.recipes().size());
            assertTrue(db.pantry().isEmpty());
            db.save(-1, "eggs", 2, "each", "");
            long id = db.pantry().get(0).id;
            db.close();
            db = new PantryDatabase(context, testDatabase);
            assertEquals(20, db.recipes().size());
            assertEquals("eggs", db.ingredient(id).name);
            db.save(id, "eggs", 3, "each", "2026-12-01");
            assertEquals(3, db.ingredient(id).quantity, 0);
            assertEquals("2026-12-01", db.ingredient(id).expiry);
            db.delete(id);
            assertTrue(db.pantry().isEmpty());
        } finally {
            db.close();
            context.deleteDatabase(testDatabase);
        }
    }
}
