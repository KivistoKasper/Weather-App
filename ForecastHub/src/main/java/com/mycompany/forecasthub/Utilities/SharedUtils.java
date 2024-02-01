/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package com.mycompany.forecasthub.Utilities;

/**
 *
 * @author MANSIB
 */
public class SharedUtils {

    public static String getWeatherConditionIcon(String weatherCondition) {
        return "/assets/" + weatherCondition.toLowerCase() + ".png";
    }

    public static String getWeatherConditionFromCode(int weatherCode) {
        String weatherCondition = "Clear sky";
        if (weatherCode == 0) {
            weatherCondition = "Clear sky";
        } else if (weatherCode == 1) {
            weatherCondition = "Mainly clear";
        } else if (weatherCode == 2) {
            weatherCondition = "Partly Cloudy";
        } else if (weatherCode == 3) {
            weatherCondition = "Overcast";
        } else if (weatherCode == 45 || weatherCode == 48) {
            weatherCondition = "Foggy";
        } else if (weatherCode >= 51 && weatherCode <= 70) {
            weatherCondition = "Rainy";
        } else if (weatherCode >= 71 && weatherCode <= 90) {
            weatherCondition = "Snowy";
        } else if (weatherCode >= 91 && weatherCode <= 100) {
            weatherCondition = "Thunderstorm";
        }
        return weatherCondition;
    }

    public static String getParsedTemperature(double value, boolean isCelsius) {
        long roundedValue = Math.round(value); // Rounds to the nearest integer

        if (!isCelsius) {
            long roundedFahrenheit = Math.round((value * 9 / 5) + 32);
            return roundedFahrenheit + "°F";
        }
        return roundedValue + "°C";
    }

    public static int getParsedTemperatureValue(double value, boolean isCelsius) {
        long roundedValue = Math.round(value); // Rounds to the nearest integer

        if (!isCelsius) {
            long roundedFahrenheit = Math.round((value * 9 / 5) + 32);
            return (int) roundedFahrenheit;
        }
        return (int) roundedValue;
    }
}
