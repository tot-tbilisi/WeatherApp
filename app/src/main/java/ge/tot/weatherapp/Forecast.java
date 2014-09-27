/*
 * Copyright (C) 2014 Medlert, Inc.
 */
package ge.tot.weatherapp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * Forecast
 */
public class Forecast {
    private final Date date;
    private final String description;
    private final double nightTemp;
    private final double dayTemp;
    private final String iconUrl;

    public static Forecast makeRandom(Date date) {
        Random random = new Random(System.currentTimeMillis());
        double dayTemp = random.nextDouble() * 10.0;
        double nightTemp = random.nextDouble() * 5.0;
        return new Forecast(date, "Random", dayTemp, nightTemp, null);
    }

    public Forecast(Date date, String description, double nightTemp, double dayTemp, String iconUrl) {
        this.date = date;
        this.description = description;
        this.nightTemp = nightTemp;
        this.dayTemp = dayTemp;
        this.iconUrl = iconUrl;
    }

    @Override
    public String toString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM");
        return capitalize(dateFormat.format(date)
                + String.format(" — %.2f°, %.2f°", nightTemp, dayTemp));
    }

    public Date getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public double getNightTemp() {
        return nightTemp;
    }

    public double getDayTemp() {
        return dayTemp;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    private static String capitalize(String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
}
