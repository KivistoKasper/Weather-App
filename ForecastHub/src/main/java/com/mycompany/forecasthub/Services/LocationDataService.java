/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.forecasthub.Services;

import com.mycompany.forecasthub.Models.Location;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleObjectProperty;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author abdullahashraf
 */
public class LocationDataService {

    private static final Properties properties = new Properties();
    private final String IPINFO_TOKEN;
    private final String COUNTRIES_AND_CITIES_API;
    private final String OPENWEATHERMAP_API_KEY;

    private final SimpleObjectProperty<Location> activeLocation = new SimpleObjectProperty<>();

    private static LocationDataService instance = new LocationDataService();

    public SimpleObjectProperty<Location> getObservableActiveLocation() {
        return activeLocation;
    }

    public void setActiveLocation(Location location) {
        activeLocation.set(location);
    }

    private LocationDataService() {
        loadProperties();
        this.IPINFO_TOKEN = getProperty("IPINFO_TOKEN");
        this.COUNTRIES_AND_CITIES_API = getProperty("COUNTRIES_AND_CITIES_API");
        this.OPENWEATHERMAP_API_KEY = getProperty("OPENWEATHERMAP_API_KEY");
        System.out.println("LocationDataService instance created.");
    }

    private void loadProperties() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.err.println("Unable to find config.properties file.");
                return;
            }
            properties.load(input);
        } catch (IOException ex) {
            System.err.println("Error loading config.properties file: " + ex.getMessage());
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public static LocationDataService getInstance() {
        if (instance == null) {
            instance = new LocationDataService();
        }
        return instance;
    }

    public Location getCurrentLocation() {
        try {
            URL url = new URL("https://ipinfo.io/json?token=" + IPINFO_TOKEN);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                throw new RuntimeException("HttpResponseCode: " + responseCode);
            }

            String inline = "";
            Scanner sc = new Scanner(url.openStream());
            while (sc.hasNext()) {
                inline += sc.nextLine();
            }
            sc.close();

            JSONObject response = new JSONObject(inline);

            Location location = new Location();
            location.setCountry(response.getString("country"));

            String[] loc = response.getString("loc").split(",");
            location.setLatitude(Double.parseDouble(loc[0]));
            location.setLongitude(Double.parseDouble(loc[1]));

            location.setTimezone(response.getString("timezone"));

            location.setCity(response.getString("city"));  // Extracting the city from the JSON response
            activeLocation.set(location);

            return location;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private void fetchLatLong(String city, String country, CityWeatherInfo cityWeatherInfo) throws Exception {
        String apiUrl = "http://api.openweathermap.org/geo/1.0/direct?q=" + city + "," + country + "&limit=1&appid=" + OPENWEATHERMAP_API_KEY;

        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException("HttpResponseCode: " + responseCode);
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String response = reader.lines().collect(Collectors.joining());
        reader.close();

        JSONArray jsonResponse = new JSONArray(response);
        if (jsonResponse.length() > 0) {
            JSONObject cityData = jsonResponse.getJSONObject(0);
            if (cityData.has("lat") && cityData.has("lon")) {
                double latitude = cityData.getDouble("lat");
                double longitude = cityData.getDouble("lon");
                cityWeatherInfo.setLatitude(latitude);
                cityWeatherInfo.setLongitude(longitude);
            } else {
                System.err.println("Invalid data for " + city + ", " + country);
                // If data is invalid, set latitude and longitude to 0.0
                cityWeatherInfo.setLatitude(0.0);
                cityWeatherInfo.setLongitude(0.0);
            }
        } else {
            System.err.println("Latitude and longitude data not found for " + city + ", " + country);
            // If data is not found, set latitude and longitude to 0.0
            cityWeatherInfo.setLatitude(0.0);
            cityWeatherInfo.setLongitude(0.0);
        }
    }

    public List<CityWeatherInfo> getCitiesWithLatLong() throws Exception {
        List<CityWeatherInfo> citiesLatLongInfo = new ArrayList<>();

        // Fetching countries and their cities
        URL url = new URL(COUNTRIES_AND_CITIES_API);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod(
                "GET");
        conn.connect();

        int responseCode = conn.getResponseCode();
        if (responseCode
                != 200) {
            throw new RuntimeException("HttpResponseCode: " + responseCode);
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String response = reader.lines().collect(Collectors.joining());

        reader.close();

        JSONObject jsonResponse = new JSONObject(response);
        JSONArray countriesData = jsonResponse.getJSONArray("data");

        // Fetch latitude and longitude for cities in Finland
        for (int i = 0;
                i < countriesData.length();
                i++) {
            JSONObject countryInfo = countriesData.getJSONObject(i);
            if (countryInfo.getString("country").equals("Finland")) {
                JSONArray cities = countryInfo.getJSONArray("cities");
                for (int j = 0; j < cities.length(); j++) {
                    String city = cities.getString(j);
                    CityWeatherInfo cityWeatherInfo = new CityWeatherInfo("Finland", city, 0.0, 0.0);
                    fetchLatLong(city, "FI", cityWeatherInfo);
                    citiesLatLongInfo.add(cityWeatherInfo);
                }
                break; // Exit loop after processing Finland cities
            }
        }
        return citiesLatLongInfo;
    }

    public static class CityWeatherInfo {

        private String country;
        private String city;
        private double latitude;
        private double longitude;
        private String timeZone;
        public int weatherCode;

        public CityWeatherInfo(String country, String city, double latitude, double longitude) {
            this.country = country;
            this.city = city;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public CityWeatherInfo(String country, String city, double latitude, double longitude, String timeZone) {
            this.country = country;
            this.city = city;
            this.latitude = latitude;
            this.longitude = longitude;
            this.timeZone = timeZone;
        }

        // Getters and setters for country, city, latitude, longitude
        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public String getTimeZone() {
            return timeZone;
        }

        public void setTimeZone(String timeZone) {
            this.timeZone = timeZone;
        }

    }

    public List<CityWeatherInfo> searchCityOptions(String query) {
        try {
            List<CityWeatherInfo> options = new ArrayList<>();

            // Encode the query to ensure special characters are handled properly in the URL
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String apiUrl = "http://api.openweathermap.org/geo/1.0/direct?q=" + encodedQuery + "&limit=1&appid=" + OPENWEATHERMAP_API_KEY;

            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                throw new RuntimeException("HttpResponseCode: " + responseCode);
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String response = reader.lines().collect(Collectors.joining());
            reader.close();

            JSONArray jsonResponse = new JSONArray(response);
            for (int i = 0; i < jsonResponse.length(); i++) {
                JSONObject cityData = jsonResponse.getJSONObject(i);
                if (cityData.has("name") && cityData.has("lat") && cityData.has("lon")) {
                    String cityName = cityData.getString("name");
                    double latitude = cityData.getDouble("lat");
                    double longitude = cityData.getDouble("lon");
                    String timeZone = getTimeZoneForCoordinates(latitude, longitude);
                    options.add(new CityWeatherInfo("", cityName, latitude, longitude, timeZone));
                }
            }

            return options;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getTimeZoneForCoordinates(double latitude, double longitude) {
        String apiKey = "AIzaSyCYJnHtESyNojDfQ6ZwRc4EOZfP2MZg_iI"; // Replace with your Google Maps API Key
        String apiUrl = "https://maps.googleapis.com/maps/api/timezone/json?location="
                + latitude + "," + longitude + "&timestamp=" + (System.currentTimeMillis() / 1000)
                + "&key=" + apiKey;

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String response = reader.lines().collect(Collectors.joining());
                reader.close();

                JSONObject jsonResponse = new JSONObject(response);
                if (jsonResponse.has("timeZoneId")) {
                    return jsonResponse.getString("timeZoneId");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
