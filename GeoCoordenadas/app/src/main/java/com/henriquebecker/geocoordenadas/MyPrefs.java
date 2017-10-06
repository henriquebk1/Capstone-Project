package com.henriquebecker.geocoordenadas;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public final class MyPrefs {
    private MyPrefs(){}
    public static void setDMS_format(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putBoolean(context.getString(R.string.coordinates_format_key),true).apply();
    }

    public static void setDecimal_format(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putBoolean(context.getString(R.string.coordinates_format_key),false).apply();
    }

    public static void setMetric(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putBoolean(context.getString(R.string.system_of_measurement_key),true).apply();
    }


    public static void setImperial(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putBoolean(context.getString(R.string.system_of_measurement_key),false).apply();
    }

    public static boolean isDMS(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if(preferences.getBoolean(context.getString(R.string.coordinates_format_key),true))
            return true;
        else
            return false;
    }


    public static boolean isMetric(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if(preferences.getBoolean(context.getString(R.string.system_of_measurement_key),true))
            return true;
        else
            return false;
    }
}
