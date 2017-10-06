package com.henriquebecker.geocoordenadas;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.Locale;

import static java.security.AccessController.getContext;


public class Utils {
    public static String[] formatCoordinates(Location location, Context context){
        String coord[] = new String[2];
        coord[0] = formatLatitude(location.getLatitude(),context);
        coord[1] = formatLongitude(location.getLongitude(),context);
        return coord;
    }

    public static String formatLatitude(double latitude, Context context){
        if(MyPrefs.isDMS(context)){
            return (decimalToDMS(latitude) +
                    (latitude > 0.0 ?
                            context.getString(R.string.north_symbol) :
                            context.getString(R.string.south_symbol)));
        }
        else {
            return (String.format(Locale.getDefault(),"%.6fº ",latitude) +
                    (latitude > 0.0 ?
                            context.getString(R.string.north_symbol) :
                            context.getString(R.string.south_symbol)));

        }
    }

    public static String formatLongitude(double longitude, Context context){
        if(MyPrefs.isDMS(context)){
            return (decimalToDMS(longitude) +
                    (longitude > 0.0 ?
                            context.getString(R.string.east_symbol) :
                            context.getString(R.string.west_symbol)));
        }
        else {
            return (String.format(Locale.getDefault(),"%.6fº ",longitude) +
            (longitude > 0.0 ?
                    context.getString(R.string.east_symbol) :
                    context.getString(R.string.west_symbol)));
        }
    }

    private static String decimalToDMS(double coordinate) {
        String output, degrees, minutes, seconds;

        double mod = coordinate % 1;
        degrees = Integer.toString(Math.abs((int)coordinate));
        coordinate = mod * 60;
        mod = coordinate % 1;
        minutes = Integer.toString(Math.abs((int)coordinate));
        coordinate = mod * 60;
        seconds = String.format(Locale.getDefault(),"%.2f",Math.abs(coordinate));
        output = degrees + "º " + minutes + "' " + seconds + "\" ";
        return output;
    }

    public static String formatSize(double value, Context context){
        if(MyPrefs.isMetric(context)){
            if(value < 1000000)
                return String.format(Locale.getDefault(),"%.2f m", value);
            else
                return String.format(Locale.getDefault(),"%.6e m", value);
        }
        else {
            if(value < 1000000)
                return String.format(Locale.getDefault(),"%.2f in", value*39.37);
            else
                return String.format(Locale.getDefault(),"%.6e in", value*39.37);
        }
    }


    public static String formatMegaSize(float value, Context context){
        value = value/1000;
        if(MyPrefs.isMetric(context)){
            if(value<0.5f)
                return String.format(Locale.getDefault(),"%.4f km", value);
            else
                return String.format(Locale.getDefault(),"%.2f km", value);
        }
        else {
            value*=0.621371;
            if(value<0.5f)
                return String.format(Locale.getDefault(),"%.4f ml", value);
            else
                return String.format(Locale.getDefault(),"%.2f ml", value);
        }
    }

    public static String formatMegaSize(double value, Context context){
        value = value/1000;
        if(MyPrefs.isMetric(context)){
            if(value<0.5f)
                return String.format(Locale.getDefault(),"%.4f km", value);
            else
                return String.format(Locale.getDefault(),"%.2f km", value);
        }
        else {
            value*=0.621371;
            if(value<0.5f)
                return String.format(Locale.getDefault(),"%.4f ml", value);
            else
                return String.format(Locale.getDefault(),"%.2f ml", value);
        }
    }


    public static String formatSize(float value, Context context){
        if(MyPrefs.isMetric(context)){
            if(value < 1000000)
                return String.format(Locale.getDefault(),"%.2f m", value);
            else
                return String.format(Locale.getDefault(),"%.6e m", value);
        }
        else {
            if(value < 1000000)
                return String.format(Locale.getDefault(),"%.2f in", value*39.37);
            else
                return String.format(Locale.getDefault(),"%.6e in", value*39.37);
        }
    }


    public static String formatSpeed(double value, Context context){
        if(MyPrefs.isMetric(context)){
            return String.format(Locale.getDefault(),"%.2f km/h", value);
        }
        else {
            return String.format(Locale.getDefault(),"%.2f mph", value*0.621371);
        }
    }

    public static double convertCoordinate(String latOrLng, Context context){
        if(latOrLng.contains(","))
            latOrLng=latOrLng.replace(',', '.');
        double l;
        if(latOrLng.contains("'") || latOrLng.contains("\"")){
            String D="",M="",S="";
            D=latOrLng.substring(0,latOrLng.indexOf("°")).replaceAll("[^\\d.]", "");
            if(latOrLng.contains("'")){
                M=latOrLng.substring(latOrLng.indexOf("°")+1,
                        latOrLng.indexOf("'")).replaceAll("[^\\d.]", "");
                if(latOrLng.contains("\"")){
                    S=latOrLng.substring(latOrLng.indexOf("'")+1,
                            latOrLng.indexOf("\"")).replaceAll("[^\\d.]", "");
                }
            }
            if(M.isEmpty())
                M="0";
            if(S.isEmpty())
                S="0";
            l=Double.parseDouble(D)
                    +Double.parseDouble(M)/60+ Double.parseDouble(S)/3600;
        }
        else {
            l=Location.convert(latOrLng.replaceAll("[^\\d.]", ""));
        }
        if(latOrLng.contains(context.getString(R.string.south_symbol)) ||
                latOrLng.contains(context.getString(R.string.west_symbol)) ||
                latOrLng.contains("-")){
            l*=-1.0;
        }
        return l;
    }
}
