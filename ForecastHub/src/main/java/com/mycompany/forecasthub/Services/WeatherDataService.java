package com.mycompany.forecasthub.Services;

/**
 * The {@code WeatherDataService} class provides methods to fetch current,
 * hourly, and weekly weather forecast data from the Open Meteo API and process
 * it into instances of {@link com.mycompany.forecasthub.Models.WeatherData}.
 *
 * @author AYMAN KHAN
 */
import com.mycompany.forecasthub.Models.WeatherData;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleObjectProperty;
import org.json.JSONArray;
import org.json.JSONObject;

public class WeatherDataService {

    private static WeatherDataService instance;
    private static String baseUrl = "https://api.open-meteo.com/v1/forecast";
    private final SimpleObjectProperty<Boolean> isCelsius = new SimpleObjectProperty<>(true);

    private WeatherDataService() {
    }

    public SimpleObjectProperty<Boolean> getObservableIsCelsius() {
        return isCelsius;
    }

    public Boolean getIsCelsius() {
        return isCelsius.getValue();
    }

    public void setIsCelsius(Boolean newValue) {
        isCelsius.set(newValue);
    }

    /**
     * Gets the singleton instance of the {@code WeatherDataService}.
     *
     * @return The singleton instance of the {@code WeatherDataService}.
     */
    public static WeatherDataService getInstance() {
        if (instance == null) {
            synchronized (WeatherDataService.class) {
                if (instance == null) {
                    instance = new WeatherDataService();
                }
            }
        }
        return instance;
    }

    /**
     * Retrieves the current weather data for a specific location.
     *
     * @param location The location for which the weather data is requested.
     * @param latitude The latitude of the location.
     * @param longitude The longitude of the location.
     * @return A {@link com.mycompany.forecasthub.Models.WeatherData} object
     * representing the current weather, or {@code null} if the data retrieval
     * fails.
     */
    public WeatherData getCurrentWeather(String location, double latitude, double longitude) {
        try {
            String apiUrl = baseUrl + "?latitude=" + latitude + "&longitude=" + longitude
                    + "&current=temperature_2m,relativehumidity_2m,apparent_temperature,precipitation,weathercode,surface_pressure,windspeed_10m&forecast_days=1";

            StringBuilder response = fetchDataRequest(apiUrl);
            if (response != null && response.length() > 0) {
                JSONObject json = new JSONObject(response.toString());
                if (json.has("current")) {
                    JSONObject currentWeatherData = json.getJSONObject("current");

                    double temperature = currentWeatherData.getDouble("temperature_2m");
                    double humidity = currentWeatherData.getDouble("relativehumidity_2m");
                    double precipitation = currentWeatherData.getDouble("precipitation");
                    double windSpeed = currentWeatherData.getDouble("windspeed_10m");
                    double apparentTemperature = currentWeatherData.getDouble("apparent_temperature");
                    double surfaceTemperature = currentWeatherData.getDouble("surface_pressure");
                    int weatherCode = currentWeatherData.getInt("weathercode");

                    WeatherData weatherData = new WeatherData(location, temperature, humidity, precipitation, windSpeed, weatherCode, apparentTemperature, surfaceTemperature);
                    return weatherData;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Retrieves hourly weather forecast data for a specific location.
     *
     * @param location The location for which the weather data is requested.
     * @param latitude The latitude of the location.
     * @param longitude The longitude of the location.
     * @return A list of {@link com.mycompany.forecasthub.Models.WeatherData}
     * objects representing hourly weather data, or an empty list if the data
     * retrieval fails.
     */
    public List<WeatherData> getHourlyForecast(String location, double latitude, double longitude) {

        List<WeatherData> hourlyWeatherDataList = new ArrayList<>();
        try {
            String apiUrl = baseUrl + "?latitude=" + latitude + "&longitude=" + longitude
                    + "&hourly=temperature_2m,weather_code,wind_speed_10m,wind_direction_10m";

            StringBuilder response = fetchDataRequest(apiUrl);
            if (response != null && response.length() > 0) {
                JSONObject json = new JSONObject(response.toString());
                if (json.has("hourly")) {

                    JSONObject hourlyWeatherData = json.getJSONObject("hourly");
                    JSONArray timeArray = hourlyWeatherData.getJSONArray("time");
                    JSONArray temperatureArray = hourlyWeatherData.getJSONArray("temperature_2m");
                    JSONArray weatherCodeArray = hourlyWeatherData.getJSONArray("weather_code");
                    JSONArray windSpeedArray = hourlyWeatherData.getJSONArray("wind_speed_10m");
                    JSONArray windDirectionArray = hourlyWeatherData.getJSONArray("wind_direction_10m");

                    for (int i = 0; i < timeArray.length(); i++) {
                        String time = timeArray.getString(i);
                        double temperature = temperatureArray.getDouble(i);
                        int weatherCode = weatherCodeArray.getInt(i);
                        double windSpeed = windSpeedArray.getDouble(i);
                        int windDirection = windDirectionArray.getInt(i);

                        WeatherData hourlyData = new WeatherData(location, temperature, weatherCode, time, windSpeed, windDirection);
                        hourlyWeatherDataList.add(hourlyData);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hourlyWeatherDataList;
    }

    /**
     * Retrieves weekly weather forecast data for a specific location.
     *
     * @param location The location for which the weather data is requested.
     * @param latitude The latitude of the location.
     * @param longitude The longitude of the location.
     * @return A list of {@link com.mycompany.forecasthub.Models.WeatherData}
     * objects representing weekly weather data, or an empty list if the data
     * retrieval fails.
     */
    public List<WeatherData> getWeeklyForecast(String location, double latitude, double longitude) {

        List<WeatherData> weeklyWeatherDataList = new ArrayList<>();

        try {
            String apiUrl = baseUrl + "?latitude=" + latitude + "&longitude=" + longitude
                    + "&daily=weather_code,temperature_2m_max,sunrise,sunset,uv_index_max&timezone=auto";
            StringBuilder response = fetchDataRequest(apiUrl);
            if (response != null && response.length() > 0) {
                JSONObject json = new JSONObject(response.toString());
                if (json.has("daily")) {

                    JSONObject dailyData = json.getJSONObject("daily");
                    JSONArray timeArray = dailyData.getJSONArray("time");
                    JSONArray weatherCodeArray = dailyData.getJSONArray("weather_code");
                    JSONArray temperatureArray = dailyData.getJSONArray("temperature_2m_max");
                    JSONArray sunriseArray = dailyData.getJSONArray("sunrise");
                    JSONArray sunsetArray = dailyData.getJSONArray("sunset");
                    JSONArray uvIndexMaxArray = dailyData.getJSONArray("uv_index_max");

                    for (int i = 0; i < timeArray.length(); i++) {
                        String date = timeArray.getString(i);
                        int weatherCode = weatherCodeArray.getInt(i);
                        double temperature = temperatureArray.getDouble(i);
                        String sunrise = sunriseArray.getString(i);
                        String sunset = sunsetArray.getString(i);
                        double uvIndexMax = uvIndexMaxArray.optDouble(i, Double.NaN);

                        WeatherData weeklyWeatherData = new WeatherData(location, temperature, weatherCode, date, sunrise, sunset, uvIndexMax);
                        weeklyWeatherDataList.add(weeklyWeatherData);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return weeklyWeatherDataList;
    }

    /**
     * Retrieves the 16-day weather forecast for the specified location using
     * latitude and longitude coordinates.
     *
     * @param location The name of the location for which the forecast is
     * requested.
     * @param latitude The latitude coordinate of the location.
     * @param longitude The longitude coordinate of the location.
     * @return A list containing WeatherData objects representing the 16-day
     * weather forecast. Returns an empty list if the forecast data retrieval
     * fails or encounters an error.
     */
    public List<WeatherData> get16DaysForecast(String location, double latitude, double longitude) {

        List<WeatherData> _16DaysWeatherDataList = new ArrayList<>();

        try {
            String apiUrl = baseUrl + "?latitude=" + latitude + "&longitude=" + longitude
                    + "&daily=weather_code,temperature_2m_max,temperature_2m_min&timezone=auto&forecast_days=16";
            StringBuilder response = fetchDataRequest(apiUrl);
            if (response != null && response.length() > 0) {
                JSONObject json = new JSONObject(response.toString());
                if (json.has("daily")) {
                    JSONObject dailyData = json.getJSONObject("daily");
                    JSONArray timeArray = dailyData.getJSONArray("time");
                    JSONArray weatherCodeArray = dailyData.getJSONArray("weather_code");
                    JSONArray maxTempArray = dailyData.getJSONArray("temperature_2m_max");
                    JSONArray minTempArray = dailyData.getJSONArray("temperature_2m_min");

                    for (int i = 0; i < timeArray.length(); i++) {
                        String date = timeArray.getString(i);
                        int weatherCode = weatherCodeArray.getInt(i);
                        double maxTemp = maxTempArray.getDouble(i);
                        double minTemp = minTempArray.getDouble(i);

                        WeatherData dayWeatherData = new WeatherData(location, maxTemp, weatherCode, minTemp, date);
                        _16DaysWeatherDataList.add(dayWeatherData);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return _16DaysWeatherDataList;
    }

    //HELPER METHODS
    private StringBuilder fetchDataRequest(String apiUrl) throws Exception {

        StringBuilder response = new StringBuilder();
//        System.out.println("Api URL: " + apiUrl); //DEBUGGING

        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
//        System.out.println("Response Code: " + responseCode); //DEBUGGING

        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

//                System.out.println("API Response: " + response); //DEBUGGING
        }

        return response;
    }

//    private String getweatherCode(int weatherCode){
//
//        String weatherCode;
//        switch (weatherCode) {
//            case 0:
//                weatherCode = "Clear sky";
//                break;
//            case 1:
//            case 2:
//            case 3:
//                weatherCode = "Mainly clear, partly cloudy, and overcast";
//                break;
//            case 45:
//            case 48:
//                weatherCode = "Fog and depositing rime fog";
//                break;
//            case 51:
//            case 53:
//            case 55:
//                weatherCode = "Drizzle: Light, moderate, and dense intensity";
//                break;
//            case 56:
//            case 57:
//                weatherCode = "Freezing Drizzle: Light and dense intensity";
//                break;
//            case 61:
//            case 63:
//            case 65:
//                weatherCode = "Rain: Slight, moderate, and heavy intensity";
//                break;
//            case 66:
//            case 67:
//                weatherCode = "Freezing Rain: Light and heavy intensity";
//                break;
//            case 71:
//            case 73:
//            case 75:
//                weatherCode = "Snow fall: Slight, moderate, and heavy intensity";
//                break;
//            case 77:
//                weatherCode = "Snow grains";
//                break;
//            case 80:
//            case 81:
//            case 82:
//                weatherCode = "Rain showers: Slight, moderate, and violent";
//                break;
//            case 85:
//            case 86:
//                weatherCode = "Snow showers slight and heavy";
//                break;
//            case 95:
//                weatherCode = "Thunderstorm: Slight or moderate";
//                break;
//            case 96:
//            case 99:
//                weatherCode = "Thunderstorm with slight and heavy hail";
//                break;
//            default:
//                weatherCode = "Unknown";
//                break;
//        }
//        return weatherCode;
//    }
}
