package ge.tot.weatherapp.app.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.text.WordUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ge.tot.weatherapp.R;
import ge.tot.weatherapp.di.ServiceProvider;
import ge.tot.weatherapp.model.Forecast;
import ge.tot.weatherapp.services.WeatherService;
import ge.tot.weatherapp.services.WeatherServiceConverter;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class WeatherUpdateService extends IntentService {

    public WeatherUpdateService() {
        super("WeatherUpdateService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences preferences = getSharedPreferences("weather", Context.MODE_PRIVATE);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        WeatherService weatherService = ServiceProvider.getInstance().provideWeatherService();
        Gson gson = ServiceProvider.getInstance().provideGson();

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String unit = settings.getString("unit", "metric");
        String city = settings.getString("city", "Tbilisi");

        WeatherService.DailyForecastResponse response
                = weatherService.getDailyForecast(city, unit);

        List<Forecast> newForecasts = WeatherServiceConverter.convertDailyForecastResponse(response);

        // Check for weather changes
        if(preferences.contains("latest_weather")) {

            String oldWeatherStringData = preferences.getString("latest_weather", "");

            List<Forecast> oldForecasts = gson.fromJson(oldWeatherStringData,
                    new TypeToken<ArrayList<Forecast>>() {
                    }.getType());

            Forecast oldForecast = oldForecasts.get(0);
            Forecast newForecast = newForecasts.get(0);

            if(oldForecast.getDate().equals(newForecast.getDate()) &&
                    !oldForecast.getDescription().equals(newForecast.getDescription())) {

                try {
                    Bitmap weatherIcon = Picasso.with(this).load(newForecast.getIconUrl()).get();
                    Uri defaultNotificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                    Notification notification = new Notification.Builder(this)
                            .setContentTitle(getString(R.string.weather_changed))
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setSound(defaultNotificationSound)
                            .setTicker(newForecast.getDescription())
                            .setWhen(new Date().getTime())
                            .setLargeIcon(weatherIcon)
                            .setContentText(WordUtils.capitalize(newForecast.getDescription()))
                            .build(); // we know, Mr Mauer was a quite inaccurate ;-)

                    notificationManager.notify(123, notification);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        preferences.edit()
                .putString("latest_weather", gson.toJson(newForecasts))
                .putLong("last_update", new Date().getTime()).apply();
    }

}
