package ge.tot.weatherapp.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Forecast
 */
public class Forecast implements Serializable {
    private static final Random RANDOM = new Random(System.currentTimeMillis());
    private final Date date;
    private final String description;
    private final double nightTemp;
    private final double dayTemp;
    private final String iconUrl;

    public static List<Forecast> makeRandom(int daysNumber) {
        Date today = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        List<Forecast> result = new ArrayList<Forecast>();
        for (int i = 0; i < daysNumber; ++i) {
            if (i > 0) {
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }
            Forecast forecast = makeRandom(calendar.getTime());
            result.add(forecast);
        }
        return result;
    }

    public static Forecast makeRandom(Date date) {
        double dayTemp = 15 + RANDOM.nextDouble() * 10.0;
        double nightTemp = 5 + RANDOM.nextDouble() * 5.0;
        return new Forecast(date, "Random", nightTemp, dayTemp, null);
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
        return "Forecast{" +
                "date=" + date +
                ", description='" + description + '\'' +
                ", nightTemp=" + nightTemp +
                ", dayTemp=" + dayTemp +
                ", iconUrl='" + iconUrl + '\'' +
                '}';
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

}
