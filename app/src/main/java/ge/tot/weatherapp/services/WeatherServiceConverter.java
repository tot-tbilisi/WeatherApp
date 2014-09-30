package ge.tot.weatherapp.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ge.tot.weatherapp.model.Forecast;

/**
 * WeatherServiceConverter
 */
public class WeatherServiceConverter {

    public static List<Forecast> convertDailyForecastResponse(WeatherService.DailyForecastResponse response) {
        if (response == null) {
            return null;
        }
        List<Forecast> forecasts = new ArrayList<Forecast>();
        for (WeatherService.DailyForecastResponse.Forecast forecast : response.getForecastList()) {
            Date date = new Date(forecast.getDt() * 1000);
            double dayTemp = forecast.getTemp().getDay();
            double nightTemp = forecast.getTemp().getNight();
            String iconUrl = "http://openweathermap.org/img/w/" + forecast.getWeatherList().get(0).getIcon() + ".png";
            String description = forecast.getWeatherList().get(0).getDescription();
            forecasts.add(new Forecast(date, description, nightTemp, dayTemp, iconUrl));
        }
        return forecasts;
    }

    private WeatherServiceConverter() {

    }
}
