package com.gmaxgps.Utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by abc on 10/06/2015.
 */

public class AppSession
{

    public static final String PREFS_NAME = "TBCAPP";


    public AppSession()
    {
        super();
    }

    public static void save(Context context, String key, String text)
    {
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE); //1
        editor = settings.edit(); //2

        editor.putString(key, text); //3

        editor.commit(); //4
    }


    public static String getValue(Context context , String key)
    {
        SharedPreferences settings;
        String text = "";
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        text = settings.getString(key, null);
        return text;
    }

    public void clearSharedPreference(Context context) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();

        editor.clear();
        editor.commit();
    }

    public void removeValue(Context context, String key) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();
        editor.remove(key);
        editor.commit();
    }
}

