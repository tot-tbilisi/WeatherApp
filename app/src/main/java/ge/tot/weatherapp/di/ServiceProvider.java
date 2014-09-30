package ge.tot.weatherapp.di;

import com.google.gson.Gson;
import com.squareup.otto.Bus;

import ge.tot.weatherapp.services.WeatherService;
import retrofit.RestAdapter;

/**
 * ServiceProvider
 */
public class ServiceProvider {
    private static ServiceProvider ourInstance = new ServiceProvider();
    private WeatherService weatherService;
    private Gson gson;
    private Bus bus;

    public static ServiceProvider getInstance() {
        return ourInstance;
    }

    public WeatherService provideWeatherService() {
        return weatherService;
    }

    public Gson provideGson() {
        return gson;
    }

    public Bus provideBus() {
        return bus;
    }

    private ServiceProvider() {
        this.weatherService = buildRealWeatherService();
        this.gson = new Gson();
        this.bus = new Bus();
    }

    private WeatherService buildRealWeatherService() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://api.openweathermap.org/data/2.5")
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();

        return restAdapter.create(WeatherService.class);
    }

}
