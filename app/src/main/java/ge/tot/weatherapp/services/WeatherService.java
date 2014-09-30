package ge.tot.weatherapp.services;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.Query;

/**
 * WeatherSerivce
 */
public interface WeatherService {

    public static class BaseResponse {
        @SerializedName("cod")
        private String code;

        public String getCode() {
            return code;
        }
    }

    public static class DailyForecastResponse extends BaseResponse {

        public static class Forecast {

            public static class Temp {
                @SerializedName("day")
                private double day;

                @SerializedName("night")
                private double night;

                public double getDay() {
                    return day;
                }

                public double getNight() {
                    return night;
                }
            }

            public static class Weather {
                @SerializedName("icon")
                private String icon;

                @SerializedName("description")
                private String description;

                public String getIcon() {
                    return icon;
                }

                public String getDescription() {
                    return description;
                }
            }

            @SerializedName("dt")
            private long dt;

            @SerializedName("temp")
            private Temp temp;

            @SerializedName("weather")
            private List<Weather> weatherList;

            public long getDt() {
                return dt;
            }

            public Temp getTemp() {
                return temp;
            }

            public List<Weather> getWeatherList() {
                return weatherList;
            }
        }

        @SerializedName("list")
        private List<Forecast> forecastList;

        public List<Forecast> getForecastList() {
            return forecastList;
        }
    }

    @GET("/forecast/daily?mode=json&cnt=7&lang=en")
    public DailyForecastResponse getDailyForecast(@Query("q") String query, @Query("units") String units);

}
