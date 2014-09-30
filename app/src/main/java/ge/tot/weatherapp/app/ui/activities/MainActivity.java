package ge.tot.weatherapp.app.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.google.android.gms.games.Games;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import ge.tot.weatherapp.R;
import ge.tot.weatherapp.app.events.IncrementAchievementEvent;
import ge.tot.weatherapp.app.events.OpenAchivementsEvent;
import ge.tot.weatherapp.app.events.OpenSettingsEvent;
import ge.tot.weatherapp.app.events.ShowWeatherCollageEvent;
import ge.tot.weatherapp.app.events.UnlockAchievementEvent;
import ge.tot.weatherapp.app.events.WeatherItemClickedEvent;
import ge.tot.weatherapp.app.games.BaseGameActivity;
import ge.tot.weatherapp.app.ui.fragments.CollageFragment;
import ge.tot.weatherapp.app.ui.fragments.ForecastFragment;
import ge.tot.weatherapp.app.ui.fragments.WeatherListFragment;
import ge.tot.weatherapp.di.ServiceProvider;
import hugo.weaving.DebugLog;

public class MainActivity extends BaseGameActivity {

    private static final int REQUEST_CODE_ACHIEVEMENTS = 1;
    private List<Object> delayedGameEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedClients(BaseGameActivity.CLIENT_GAMES | BaseGameActivity.CLIENT_PLUS);
        delayedGameEvents = new ArrayList<Object>();
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new WeatherListFragment(), "weather_list")
//                    .add(new LocationFragment(), "location")
                    .commit();
        }

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
        ServiceProvider.getInstance().provideBus().post(new IncrementAchievementEvent(getString(R.string.achievement_id_master)));
    }

    @Override
    protected void onStop() {
        super.onStop();
        ServiceProvider.getInstance().provideBus().unregister(this);
    }

    @DebugLog
    @Override
    public void onSignInSucceeded() {
        if (delayedGameEvents.size() > 0) {
            for (Object event: delayedGameEvents) {
                handleDelayedGameEvent(event);
            }
        }
    }

    @DebugLog
    @Override
    public void onSignInFailed() {
        // TOOD: handle me
    }

    @DebugLog
    @Subscribe
    public void onOpenSettingsEvent(OpenSettingsEvent event) {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    @DebugLog
    @Subscribe
    public void onOpenAchievementsEvent(OpenAchivementsEvent event) {
        startActivityForResult(Games.Achievements.getAchievementsIntent(getApiClient()), REQUEST_CODE_ACHIEVEMENTS);
    }

    @DebugLog
    @Subscribe
    public void onWeatherItemClicked(WeatherItemClickedEvent event) {
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content,
                        ForecastFragment.newInstance(event.getForecast()),
                        "forecast")
                .addToBackStack("forecast")
                .commit();
    }

    @DebugLog
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

    @DebugLog
    @Subscribe
    public void onUnlockAchievement(UnlockAchievementEvent event) {
        if (isSignedIn()) {
            Games.Achievements.unlock(getApiClient(), event.getAchievementId());
        } else {
            delayedGameEvents.add(event);
        }
    }

    @DebugLog
    @Subscribe
    public void onIncrementAchievement(IncrementAchievementEvent event) {
        if (isSignedIn()) {
            Games.Achievements.increment(getApiClient(), event.getAchievementId(), 1);
        } else {
            delayedGameEvents.add(event);
        }
    }

    private void handleDelayedGameEvent(Object event) {
        if (event instanceof UnlockAchievementEvent) {
            Games.Achievements.unlock(getApiClient(), ((UnlockAchievementEvent) event).getAchievementId());
        } else if (event instanceof IncrementAchievementEvent) {
            Games.Achievements.increment(getApiClient(),((IncrementAchievementEvent) event).getAchievementId(), 1);
        }
    }

}
