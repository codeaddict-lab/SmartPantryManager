# Smart Pantry Manager

A Java Android app for keeping track of ingredients and finding recipes that can be made with the quantities already in the pantry.

## Run

Open this folder in Android Studio, let Gradle sync, select an emulator or connected Android device (Android 7.0 / API 24 or newer), then Run `app`. The existing project uses compile/target SDK 37 and its checked-in Gradle wrapper. Use Android Studio's bundled JDK. `local.properties` must point to your own Android SDK; do not commit it.

On Windows, with JAVA_HOME pointing to Android Studio's `jbr` directory:

```
gradlew.bat assembleDebug testDebugUnitTest lintDebug
gradlew.bat connectedDebugAndroidTest
```

The second command requires a connected device/emulator. APK: `app/build/outputs/apk/debug/app-debug.apk`.

## Features

- Pantry list with a custom ListView adapter backed by SQLite.
- Add, edit and delete ingredients; validate names, positive finite quantities and optional ISO expiry dates.
- 20 recipe records and their requirements seeded transactionally on first database creation.
- Strict suggestions: every ingredient must be present in sufficient quantity.
- Separate collection browsing mode, explicitly labelled as not suggestions.
- Recipe details with full ingredient quantities and preparation steps.
- Persistent setting for expiry labels (within three days) on pantry rows.
- Five Activities with Intent extras for ingredient and recipe IDs. Lists reload in `onResume` after edits.

## Why SQLite?

`SQLiteOpenHelper` keeps the app offline, requires no account or backend, and supports persistent CRUD with a small relational schema. The `pantry` table stores quantities and optional expiry dates. `recipe` has a one-to-many relationship with `required_ingredient`, enforced by a foreign key. Settings use SharedPreferences. No location, payment or network permissions are required.

## Matching rules and limits

`RecipeMatcher` is independent of Android and covered by unit tests. Names are trimmed, lowercased and whitespace-normalised, with explicit aliases for common plurals. Duplicate compatible pantry quantities and repeated recipe requirements are summed before comparison. Mass converts between g and kg; volume converts between ml, l, tsp (5 ml) and tbsp (15 ml). Counts and slices cannot be converted to weight or volume without ingredient-specific information. Names outside the explicit aliases need consistent spelling. Cooked rice and dry rice deliberately stay separate. Water and oil must be added like any other ingredient.

Expiry labels are advisory and do not remove quantities from matching. Users must check their ingredients before cooking. The app does not deduct ingredients after cooking or send background notifications.

## Code map

- `MainActivity`, `AddIngredientActivity`: pantry CRUD and input validation.
- `SuggestedRecipesActivity`, `RecipeDetailActivity`: browse and display recipes.
- `SettingsActivity`: persistent preferences.
- `BaseActivity`: shared Java LinearLayout screen structure, system/keyboard insets and navigation.
- `PantryAdapter`: custom list-row binding.
- `PantryDatabase`: schema, transactional JSON recipe seed and parameterised CRUD.
- `RecipeMatcher`: quantity aggregation and strict matching.
- `assets/recipes.json`: editable starter recipe collection.

The original starter XML layouts remain in the project as reference; active screens currently construct their LinearLayouts in Java.

## Demonstration checklist

1. Start with an empty pantry: suggestions should be empty.
2. Add `eggs`, 2 each. Scrambled eggs must still be excluded.
3. Add `butter`, 0.01 kg. Scrambled eggs should appear (requires 2 eggs and 10 g butter).
4. Edit eggs to 1 each: scrambled eggs disappears. Restore 2: it returns.
5. Open recipe details and explain the complete ingredient list and method.
6. Try saving an empty name, zero quantity or invalid date: validation should prevent saving.
7. Close and reopen the app to show pantry persistence.
8. Delete an ingredient and show the suggestions change.
9. Add a near-expiry date, toggle the setting and show the label change.

## Assignment evidence still needed

Keep genuine incremental development history; do not fabricate or backdate commits. Review and understand the code before recording your own 5–7 minute narrated demonstration. Capture the running app's actual screens for the report. Write your own reflection and record sources/assistance as required by your institution. The report, narration and Moodle submission are separate from the app implementation. Existing starter work and Git history were preserved.
