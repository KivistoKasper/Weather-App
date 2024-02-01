package com.mycompany.forecasthub.Models;

/**
 * The {@code WeatherData} class represents weather information for a specific
 * location. It includes data for current weather, hourly weather, and weekly
 * weather forecasts. Instances of this class are used to store and retrieve
 * weather-related details.
 *
 * @author AYMAN KHAN
 */
public class WeatherData {

    private int windDirection;

    private double temperature;
    private double humidity;
    private double precipitation;
    private double windSpeed;
    private double apparentTemperature;
    private double surfacePressure;
    private double uvIndex;
    private double minTemperature;

    private String location;
    private int weatherCode;
    private String time;
    private String date;
    private String sunrise;
    private String sunset;

    public WeatherData(String location, double temperature, int weatherCode) {
        this.location = location;
        this.temperature = temperature;
        this.weatherCode = weatherCode;
    }

    /**
     * Constructs a {@code WeatherData} object for current weather data.
     *
     * @param location The location for which the weather data is recorded.
     * @param temperature The current temperature in degrees Celsius.
     * @param humidity The current humidity percentage.
     * @param precipitation The amount of precipitation in millimeters.
     * @param windSpeed The current wind speed in kilometers per hour.
     * @param weatherCode The current weather condition.
     * @param apparentTemperature The perceived temperature in degrees Celsius.
     * @param surfacePressure The atmospheric pressure at the surface in hPa.
     */
    public WeatherData(String location, double temperature, double humidity, double precipitation, double windSpeed, int weatherCode, double apparentTemperature, double surfacePressure) {
        this(location, temperature, weatherCode);
        this.humidity = humidity;
        this.precipitation = precipitation;
        this.windSpeed = windSpeed;
        this.weatherCode = weatherCode;
        this.apparentTemperature = apparentTemperature;
        this.surfacePressure = surfacePressure;
    }

    /**
     * Constructs a {@code WeatherData} object for hourly weather data.
     *
     * @param location The location for which the weather data is recorded.
     * @param temperature The temperature in degrees Celsius for the specified
     * hour.
     * @param weatherCode The weather condition for the specified hour.
     * @param time The time for which the weather data is recorded.
     * @param windSpeed The wind speed in kilometers per hour for the specified
     * hour.
     * @param windDirection The wind direction in degrees for the specified
     * hour.
     */
    public WeatherData(String location, double temperature, int weatherCode, String time, double windSpeed, int windDirection) {
        this(location, temperature, weatherCode);
        this.time = time;
        this.windDirection = windDirection;
        this.windSpeed = windSpeed;
    }

    /**
     * Constructs a {@code WeatherData} object for weekly weather data.
     *
     * @param location The location for which the weather data is recorded.
     * @param temperature The temperature in degrees Celsius for the specified
     * day.
     * @param weatherCode The weather condition for the specified day.
     * @param date The date for which the weather data is recorded.
     * @param sunrise The time of sunrise for the specified day.
     * @param sunset The time of sunset for the specified day.
     * @param UVIndex The maximum UV index for the specified day.
     */
    public WeatherData(String location, double temperature, int weatherCode, String date, String sunrise, String sunset, double UVIndex) {
        this(location, temperature, weatherCode);
        this.date = date;
        this.sunrise = sunrise;
        this.sunset = sunset;
        this.uvIndex = UVIndex;
    }

    /**
     * Constructs a WeatherData object representing weather information
     * including minimum temperature and date.
     *
     * @param location The name of the location associated with this weather
     * data.
     * @param temperature The temperature at the location.
     * @param weatherCode The weather code indicating the weather condition.
     * @param minTemperature The minimum temperature forecast for the day.
     * @param date The date for which the weather information is recorded.
     */
    public WeatherData(String location, double temperature, int weatherCode, double minTemperature, String date) {
        this(location, temperature, weatherCode);
        this.minTemperature = minTemperature;
        this.date = date;
    }

    // GETTER METHODS
    public String getLocation() {
        return location;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public double getPrecipitation() {
        return precipitation;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public int getWeatherCode() {
        return weatherCode;
    }

    public double getApparentTemperature() {
        return apparentTemperature;
    }

    public double getSurfacePressure() {
        return surfacePressure;
    }

    public String getTime() {
        return time;
    }

    public int getWindDirection() {
        return windDirection;
    }

    public String getDate() {
        return date;
    }

    public String getSunrise() {
        return sunrise;
    }

    public String getSunset() {
        return sunset;
    }

    public double getUVIndex() {
        return uvIndex;
    }

    public double getMinTemperature() {
        return minTemperature;
    }
}
