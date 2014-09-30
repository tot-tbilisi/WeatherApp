package ge.tot.weatherapp.app.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;

import com.squareup.otto.Subscribe;

import ge.tot.weatherapp.R;
import ge.tot.weatherapp.app.events.ShowWeatherCollageEvent;
import ge.tot.weatherapp.app.events.WeatherItemClickedEvent;
import ge.tot.weatherapp.app.ui.fragments.CollageFragment;
import ge.tot.weatherapp.app.ui.fragments.ForecastFragment;
import ge.tot.weatherapp.app.ui.fragments.LocationFragment;
import ge.tot.weatherapp.app.ui.fragments.WeatherListFragment;
import ge.tot.weatherapp.di.ServiceProvider;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new WeatherListFragment(), "weather_list")
                    .add(new LocationFragment(), "location")
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String city = prefs.getString("city", "Tbilisi");
        setTitle("Weather in " + city);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ServiceProvider.getInstance().provideBus().register(this);
    }

    @Subscribe
     public void onWeatherItemClicked(WeatherItemClickedEvent event) {
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content,
                        ForecastFragment.newInstance(event.getForecast()),
                        "forecast")
                .addToBackStack("forecast")
                .commit();
    }

    @Subscribe
    public void onShowWeatherCollage(ShowWeatherCollageEvent event) {
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content,
                        new CollageFragment.Builder()
                                .picImagePath(event.getCollagePath())
                                .forecast(event.getForecast())
                                .build(),
                        "weather_collage")
                .addToBackStack("weather_collage")
                .commit();
    }

    @Override
    protected void onStop() {
        super.onStop();
        ServiceProvider.getInstance().provideBus().unregister(this);
    }

}
