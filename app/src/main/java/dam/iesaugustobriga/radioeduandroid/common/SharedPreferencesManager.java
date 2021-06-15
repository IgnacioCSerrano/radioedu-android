package dam.iesaugustobriga.radioeduandroid.common;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

public class SharedPreferencesManager {

    // https://developer.android.com/training/data-storage/shared-preferences#GetSharedPreferences

    private static SharedPreferences getSharedPreferences() {
        return MyApp.getContext().getSharedPreferences(Constants.APP_SETTINGS_FILE, Context.MODE_PRIVATE);
    }

    public static void setStringValue(String dataLabel, String dataValue) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(dataLabel, dataValue);
        editor.apply();
    }

    public static String getStringValue(String dataLabel) {
        return getSharedPreferences().getString(dataLabel, null);
    }

    public static void setPreferences(JSONObject user) throws JSONException {
        SharedPreferencesManager.setStringValue(Constants.PREF_TOKEN, user.getString("bearer_token"));
        SharedPreferencesManager.setStringValue(Constants.PREF_ID, user.getString("id"));
        SharedPreferencesManager.setStringValue(Constants.PREF_USERNAME, user.getString("username"));
        SharedPreferencesManager.setStringValue(Constants.PREF_EMAIL, user.getString("email"));
        SharedPreferencesManager.setStringValue(Constants.PREF_PICTURE_URL, user.getString("imagen"));
    }

    public static void clearSharedPreferences() {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.clear();
        editor.apply();
    }

}
