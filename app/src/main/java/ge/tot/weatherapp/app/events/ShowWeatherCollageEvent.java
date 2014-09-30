package ge.tot.weatherapp.app.events;

import ge.tot.weatherapp.model.Forecast;

/**
 * ShowWeatherCollageEvent
 */
public class ShowWeatherCollageEvent {

    private String collagePath;
    private Forecast forecast;

    public ShowWeatherCollageEvent(String collagePath, Forecast forecast) {
        this.collagePath = collagePath;
        this.forecast = forecast;
    }

    public String getCollagePath() {
        return collagePath;
    }

    public Forecast getForecast() {
        return forecast;
    }
}
