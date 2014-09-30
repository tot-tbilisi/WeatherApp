package ge.tot.weatherapp.app.ui.fragments;


import android.app.Fragment;
import android.app.ListFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ListView;

import java.util.List;

import ge.tot.weatherapp.R;
import ge.tot.weatherapp.app.ui.adapters.WeatherListAdapter;
import ge.tot.weatherapp.di.ServiceProvider;
import ge.tot.weatherapp.app.events.WeatherItemClickedEvent;
import ge.tot.weatherapp.model.Forecast;
import ge.tot.weatherapp.services.WeatherService;
import ge.tot.weatherapp.services.WeatherServiceConverter;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class WeatherListFragment extends ListFragment implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor sensor;

    public class LoadWeatherTask extends AsyncTask<Void, Void, List<ge.tot.weatherapp.model.Forecast>> {

        @Override
        protected List<ge.tot.weatherapp.model.Forecast> doInBackground(Void... params) {
            WeatherService weatherService = ServiceProvider.getInstance().provideWeatherService();

            try {
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String unit = settings.getString("unit", "metric");
                String city = settings.getString("city", "Tbilisi");
                WeatherService.DailyForecastResponse response = weatherService.getDailyForecast(city, unit);
                return WeatherServiceConverter.convertDailyForecastResponse(response);
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<ge.tot.weatherapp.model.Forecast> result) {
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.weather_list, menu);
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
        ServiceProvider.getInstance().provideBus().post(new WeatherItemClickedEvent(forecast));
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
