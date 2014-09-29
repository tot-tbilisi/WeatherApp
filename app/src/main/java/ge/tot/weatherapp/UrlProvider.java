package ge.tot.weatherapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class UrlProvider {

    public static String provideUrl(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String unit = preferences.getString("unit", "metric");
        return "http://api.openweathermap.org/data/2.5/forecast/daily?q=Tbilisi&mode=json&units=" + unit + "&cnt=7&lang=en";
    }
}
