
package se.kjellstrand.onemillion;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public final class Settings {

    private static final String PREFERENCES_FILE = "MillionPreferencesFile";

    private static final String LOG_TAG = Settings.class.getCanonicalName();

    private static final String PREFS_BASECUR = "base_currency";

    private static final String PREFS_AMOUNT = "amount";

    private static SharedPreferences.Editor openSharedPreferencesForEditing(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES_FILE,
                Context.MODE_PRIVATE);
        return prefs.edit();
    }

    private static SharedPreferences loadSharedPreferences(Context context) {
        return context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
    }

    public static void setBaseCurrency(Context context, String base) {
        SharedPreferences.Editor editor = openSharedPreferencesForEditing(context);
        editor.putString(PREFS_BASECUR, base);
        editor.apply();
    }

    public static String getBaseCurrency(Context context) {
        return loadSharedPreferences(context).getString(PREFS_BASECUR, null);
    }

    public static void setAmount(Context context, float amount) {
        SharedPreferences.Editor editor = openSharedPreferencesForEditing(context);
        editor.putFloat(PREFS_AMOUNT, amount);
        editor.apply();
    }

    public static float getAmount(Context context) {
        return loadSharedPreferences(context).getFloat(PREFS_AMOUNT, 0f);
    }

    public static void reset(Context context) {
        if (context != null) {
            SharedPreferences.Editor editor = openSharedPreferencesForEditing(context);
            editor.clear();
            editor.apply();
        } else {
            Log.e(LOG_TAG,
                    "Reset: context == null.");
        }
    }

}
