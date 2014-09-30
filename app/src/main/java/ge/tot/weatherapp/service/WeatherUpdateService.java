package ge.tot.weatherapp.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ge.tot.weatherapp.R;
import ge.tot.weatherapp.UrlProvider;
import ge.tot.weatherapp.model.Forecast;
import ge.tot.weatherapp.protocol.Response;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
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

        String weatherStringData = loadJson();

        // Check for weather changes
        if(preferences.contains("latest_weather")) {

            String oldWeatherStringData = preferences.getString("latest_weather", "");

            List<Forecast> oldForecasts = transformJson(oldWeatherStringData);
            List<Forecast> newForecasts = transformJson(weatherStringData);

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
                            .setContentText(newForecast.getDescription())
                            .build();

                    notificationManager.notify(123, notification);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        preferences.edit()
                .putString("latest_weather", weatherStringData)
                .putLong("last_update", new Date().getTime()).apply();
    }

    private String loadJson() {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String forecastJsonStr = null;

        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are available at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            URL url = new URL(UrlProvider.provideUrl(this));

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(10000);
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                forecastJsonStr = null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                forecastJsonStr = null;
            }
            forecastJsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e("PlaceholderFragment", "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
            forecastJsonStr = null;
        } finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("PlaceholderFragment", "Error closing stream", e);
                }
            }
        }
        return forecastJsonStr;
    }

    private List<Forecast> transformJson(String responseJson) {
        if (responseJson == null) {
            return null;
        }
        Response response = new Gson().fromJson(responseJson, Response.class);
        List<Forecast> forecasts = new ArrayList<Forecast>();
        for (Response.RForecast rForecast : response.RForecastList) {
            Date date = new Date(rForecast.dt * 1000);
            double dayTemp = rForecast.temp.day;
            double nightTemp = rForecast.temp.night;
            String iconUrl = "http://openweathermap.org/img/w/" + rForecast.weatherList.get(0).icon + ".png";
            String description = rForecast.weatherList.get(0).description;
            forecasts.add(new Forecast(date, description, nightTemp, dayTemp, iconUrl));
        }
        return forecasts;
    }
}
