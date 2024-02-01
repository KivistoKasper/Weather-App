/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.forecasthub.Models;

/**
 *
 * @author abdullahashraf
 */
public class Location {

    private String country;
    private double latitude;
    private double longitude;
    private String timezone;
    private String city;
    public String weatherConditionImage;

    public Location(String city, double latitude, double longitude, String timezone, String weatherConditionImage) {
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timezone = timezone;
        this.weatherConditionImage = weatherConditionImage;
    }

    public Location() {

    }

    // Getters and setters
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
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

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "LocationModel{"
                + "country='" + country + '\''
                + ", latitude=" + latitude
                + ", longitude=" + longitude
                + ", timezone='" + timezone + '\''
                + ", city='" + city + '\''
                + '}';
    }
}
