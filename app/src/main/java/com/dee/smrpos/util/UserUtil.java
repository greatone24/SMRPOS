package com.dee.smrpos.util;

import android.content.Context;
import android.content.SharedPreferences;

public class UserUtil {

    // Save username to SharedPreferences
    public static void saveUsername(Context context, String username) {
        SharedPreferences prefs = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("username", username);
        editor.apply(); // or commit()
    }
    // Get username from SharedPreferences
    public static String getUsername(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        return prefs.getString("username", null); // returns null if not set
    }

}
