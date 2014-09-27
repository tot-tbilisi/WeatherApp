/*
 * Copyright (C) 2014 Medlert, Inc.
 */
package ge.tot.weatherapp;

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
