package ge.tot.weatherapp.app.events;

import ge.tot.weatherapp.model.Forecast;

/**
 * WeatherItemClickedEvent
 */
public class WeatherItemClickedEvent {
    private final Forecast forecast;

    public WeatherItemClickedEvent(Forecast forecast) {
        this.forecast = forecast;
    }

    public Forecast getForecast() {
        return forecast;
    }
}
