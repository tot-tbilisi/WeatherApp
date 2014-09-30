package ge.tot.weatherapp.ui;


import android.app.Fragment;
import android.app.ListFragment;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ge.tot.weatherapp.UrlProvider;
import ge.tot.weatherapp.model.Forecast;
import ge.tot.weatherapp.otto.BusProvider;
import ge.tot.weatherapp.otto.WeatherItemClickedEvent;
import ge.tot.weatherapp.protocol.Response;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class WeatherListFragment extends ListFragment implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor sensor;

    @Override
    public void onSensorChanged(SensorEvent event) {
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public class LoadWeatherTask extends AsyncTask<Void, Void, List<Forecast>> {

        @Override
        protected List<Forecast> doInBackground(Void... params) {
            String responseJson = loadJson();
            return transformJson(responseJson);
        }

        @Override
        protected void onPostExecute(List<Forecast> result) {
            super.onPostExecute(result);
            if (result == null) {
                setListAdapter(null);
                setEmptyText("Unexpected error. Please check your network connection");
            } else {
                WeatherListAdapter adapter = new WeatherListAdapter(getActivity(), result);
                setListAdapter(adapter);
                setListShown(true);
            }
        }
    }

    public WeatherListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        new LoadWeatherTask().execute();

        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);

        if(sensor != null) {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(sensor != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Forecast forecast = (Forecast) getListAdapter().getItem(position);
        BusProvider.getBus().post(new WeatherItemClickedEvent(forecast));
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
            URL url = new URL(UrlProvider.provideUrl(getActivity()));

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
        ((MyActivity)getActivity()).setDayTemp(String.valueOf(forecasts.get(0).getDayTemp()));
        return forecasts;
    }
}
